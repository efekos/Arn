package dev.efekos.arn;

import com.mojang.brigadier.CommandDispatcher;
import dev.efekos.arn.annotation.Command;
import dev.efekos.arn.annotation.CommandAnnotationData;
import dev.efekos.arn.annotation.Container;
import dev.efekos.arn.annotation.RestCommand;
import dev.efekos.arn.config.ArnConfigurer;
import dev.efekos.arn.exception.ArnCommandException;
import dev.efekos.arn.exception.ArnConfigurerException;
import dev.efekos.arn.handler.CommandHandlerMethod;
import dev.efekos.arn.resolver.CommandHandlerMethodArgumentResolver;
import dev.efekos.arn.resolver.CommandHandlerMethodIntArgumentResolver;
import dev.efekos.arn.resolver.CommandHandlerMethodStringArgumentResolver;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.server.MinecraftServer;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public final class Arn {

    private static final Arn instance = new Arn();
    private final List<CommandHandlerMethodArgumentResolver> resolvers = new ArrayList<>();
    private final Map<String,CommandHandlerMethod> handlers = new HashMap<>();

    public static void run(Class<?> mainClass) {
        try {
            instance.configure();
            instance.scanConfigurers(mainClass);
            instance.scanCommands(mainClass);
            instance.registerCommands();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void configure(){
        resolvers.add(new CommandHandlerMethodIntArgumentResolver());
        resolvers.add(new CommandHandlerMethodStringArgumentResolver());
    }

    private void scanConfigurers(Class<?> mainClass) throws ArnConfigurerException {
        Reflections reflections = new Reflections(mainClass.getPackage().getName());

        Object[] configurers = reflections.getTypesAnnotatedWith(Container.class).stream().filter(aClass -> Arrays.asList(aClass.getInterfaces()).contains(ArnConfigurer.class)).toArray();

        for (Object configurer : configurers) {
            Class<? extends ArnConfigurer> clazz = (Class<? extends ArnConfigurer>) configurer;

            try {
                Constructor<? extends ArnConfigurer> constructor = clazz.getConstructor();
                constructor.setAccessible(true);
                ArnConfigurer configurerInstance = constructor.newInstance();
                ArrayList<CommandHandlerMethodArgumentResolver> list = new ArrayList<>();
                configurerInstance.addArgumentResolvers(list);
                resolvers.addAll(list);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException e) {
                throw new ArnConfigurerException(e);
            }
        }
    }

    private void scanCommands(Class<?> mainClass) throws ArnCommandException {
        Reflections reflections = new Reflections(mainClass.getPackage().getName());

        Set<Class<?>> containers = reflections.getTypesAnnotatedWith(Container.class);
        Set<Class<?>> restCommands = reflections.getTypesAnnotatedWith(RestCommand.class);

        for (Class<?> container : containers) {

            if(container.isAnnotationPresent(RestCommand.class)) continue;

            for (Method method : container.getMethods()) {

                if(method.isAnnotationPresent(Command.class)) instance.command(method.getAnnotation(Command.class),method);

            }
        }

        for (Class<?> restCommand : restCommands) {

            for (Method method : restCommand.getMethods()) {

                if(method.isAnnotationPresent(Command.class)) instance.restCommand(method.getAnnotation(Command.class),method,restCommand);

            }

        }
    }

    private void restCommand(Command annotation, Method method, Class<?> restCommand) throws ArnCommandException {
        //TODO
    }

    private static final List<Class<? extends CommandSender>> REQUIRED_SENDER_CLASSES = Arrays.asList(CommandSender.class, Player.class, ConsoleCommandSender.class, BlockCommandSender.class);

    private void command(Command annotation,Method method) throws ArnCommandException {
        if(handlers.containsKey(annotation.value())) throw new ArnCommandException("Duplicate command '" + annotation.value() + "'");
        if(!method.getReturnType().equals(int.class)) throw new ArnCommandException("Handler method '"+ method.getName() + "' for command '" + annotation.value() + "' does not return 'int'");
        long count = Arrays.stream(method.getParameters()).filter(parameter -> REQUIRED_SENDER_CLASSES.contains(parameter.getType())).count();
        if(count!=1) throw new ArnCommandException("Handler method '"+ method.getName() + "' for command '" + annotation.value() + "' must contain exactly one parameter that is a CommandSender.");
        for (Parameter parameter : method.getParameters()) {
            if(resolvers.stream().noneMatch(car -> car.isApplicable(parameter))) throw new ArnCommandException("Handler method '"+ method.getName() + "' for command '" + annotation.value() + "' has a parameter '"+parameter.getName()+"' that isn't applicable for anything.");
        }

        CommandHandlerMethod commandHandlerMethod = createHandlerMethod(annotation, method);

        handlers.put(annotation.value(), commandHandlerMethod);
    }

    private static CommandHandlerMethod createHandlerMethod(Command annotation, Method method) {
        CommandHandlerMethod commandHandlerMethod = new CommandHandlerMethod();

        commandHandlerMethod.setCommand(annotation.value());
        commandHandlerMethod.setMethod(method);
        commandHandlerMethod.setParameters(Arrays.asList(method.getParameters()));

        CommandAnnotationData baseAnnData = new CommandAnnotationData(annotation);

        if(baseAnnData.getDescription()==null) baseAnnData.setDescription("No description provided.");
        if(baseAnnData.getPermission()==null) baseAnnData.setPermission("spigot.command."+ annotation.value());

        commandHandlerMethod.setAnnotationData(baseAnnData);
        return commandHandlerMethod;
    }

    private void registerCommands(){
        CommandDispatcher<CommandListenerWrapper> dispatcher = MinecraftServer.getServer().aE().a();

        //TODO
    }

}