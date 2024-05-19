package dev.efekos.arn;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.efekos.arn.annotation.Command;
import dev.efekos.arn.annotation.CommandArgument;
import dev.efekos.arn.annotation.Container;
import dev.efekos.arn.annotation.block.BlockCommandBlock;
import dev.efekos.arn.annotation.block.BlockConsole;
import dev.efekos.arn.annotation.block.BlockPlayer;
import dev.efekos.arn.config.ArnConfigurer;
import dev.efekos.arn.data.CommandAnnotationData;
import dev.efekos.arn.data.CommandAnnotationLiteral;
import dev.efekos.arn.data.CommandHandlerMethod;
import dev.efekos.arn.exception.ArnCommandException;
import dev.efekos.arn.exception.ArnContainerException;
import dev.efekos.arn.resolver.CommandArgumentResolver;
import dev.efekos.arn.resolver.CommandHandlerMethodArgumentResolver;
import dev.efekos.arn.resolver.impl.command.*;
import dev.efekos.arn.resolver.impl.handler.*;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.network.chat.IChatBaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * Main class of Arn, used to run command scanning and registration. Handles scanning {@link Container}s, applying
 * {@link ArnConfigurer}s, creating {@link CommandHandlerMethod}s and registering commands. {@link Arn#run(Class)} must
 * be called in {@link JavaPlugin#onEnable()} to register commands.
 *
 * @author efekos
 * @since 0.1
 */
public final class Arn {

    /**
     * Local instance of {@link Arn}, used by {@link #run(Class)}.
     */
    private static final Arn instance = new Arn();

    /**
     * A list of {@link CommandHandlerMethodArgumentResolver}s that can provide values to parameters of a {@link CommandHandlerMethod}.
     */
    private final List<CommandHandlerMethodArgumentResolver> handlerMethodArgumentResolvers = new ArrayList<>();

    /**
     * A list of {@link CommandArgumentResolver} that can provide {@link ArgumentBuilder}s using parameters of a command
     * handler method to create command structures.
     */
    private final List<CommandArgumentResolver> commandArgumentResolvers = new ArrayList<>();

    /**
     * A list of scanned {@link CommandHandlerMethod}s. Used to detect duplicate {@link CommandHandlerMethod}s and register
     * commands.
     */
    private final List<CommandHandlerMethod> handlers = new ArrayList<>();

    /**
     * A map containing an instance of every type annotated with {@link Container}. Used to instantiate {@link ArnConfigurer}s.
     */
    private final Map<String, Object> containerInstanceMap = new HashMap<>();

    /**
     * An exception type thrown by command handler when a command is blocked to {@link ConsoleCommandSender}s, but the
     * command sender is a {@link ConsoleCommandSender}.
     */
    public static final SimpleCommandExceptionType CONSOLE_BLOCKED_EXCEPTION = new SimpleCommandExceptionType(IChatBaseComponent.b("This command can't be used by the console."));

    /**
     * An exception type thrown by command handler when a command is blocked to {@link BlockCommandSender}s, but the
     * command sender is a {@link BlockCommandSender}.
     */
    public static final SimpleCommandExceptionType CM_BLOCKED_EXCEPTION = new SimpleCommandExceptionType(IChatBaseComponent.b("This command can't be used by command blocks."));

    /**
     * An exception type thrown by command handler when a command is blocked to {@link Player}s, but the command sender
     * is a {@link Player}.
     */
    public static final SimpleCommandExceptionType PLAYER_BLOCKED_EXCEPTION = new SimpleCommandExceptionType(IChatBaseComponent.b("This command can't be used by players."));

    /**
     * Main method used to run Arn. Scans every class under the package of {@code mainClass}, applies {@link ArnConfigurer}s
     * to base configuration, and registers found {@link CommandHandlerMethod}s.
     *
     * @param mainClass Main class whose package will be scanned. Recommended to make it your {@link JavaPlugin} class.
     */
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

    /**
     * Creates an instance of every {@link Container} found by {@code reflections}.
     *
     * @param reflections A {@link Reflections} to find {@link Container}s.
     * @throws ArnContainerException If a {@link Container} can't be instantiated using an empty constructor.
     */
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

    /**
     * Default configuration of {@link Arn}.
     */
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
        handlerMethodArgumentResolvers.add(new HndBlockDataArg());
        handlerMethodArgumentResolvers.add(new HndSender());
        handlerMethodArgumentResolvers.add(new HndConsoleCommandSender());
        handlerMethodArgumentResolvers.add(new HndBlockCommandSender());

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
        commandArgumentResolvers.add(new CmdBlockDataArg());
    }

    /**
     * Scans every class that implements {@link ArnConfigurer} using {@code reflections}, and calls their commands.
     *
     * @param reflections A {@link Reflections} object to use finding {@link ArnConfigurer}s.
     */
    private void scanConfigurers(Reflections reflections) {
        Object[] configurers = reflections.getTypesAnnotatedWith(Container.class).stream().filter(aClass -> Arrays.asList(aClass.getInterfaces()).contains(ArnConfigurer.class)).toArray();

        for (Object configurer : configurers) {
            Class<? extends ArnConfigurer> clazz = (Class<? extends ArnConfigurer>) configurer;

            ArnConfigurer configurerInstance = (ArnConfigurer) containerInstanceMap.get(clazz.getName());
            configurerInstance.addHandlerMethodArgumentResolvers(handlerMethodArgumentResolvers);
            configurerInstance.addArgumentResolvers(commandArgumentResolvers);
        }
    }

    /**
     * Scans every {@link Container} for {@link Command}s using {@code reflections}.
     * @param reflections A {@link Reflections} object to use finding {@link Command}s.
     * @throws ArnCommandException If a {@link Command} is invalid.
     */
    private void scanCommands(Reflections reflections) throws ArnCommandException {
        Set<Class<?>> containers = reflections.getTypesAnnotatedWith(Container.class);

        for (Class<?> container : containers)
            for (Method method : container.getMethods())
                if (method.isAnnotationPresent(Command.class)) instance.command(method.getAnnotation(Command.class), method);

    }


    /**
     * A list of classes that are a sender. There can't be more than one parameter with one of these classes in a
     * {@link CommandHandlerMethod}, excluding ones annotated with {@link CommandArgument}.
     */
    private static final List<Class<? extends CommandSender>> REQUIRED_SENDER_CLASSES = Arrays.asList(CommandSender.class, Player.class, ConsoleCommandSender.class, BlockCommandSender.class);

    /**
     * Checks errors to ensure the command is valid.
     * @param annotation The {@link Command} annotation of {@code method}.
     * @param method A {@link Method} that is annotated with {@code annotation}.
     * @throws ArnCommandException If something about {@code method} or created {@link CommandHandlerMethod} doesn't seem right.
     */
    private void command(Command annotation, Method method) throws ArnCommandException {
        // Errors
        if (!method.getReturnType().equals(int.class))
            throw new ArnCommandException("Handler method '" + method.getName() + "' for command '" + annotation.value() + "' does not return 'int'");
        long count = Arrays.stream(method.getParameters()).filter(parameter -> REQUIRED_SENDER_CLASSES.contains(parameter.getType()) && !parameter.isAnnotationPresent(CommandArgument.class)).count();
        if (count > 1)
            throw new ArnCommandException("Handler method '" + method.getName() + "' for command '" + annotation.value() + "' must contain maximum one parameter that is a CommandSender.");
        for (Parameter parameter : method.getParameters()) {
            if (handlerMethodArgumentResolvers.stream().noneMatch(car -> car.isApplicable(parameter)))
                throw new ArnCommandException("Handler method '" + method.getName() + "' for command '" + annotation.value() + "' has a parameter '" + parameter.getName() + "' that isn't applicable for anything.");
            if (handlerMethodArgumentResolvers.stream().anyMatch(car -> car.isApplicable(parameter) && car.requireCommandArgument()) && commandArgumentResolvers.stream().noneMatch(car -> car.isApplicable(parameter)))
                throw new ArnCommandException("Handler method '" + method.getName() + "' for command '" + annotation.value() + "' has a parameter '" + parameter.getName() + "' that isn't applicable for anything.");
        }

        CommandHandlerMethod commandHandlerMethod = createHandlerMethod(annotation, method);

        if (handlers.stream().anyMatch(method1 -> commandHandlerMethod.getSignature().equals(method1.getSignature())))
            throw new ArnCommandException("Duplicate command '" + commandHandlerMethod.getSignature() + "'");
        for (CommandAnnotationLiteral literal : commandHandlerMethod.getAnnotationData().getLiterals()) {

            if(literal.getOffset()<0) throw new ArnCommandException("Command '"+annotation.value()+"' has a literal with a negative offset value.");
            if(!literal.getLiteral().matches("^[a-z]+$")) throw new ArnCommandException("Literal '"+literal.getLiteral()+"' of command '"+annotation.value()+" has an illegal character.");

        }
        handlers.add(commandHandlerMethod);
    }

    /**
     * Creates a {@link CommandHandlerMethod} using {@code annotation} and {@code method}.
     * @param annotation The {@link Command} annotation of {@code method}.
     * @param method A {@link Method} that is annotated with {@code annotation}.
     * @return Created {@link CommandHandlerMethod}.
     */
    private CommandHandlerMethod createHandlerMethod(Command annotation, Method method) {
        CommandHandlerMethod commandHandlerMethod = new CommandHandlerMethod();
        StringBuilder signatureBuilder = new StringBuilder();

        signatureBuilder.append(annotation.value()).append(" ");

        commandHandlerMethod.setCommand(annotation.value());
        commandHandlerMethod.setMethod(method);
        commandHandlerMethod.setParameters(Arrays.asList(method.getParameters()));
        commandHandlerMethod.setBlocksCommandBlock(method.isAnnotationPresent(BlockCommandBlock.class));
        commandHandlerMethod.setBlocksConsole(method.isAnnotationPresent(BlockConsole.class));
        commandHandlerMethod.setBlocksPlayer(method.isAnnotationPresent(BlockPlayer.class));

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
            if (handlerMethodArgumentResolver.requireCommandArgument())
                argumentResolvers.add(this.commandArgumentResolvers.stream().filter(resolver -> resolver.isApplicable(parameter)).findFirst().get());
            else argumentResolvers.add(null);
        }
        signatureBuilder.append(")");

        commandHandlerMethod.setArgumentResolvers(argumentResolvers);
        commandHandlerMethod.setHandlerMethodResolvers(handlerMethodResolvers);

        commandHandlerMethod.setSignature(signatureBuilder.toString());
        return commandHandlerMethod;
    }

    /**
     * Registers every {@link CommandHandlerMethod} in {@link #handlers}.
     * @throws ArnCommandException As a wrapper of an actual exception when encountered.
     */
    private void registerCommands() throws ArnCommandException {
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
                    if (resolver == null) {
                        parametersClone.remove(i);
                        nonnullResolvers.remove(i);
                    }
                }

                for (CommandAnnotationLiteral lit : literals)
                    if (lit.getOffset() == 0) nodes.add(net.minecraft.commands.CommandDispatcher.a(lit.getLiteral()));

                System.out.println("iterate nonnull resolvers");
                System.out.println(nonnullResolvers);
                for (int i = 0; i < nonnullResolvers.size(); i++) {
                    CommandArgumentResolver resolver = nonnullResolvers.get(i);
                    System.out.println(resolver);

                    if (i != 0) for (CommandAnnotationLiteral lit : literals)
                        if (lit.getOffset() == i)
                            nodes.add(net.minecraft.commands.CommandDispatcher.a(lit.getLiteral()));

                    ArgumentBuilder builder = resolver.apply(parametersClone.get(i));
                    if (builder != null) nodes.add(builder);
                }

                com.mojang.brigadier.Command<CommandListenerWrapper> lambda = commandContext -> {

                    CommandSender sender = commandContext.getSource().getBukkitSender();
                    if (method.isBlocksConsole() && sender instanceof ConsoleCommandSender)
                        throw CONSOLE_BLOCKED_EXCEPTION.create();
                    if (method.isBlocksCommandBlock() && sender instanceof BlockCommandSender)
                        throw CM_BLOCKED_EXCEPTION.create();
                    if (method.isBlocksPlayer() && sender instanceof Player) throw PLAYER_BLOCKED_EXCEPTION.create();

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
            } catch (Exception e) {
                System.out.println(method);
                throw new ArnCommandException("Something went wrong with registering command '" + method.getCommand() + "'", e);
            }

        }
    }

    /**
     * Chains given argument builders into one {@link ArgumentBuilder} that can be used to register the command.
     * @param nodes List of the nodes to chain.
     * @param executes execute function to handle the command. Added to the last argument in the chain.
     * @param data {@link CommandAnnotationData} associated with the nodes. If there is a permission required, it will
     *             be applied to the last literal of the chain.
     * @return {@code nodes[0]} with rest of the nodes attached to it.
     */
    private static ArgumentBuilder<?, ?> chainArgumentBuilders(List<ArgumentBuilder> nodes, com.mojang.brigadier.Command<CommandListenerWrapper> executes, CommandAnnotationData data) {
        if (nodes.isEmpty()) return null;
        System.out.println(nodes.size());
        System.out.println(data);

        ArgumentBuilder chainedBuilder = nodes.get(nodes.size() - 1).executes(executes);

        for (int i = nodes.size() - 2; i >= 0; i--) {
            chainedBuilder = nodes.get(i).then(chainedBuilder);
        }

        return chainedBuilder;
    }

}