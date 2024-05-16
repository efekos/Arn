package dev.efekos.arn;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.efekos.arn.annotation.Command;
import dev.efekos.arn.annotation.CommandAnnotationData;
import dev.efekos.arn.annotation.Container;
import dev.efekos.arn.annotation.RestCommand;
import dev.efekos.arn.config.ArnConfigurer;
import dev.efekos.arn.exception.ArnCommandException;
import dev.efekos.arn.exception.ArnConfigurerException;
import dev.efekos.arn.exception.ArnContainerException;
import dev.efekos.arn.handler.CommandHandlerMethod;
import dev.efekos.arn.resolver.CommandArgumentResolver;
import dev.efekos.arn.resolver.CommandHandlerMethodArgumentResolver;
import dev.efekos.arn.resolver.impl.*;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.network.chat.IChatBaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.entity.Player;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public final class Arn {

    private static final Arn instance = new Arn();
    private final List<CommandHandlerMethodArgumentResolver> handlerMethodArgumentResolvers = new ArrayList<>();
    private final List<CommandArgumentResolver> commandArgumentResolvers = new ArrayList<>();
    private final Map<String, CommandHandlerMethod> handlers = new HashMap<>();
    private final Map<String, Object> containerInstanceMap = new HashMap<>();

    public static void run(Class<?> mainClass) {
        Reflections reflections = new Reflections(mainClass.getPackage().getName());

        try {
            instance.configure();
            instance.scanConfigurers(reflections);
            instance.createContainerInstances(reflections);
            instance.scanCommands(reflections);
            instance.registerCommands();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createContainerInstances(Reflections reflections) throws ArnContainerException {
        for (Class<?> clazz : reflections.getTypesAnnotatedWith(Container.class)) {
            if (clazz.isInterface()||clazz.isAnnotation()||clazz.isEnum()) return;

            try {
                Constructor<?> ctor = clazz.getConstructor();
                ctor.setAccessible(true);
                Object o = ctor.newInstance();
                containerInstanceMap.put(clazz.getName(),o);
            } catch (Exception e) {
                throw new ArnContainerException("There was an error while trying to instantiate "+clazz.getName()+".",e);
            }
        }
    }

    private void configure() {
        handlerMethodArgumentResolvers.add(new CommandHandlerMethodIntArgumentResolver());
        handlerMethodArgumentResolvers.add(new CommandHandlerMethodStringArgumentResolver());
        handlerMethodArgumentResolvers.add(new CommandHandlerMethodPlayerSenderResolver());

        commandArgumentResolvers.add(new CommandIntArgumentResolver());
        commandArgumentResolvers.add(new CommandStringArgumentResolver());
        commandArgumentResolvers.add(new CommandSenderArgumentResolver());
    }

    private void scanConfigurers(Reflections reflections) throws ArnConfigurerException {
        Object[] configurers = reflections.getTypesAnnotatedWith(Container.class).stream().filter(aClass -> Arrays.asList(aClass.getInterfaces()).contains(ArnConfigurer.class)).toArray();

        for (Object configurer : configurers) {
            Class<? extends ArnConfigurer> clazz = (Class<? extends ArnConfigurer>) configurer;

            try {
                Constructor<? extends ArnConfigurer> constructor = clazz.getConstructor();
                constructor.setAccessible(true);
                ArnConfigurer configurerInstance = constructor.newInstance();
                ArrayList<CommandHandlerMethodArgumentResolver> list = new ArrayList<>();
                configurerInstance.addHandlerMethodArgumentResolvers(list);
                configurerInstance.addArgumentResolvers(commandArgumentResolvers);
                handlerMethodArgumentResolvers.addAll(list);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException e) {
                throw new ArnConfigurerException(e);
            }
        }
    }

    private void scanCommands(Reflections reflections) throws ArnCommandException {
        Set<Class<?>> containers = reflections.getTypesAnnotatedWith(Container.class);
        Set<Class<?>> restCommands = reflections.getTypesAnnotatedWith(RestCommand.class);

        for (Class<?> container : containers) {

            if (container.isAnnotationPresent(RestCommand.class)) continue;

            for (Method method : container.getMethods()) {

                if (method.isAnnotationPresent(Command.class))
                    instance.command(method.getAnnotation(Command.class), method);

            }
        }

        for (Class<?> restCommand : restCommands) {

            for (Method method : restCommand.getMethods()) {

                if (method.isAnnotationPresent(Command.class))
                    instance.restCommand(method.getAnnotation(Command.class), method, restCommand);

            }

        }
    }

    private void restCommand(Command annotation, Method method, Class<?> restCommand) throws ArnCommandException {
        //TODO
    }

    private static final List<Class<? extends CommandSender>> REQUIRED_SENDER_CLASSES = Arrays.asList(CommandSender.class, Player.class, ConsoleCommandSender.class, BlockCommandSender.class);

    private void command(Command annotation, Method method) throws ArnCommandException {
        if (handlers.containsKey(annotation.value()))
            throw new ArnCommandException("Duplicate command '" + annotation.value() + "'");
        if (!method.getReturnType().equals(int.class))
            throw new ArnCommandException("Handler method '" + method.getName() + "' for command '" + annotation.value() + "' does not return 'int'");
        long count = Arrays.stream(method.getParameters()).filter(parameter -> REQUIRED_SENDER_CLASSES.contains(parameter.getType())).count();
        if (count != 1)
            throw new ArnCommandException("Handler method '" + method.getName() + "' for command '" + annotation.value() + "' must contain exactly one parameter that is a CommandSender.");
        for (Parameter parameter : method.getParameters()) {
            if (handlerMethodArgumentResolvers.stream().noneMatch(car -> car.isApplicable(parameter)))
                throw new ArnCommandException("Handler method '" + method.getName() + "' for command '" + annotation.value() + "' has a parameter '" + parameter.getName() + "' that isn't applicable for anything.");
            if (commandArgumentResolvers.stream().noneMatch(car -> car.isApplicable(parameter)))
                throw new ArnCommandException("Handler method '" + method.getName() + "' for command '" + annotation.value() + "' has a parameter '" + parameter.getName() + "' that isn't applicable for anything.");
        }

        CommandHandlerMethod commandHandlerMethod = createHandlerMethod(annotation, method);

        handlers.put(annotation.value(), commandHandlerMethod);
    }

    private CommandHandlerMethod createHandlerMethod(Command annotation, Method method) {
        CommandHandlerMethod commandHandlerMethod = new CommandHandlerMethod();

        commandHandlerMethod.setCommand(annotation.value());
        commandHandlerMethod.setMethod(method);
        commandHandlerMethod.setParameters(Arrays.asList(method.getParameters()));

        CommandAnnotationData baseAnnData = new CommandAnnotationData(annotation);

        if (baseAnnData.getDescription() == null) baseAnnData.setDescription("No description provided.");
        if (baseAnnData.getPermission() == null) baseAnnData.setPermission("spigot.command." + annotation.value());

        commandHandlerMethod.setAnnotationData(baseAnnData);

        ArrayList<CommandArgumentResolver> argumentResolvers = new ArrayList<>();
        ArrayList<CommandHandlerMethodArgumentResolver> handlerMethodResolvers = new ArrayList<>();
        for (int i = 0; i < method.getParameters().length; i++) {
            Parameter parameter = method.getParameters()[i];

            argumentResolvers.add(this.commandArgumentResolvers.stream().filter(resolver -> resolver.isApplicable(parameter)).findFirst().get());
            handlerMethodResolvers.add(this.handlerMethodArgumentResolvers.stream().filter(resolver -> resolver.isApplicable(parameter)).findFirst().get());
        }

        commandHandlerMethod.setArgumentResolvers(argumentResolvers);
        commandHandlerMethod.setHandlerMethodResolvers(handlerMethodResolvers);

        return commandHandlerMethod;
    }

    private void registerCommands() {
        CommandDispatcher<CommandListenerWrapper> dispatcher = ((CraftServer) Bukkit.getServer()).getHandle().c().aE().a();

        System.out.println("sdrkgmdskfg");
        dispatcher.register(net.minecraft.commands.CommandDispatcher.a("test").then(net.minecraft.commands.CommandDispatcher.a("cool").executes(commandContext -> {
            commandContext.getSource().a(IChatBaseComponent.b("Hello terry"));
            return 0;
        })));

        for (CommandHandlerMethod method : handlers.values()) {
            List<ArgumentBuilder> nodes = new ArrayList<>();
            nodes.add(net.minecraft.commands.CommandDispatcher.a(method.getCommand()));

            for (int i = 0; i < method.getArgumentResolvers().size(); i++) {
                CommandArgumentResolver resolver = method.getArgumentResolvers().get(i);
                ArgumentBuilder builder = resolver.apply(method.getParameters().get(i));
                if (builder != null) nodes.add(builder);
            }

            //TODO: find a way to connect nodes

            com.mojang.brigadier.Command<CommandListenerWrapper> lambda = commandContext -> {

                List<Object> objects = new ArrayList<>();

                for (int i = 0; i < method.getHandlerMethodResolvers().size(); i++) {
                    CommandHandlerMethodArgumentResolver resolver = method.getHandlerMethodResolvers().get(i);
                    objects.add(resolver.resolve(method.getParameters().get(i), method, commandContext));
                }

                Method actualMethodToInvoke = method.getMethod();

                try {
                    actualMethodToInvoke.setAccessible(true);
                    return (int) actualMethodToInvoke.invoke(containerInstanceMap.get(method.getMethod().getDeclaringClass().getName()),objects.toArray());
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }


            };

            LiteralArgumentBuilder<CommandListenerWrapper> builder = (LiteralArgumentBuilder<CommandListenerWrapper>) chainArgumentBuilders(nodes, lambda);

            dispatcher.register(builder);

        }
    }

    public static ArgumentBuilder<?, ?> chainArgumentBuilders(List<ArgumentBuilder> nodes, com.mojang.brigadier.Command<CommandListenerWrapper> executes) {
        // Base case: If there are no nodes, return null
        if (nodes.isEmpty()) {
            return null;
        }

        // Start chaining from the last node
        ArgumentBuilder chainedBuilder = nodes.get(nodes.size() - 1).executes(executes);

        // Chain the nodes in reverse order
        for (int i = nodes.size() - 2; i >= 0; i--) {
            chainedBuilder = nodes.get(i).then(chainedBuilder);
        }

        return chainedBuilder;
    }

}