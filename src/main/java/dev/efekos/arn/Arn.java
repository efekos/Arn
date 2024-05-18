package dev.efekos.arn;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.efekos.arn.annotation.*;
import dev.efekos.arn.config.ArnConfigurer;
import dev.efekos.arn.data.CommandAnnotationData;
import dev.efekos.arn.data.CommandAnnotationLiteral;
import dev.efekos.arn.exception.ArnCommandException;
import dev.efekos.arn.exception.ArnConfigurerException;
import dev.efekos.arn.exception.ArnContainerException;
import dev.efekos.arn.handler.CommandHandlerMethod;
import dev.efekos.arn.resolver.CommandArgumentResolver;
import dev.efekos.arn.resolver.CommandHandlerMethodArgumentResolver;
import dev.efekos.arn.resolver.impl.command.*;
import dev.efekos.arn.resolver.impl.handler.*;
import net.minecraft.commands.CommandListenerWrapper;
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
    private final List<CommandHandlerMethod> handlers = new ArrayList<>();
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
            if (clazz.isInterface() || clazz.isAnnotation() || clazz.isEnum()) return;

            try {
                Constructor<?> ctor = clazz.getConstructor();
                ctor.setAccessible(true);
                Object o = ctor.newInstance();
                containerInstanceMap.put(clazz.getName(), o);
            } catch (Exception e) {
                throw new ArnContainerException("There was an error while trying to instantiate " + clazz.getName() + ".", e);
            }
        }
    }

    private void configure() {
        handlerMethodArgumentResolvers.add(new HndIntArg());
        handlerMethodArgumentResolvers.add(new HndStringArg());
        handlerMethodArgumentResolvers.add(new HndPlayerSender());
        handlerMethodArgumentResolvers.add(new HndBooleanArg());
        handlerMethodArgumentResolvers.add(new HndDoubleArg());
        handlerMethodArgumentResolvers.add(new HndLocationArg());
        handlerMethodArgumentResolvers.add(new HndLongArg());
        handlerMethodArgumentResolvers.add(new HndTextArg());
        handlerMethodArgumentResolvers.add(new HndEffectTypeArg());
        handlerMethodArgumentResolvers.add(new HndGameModeArg());
        handlerMethodArgumentResolvers.add(new HndPlayerArg());
        handlerMethodArgumentResolvers.add(new HndMultiplePlayerArg());
        handlerMethodArgumentResolvers.add(new HndDimensionArg());
        handlerMethodArgumentResolvers.add(new HndEntityArg());
        handlerMethodArgumentResolvers.add(new HndMultipleEntityArg());
        handlerMethodArgumentResolvers.add(new HndFloatArg());
        handlerMethodArgumentResolvers.add(new HndEnchantmentArg());
        handlerMethodArgumentResolvers.add(new HndItemArg());
        handlerMethodArgumentResolvers.add(new HndBlockArg());
        handlerMethodArgumentResolvers.add(new HndItemStackArg());
        handlerMethodArgumentResolvers.add(new HndBlockStateArg());

        commandArgumentResolvers.add(new CmdBooleanArg());
        commandArgumentResolvers.add(new CmdDoubleArg());
        commandArgumentResolvers.add(new CmdLocationArg());
        commandArgumentResolvers.add(new CmdLongArg());
        commandArgumentResolvers.add(new CmdIntArg());
        commandArgumentResolvers.add(new CmdStringArg());
        commandArgumentResolvers.add(new CmdTextArg());
        commandArgumentResolvers.add(new CmdEffectTypeArg());
        commandArgumentResolvers.add(new CmdGameModeArg());
        commandArgumentResolvers.add(new CmdPlayerArg());
        commandArgumentResolvers.add(new CmdMultiplePlayerArg());
        commandArgumentResolvers.add(new CmdDimensionArg());
        commandArgumentResolvers.add(new CmdEntityArg());
        commandArgumentResolvers.add(new CmdMultipleEntityArg());
        commandArgumentResolvers.add(new CmdFloatArg());
        commandArgumentResolvers.add(new CmdEnchantmentArg());
        commandArgumentResolvers.add(new CmdItemArg());
        commandArgumentResolvers.add(new CmdBlockArg());
        commandArgumentResolvers.add(new CmdItemStackArg());
        commandArgumentResolvers.add(new CmdBlockStateArg());
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
        // Errors
        if (!method.getReturnType().equals(int.class))
            throw new ArnCommandException("Handler method '" + method.getName() + "' for command '" + annotation.value() + "' does not return 'int'");
        long count = Arrays.stream(method.getParameters()).filter(parameter -> REQUIRED_SENDER_CLASSES.contains(parameter.getType()) && !parameter.isAnnotationPresent(CommandArgument.class)).count();
        if (count != 1)
            throw new ArnCommandException("Handler method '" + method.getName() + "' for command '" + annotation.value() + "' must contain exactly one parameter that is a CommandSender.");
        for (Parameter parameter : method.getParameters()) {
            if (handlerMethodArgumentResolvers.stream().noneMatch(car -> car.isApplicable(parameter)))
                throw new ArnCommandException("Handler method '" + method.getName() + "' for command '" + annotation.value() + "' has a parameter '" + parameter.getName() + "' that isn't applicable for anything.");
            if (handlerMethodArgumentResolvers.stream().anyMatch(car -> car.isApplicable(parameter) && car.requireCommandArgument()) && commandArgumentResolvers.stream().noneMatch(car -> car.isApplicable(parameter)))
                throw new ArnCommandException("Handler method '" + method.getName() + "' for command '" + annotation.value() + "' has a parameter '" + parameter.getName() + "' that isn't applicable for anything.");
        }

        CommandHandlerMethod commandHandlerMethod = createHandlerMethod(annotation, method);

        if (handlers.stream().anyMatch(method1 -> commandHandlerMethod.getSignature().equals(method1.getSignature())))
            throw new ArnCommandException("Duplicate command '" + commandHandlerMethod.getSignature() + "'");
        handlers.add(commandHandlerMethod);
    }

    private CommandHandlerMethod createHandlerMethod(Command annotation, Method method) {
        CommandHandlerMethod commandHandlerMethod = new CommandHandlerMethod();
        StringBuilder signatureBuilder = new StringBuilder();

        signatureBuilder.append(annotation.value()).append(" ");

        commandHandlerMethod.setCommand(annotation.value());
        commandHandlerMethod.setMethod(method);
        commandHandlerMethod.setParameters(Arrays.asList(method.getParameters()));

        CommandAnnotationData baseAnnData = new CommandAnnotationData(annotation);

        if (baseAnnData.getDescription().isEmpty()) baseAnnData.setDescription("No description provided.");

        ArrayList<CommandAnnotationLiteral> literals = new ArrayList<>();
        for (String s : annotation.value().split("\\" + CommandAnnotationLiteral.SEPARATOR_CHAR_STRING))
            literals.add(CommandAnnotationLiteral.parse(s));

        baseAnnData.setLiterals(literals);

        commandHandlerMethod.setAnnotationData(baseAnnData);

        ArrayList<CommandArgumentResolver> argumentResolvers = new ArrayList<>();
        ArrayList<CommandHandlerMethodArgumentResolver> handlerMethodResolvers = new ArrayList<>();
        signatureBuilder.append("(");
        for (int i = 0; i < method.getParameters().length; i++) {
            Parameter parameter = method.getParameters()[i];

            if (i != 0) signatureBuilder.append(",");
            signatureBuilder.append(parameter.getType().getName());
            CommandHandlerMethodArgumentResolver handlerMethodArgumentResolver = this.handlerMethodArgumentResolvers.stream().filter(resolver -> resolver.isApplicable(parameter)).findFirst().get();
            handlerMethodResolvers.add(handlerMethodArgumentResolver);
            System.out.println(handlerMethodArgumentResolver);
            System.out.println(handlerMethodArgumentResolver.requireCommandArgument());
            System.out.println(this.commandArgumentResolvers.stream().filter(resolver -> resolver.isApplicable(parameter)).findFirst().orElse(null));
            if (handlerMethodArgumentResolver.requireCommandArgument()) argumentResolvers.add(this.commandArgumentResolvers.stream().filter(resolver -> resolver.isApplicable(parameter)).findFirst().get());
            else argumentResolvers.add(null);
        }
        signatureBuilder.append(")");

        commandHandlerMethod.setArgumentResolvers(argumentResolvers);
        commandHandlerMethod.setHandlerMethodResolvers(handlerMethodResolvers);

        commandHandlerMethod.setSignature(signatureBuilder.toString());
        return commandHandlerMethod;
    }

    private void registerCommands() throws ArnCommandException{
        CommandDispatcher<CommandListenerWrapper> dispatcher = ((CraftServer) Bukkit.getServer()).getHandle().c().aE().a();

        for (CommandHandlerMethod method : handlers) {
          try {
              List<ArgumentBuilder> nodes = new ArrayList<>();

              // initialize lists
              List<CommandAnnotationLiteral> literals = method.getAnnotationData().getLiterals();
              List<CommandArgumentResolver> nonnullResolvers = new ArrayList<>(method.getArgumentResolvers());
              List<Parameter> parametersClone = new ArrayList<>(method.getParameters());


              // iterate argument resolvers
              for (int i = 0; i < method.getArgumentResolvers().size(); i++) {
                  CommandArgumentResolver resolver = method.getArgumentResolvers().get(i);
                  if(resolver==null){
                      parametersClone.remove(i);
                      nonnullResolvers.remove(i);
                  }
              }

              for (CommandAnnotationLiteral lit : literals) if(lit.getOffset()==0) nodes.add(net.minecraft.commands.CommandDispatcher.a(lit.getLiteral()));

              System.out.println("iterate nonnull resolvers");
              System.out.println(nonnullResolvers);
              for (int i = 0; i < nonnullResolvers.size(); i++) {
                  CommandArgumentResolver resolver = nonnullResolvers.get(i);
                  System.out.println(resolver);

                  if(i!=0) for (CommandAnnotationLiteral lit : literals) if(lit.getOffset()==i) nodes.add(net.minecraft.commands.CommandDispatcher.a(lit.getLiteral()));

                  ArgumentBuilder builder = resolver.apply(parametersClone.get(i));
                  if (builder != null) nodes.add(builder);
              }

              com.mojang.brigadier.Command<CommandListenerWrapper> lambda = commandContext -> {

                  List<Object> objects = new ArrayList<>();

                  for (int i = 0; i < method.getHandlerMethodResolvers().size(); i++) {
                      CommandHandlerMethodArgumentResolver resolver = method.getHandlerMethodResolvers().get(i);
                      objects.add(resolver.resolve(method.getParameters().get(i), method, commandContext));
                  }

                  Method actualMethodToInvoke = method.getMethod();

                  try {
                      actualMethodToInvoke.setAccessible(true);
                      return (int) actualMethodToInvoke.invoke(containerInstanceMap.get(method.getMethod().getDeclaringClass().getName()), objects.toArray());
                  } catch (InvocationTargetException e) {
                      if (e.getCause() != null) try { // this wrap exists so ArnCommandException can be thrown
                          throw new ArnCommandException("Caused by " + e.getCause().getClass().getSimpleName() + ": " + e.getCause().getMessage(), e.getCause());
                      } catch (ArnCommandException ex) {
                          ex.printStackTrace();
                      }
                      return 1;
                  } catch (Exception e) {
                      e.printStackTrace();
                      return 1;
                  }


              };

              LiteralArgumentBuilder<CommandListenerWrapper> builder = (LiteralArgumentBuilder<CommandListenerWrapper>) chainArgumentBuilders(nodes, lambda, method.getAnnotationData());

              dispatcher.register(builder);
          } catch (Exception e){
              System.out.println(method);
              throw new ArnCommandException("Something went wrong with registering command '"+method.getCommand()+"'",e);
          }

        }
    }

    public static ArgumentBuilder<?, ?> chainArgumentBuilders(List<ArgumentBuilder> nodes, com.mojang.brigadier.Command<CommandListenerWrapper> executes, CommandAnnotationData data) {
        if (nodes.isEmpty()) return null;
        System.out.println(nodes.size());
        System.out.println(data);
        if (nodes.size() == 1) return nodes.get(0).executes(executes);

        ArgumentBuilder chainedBuilder = nodes.get(nodes.size() - 1).executes(executes);

        for (int i = nodes.size() - 2; i >= 0; i--) {
            chainedBuilder = nodes.get(i).then(chainedBuilder);
        }

        return chainedBuilder;
    }

}