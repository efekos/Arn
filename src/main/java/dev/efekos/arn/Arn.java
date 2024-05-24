/*
 * MIT License
 *
 * Copyright (c) 2024 efekos
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.efekos.arn;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.efekos.arn.annotation.Command;
import dev.efekos.arn.annotation.CommandArgument;
import dev.efekos.arn.annotation.Container;
import dev.efekos.arn.annotation.CustomArgument;
import dev.efekos.arn.annotation.block.BlockCommandBlock;
import dev.efekos.arn.annotation.block.BlockConsole;
import dev.efekos.arn.annotation.block.BlockPlayer;
import dev.efekos.arn.config.ArnConfigurer;
import dev.efekos.arn.config.BaseArnConfigurer;
import dev.efekos.arn.data.CommandAnnotationData;
import dev.efekos.arn.data.CommandAnnotationLiteral;
import dev.efekos.arn.data.CommandHandlerMethod;
import dev.efekos.arn.data.ExceptionMap;
import dev.efekos.arn.exception.ArnArgumentException;
import dev.efekos.arn.exception.ArnCommandException;
import dev.efekos.arn.exception.ArnContainerException;
import dev.efekos.arn.exception.ArnException;
import dev.efekos.arn.exception.type.ArnExceptionTypes;
import dev.efekos.arn.resolver.CommandArgumentResolver;
import dev.efekos.arn.resolver.CommandHandlerMethodArgumentResolver;
import dev.efekos.arn.resolver.impl.command.CmdEnumArg;
import dev.efekos.arn.resolver.impl.handler.HndEnumArg;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.network.chat.IChatBaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
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
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    private final ExceptionMap<CommandArgumentResolver> commandArgumentResolverExceptions = new ExceptionMap<>();
    private final ExceptionMap<CommandHandlerMethodArgumentResolver> handlerExceptions = new ExceptionMap<>();

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
     * Whether was {@link #configure()} called or not. Used to prevent {@link #configure()} from being called more than
     * once.
     */
    private boolean configured;

    /**
     * Main method used to run Arn. Scans every class under the package of {@code mainClass}, applies {@link ArnConfigurer}s
     * to base configuration, and registers found {@link CommandHandlerMethod}s.
     *
     * @param mainClass Main class whose package will be scanned. Recommended to make it your {@link JavaPlugin} class.
     */
    public static void run(Class<?> mainClass) {
        Reflections reflections = new Reflections(mainClass.getPackage().getName());

        try {
            if (!instance.configured) instance.configure();
            instance.scanConfigurers(reflections);
            instance.scanEnumArguments(reflections);


            instance.createContainerInstances(reflections);
            instance.scanCommands(reflections);
            instance.registerCommands();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Scans for {@link Container} enums annotated with {@link CustomArgument}, and registers a {@link CmdEnumArg} &amp;
     * {@link HndEnumArg} for them.
     *
     * @param reflections Main reflections.
     * @throws ArnArgumentException If something about an enum found is invalid.
     */
    private void scanEnumArguments(Reflections reflections) throws ArnException {
        List<Class<?>> classes = reflections.getTypesAnnotatedWith(Container.class).stream().filter(aClass -> aClass.isAnnotationPresent(CustomArgument.class)).collect(Collectors.toList());
        for (Class<?> aClass : classes) {
            if (!aClass.isEnum()) throw ArnExceptionTypes.CA_NOT_ENUM.create(aClass);
            Class<? extends Enum<?>> enumC = (Class<? extends Enum<?>>) aClass;

            CustomArgument customArgument = enumC.getAnnotation(CustomArgument.class);
            try {
                NamespacedKey.fromString(customArgument.value());
            } catch (Exception e) {
                throw ArnExceptionTypes.CA_VALUE_NOT_KEY.create(aClass);
            }

            if (enumC.getEnumConstants().length == 0) throw ArnExceptionTypes.CA_NO_CONSTANTS.create(enumC);
            if (Arrays.stream(enumC.getEnumConstants()).anyMatch(constant -> !constant.name().toUpperCase(Locale.ENGLISH).equals(constant.name())))
                throw ArnExceptionTypes.CA_LOWERCASE.create(enumC);

            handlerMethodArgumentResolvers.add(new HndEnumArg(enumC));
            commandArgumentResolvers.add(new CmdEnumArg(enumC));
        }
    }

    /**
     * Creates an instance of every {@link Container} found by {@code reflections}.
     *
     * @param reflections A {@link Reflections} to find {@link Container}s.
     * @throws ArnContainerException If a {@link Container} can't be instantiated using an empty constructor.
     */
    private void createContainerInstances(Reflections reflections) throws ArnException {
        for (Class<?> clazz : reflections.getTypesAnnotatedWith(Container.class)) {
            if (clazz.isInterface() || clazz.isAnnotation() || clazz.isEnum()) continue;

            try {
                Constructor<?> ctor = clazz.getConstructor();
                ctor.setAccessible(true);
                Object o = ctor.newInstance();
                containerInstanceMap.put(clazz.getName(), o);
            } catch (Exception e) {
                throw ArnExceptionTypes.CONTAINER_INSTANTIATE.create(clazz, e);
            }
        }
    }

    /**
     * Default configuration of {@link Arn}.
     */
    private void configure() {
        BaseArnConfigurer configurer = new BaseArnConfigurer();
        configurer.addArgumentResolvers(commandArgumentResolvers);
        configurer.addHandlerMethodArgumentResolvers(handlerMethodArgumentResolvers);
        configurer.putArgumentResolverExceptions(commandArgumentResolverExceptions);
        configurer.putHandlerMethodArgumentResolverExceptions(handlerExceptions);
        configured = true;
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
            configurerInstance.putArgumentResolverExceptions(commandArgumentResolverExceptions);
            System.out.println("a");
            configurerInstance.putHandlerMethodArgumentResolverExceptions(handlerExceptions);
        }
    }

    /**
     * Scans every {@link Container} for {@link Command}s using {@code reflections}.
     *
     * @param reflections A {@link Reflections} object to use finding {@link Command}s.
     * @throws ArnException If a {@link Command} is invalid.
     */
    private void scanCommands(Reflections reflections) throws ArnException {
        Set<Class<?>> containers = reflections.getTypesAnnotatedWith(Container.class);

        for (Class<?> container : containers)
            for (Method method : container.getMethods())
                if (method.isAnnotationPresent(Command.class))
                    instance.command(method.getAnnotation(Command.class), method);

    }

    /**
     * A list of classes that are a sender. There can't be more than one parameter with one of these classes in a
     * {@link CommandHandlerMethod}, excluding ones annotated with {@link CommandArgument}.
     */
    private static final List<Class<? extends CommandSender>> REQUIRED_SENDER_CLASSES = Arrays.asList(CommandSender.class, Player.class, ConsoleCommandSender.class, BlockCommandSender.class);

    /**
     * Checks errors to ensure the command is valid.
     *
     * @param annotation The {@link Command} annotation of {@code method}.
     * @param method     A {@link Method} that is annotated with {@code annotation}.
     * @throws ArnException If something about {@code method} or created {@link CommandHandlerMethod} doesn't seem right.
     */
    private void command(Command annotation, Method method) throws ArnException {
        // Errors
        if (!method.getReturnType().equals(int.class)) throw ArnExceptionTypes.HM_NOT_INT.create(method, annotation);
        List<Class<?>> exceptions = Arrays.asList(method.getExceptionTypes());
        if (exceptions.size() > 1 || (!exceptions.isEmpty() && exceptions.get(0) != CommandSyntaxException.class))
            throw ArnExceptionTypes.HM_THROWS.create(method, annotation, exceptions);

        long count = Arrays.stream(method.getParameters()).filter(parameter -> REQUIRED_SENDER_CLASSES.contains(parameter.getType()) && !parameter.isAnnotationPresent(CommandArgument.class)).count();
        if (count > 1) throw ArnExceptionTypes.HM_MULTIPLE_SENDERS.create(method, annotation);

        for (Parameter parameter : method.getParameters()) {
            if (handlerMethodArgumentResolvers.stream().noneMatch(car -> car.isApplicable(parameter)))
                throw ArnExceptionTypes.HM_NOT_APPLICABLE.create(method, annotation, parameter);
            if (handlerMethodArgumentResolvers.stream().anyMatch(car -> car.isApplicable(parameter) && car.requireCommandArgument()) && commandArgumentResolvers.stream().noneMatch(car -> car.isApplicable(parameter)))
                throw ArnExceptionTypes.HM_NOT_APPLICABLE.create(method, annotation, parameter);
        }

        CommandHandlerMethod commandHandlerMethod = createHandlerMethod(annotation, method);

        if (handlers.stream().anyMatch(method1 -> commandHandlerMethod.getSignature().equals(method1.getSignature()))) throw ArnExceptionTypes.HM_DUPLICATE.create(commandHandlerMethod);
        for (CommandAnnotationLiteral literal : commandHandlerMethod.getAnnotationData().getLiterals()) {
            if (literal.getOffset() < 0) throw ArnExceptionTypes.LITERAL_NEG_OFFSET.create(annotation);
            if (!literal.getLiteral().matches("^[a-z]+$")) throw ArnExceptionTypes.LITERAL_ILLEGAL.create(literal, annotation);
        }
        handlers.add(commandHandlerMethod);
    }

    /**
     * Creates a {@link CommandHandlerMethod} using {@code annotation} and {@code method}.
     *
     * @param annotation The {@link Command} annotation of {@code method}.
     * @param method     A {@link Method} that is annotated with {@code annotation}.
     * @return Created {@link CommandHandlerMethod}.
     */
    private CommandHandlerMethod createHandlerMethod(Command annotation, Method method) throws ArnException {
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
            CommandHandlerMethodArgumentResolver handlerMethodArgumentResolver = this.handlerMethodArgumentResolvers.stream().filter(resolver ->
                    resolver.isApplicable(parameter) && handlerExceptions.get(resolver.getClass()).stream().noneMatch(parameter::isAnnotationPresent)
            ).findFirst().orElseThrow(() -> ArnExceptionTypes.HM_NO_RESOLVER_ACCESS.create(signatureBuilder.append(")").toString()));

            handlerMethodResolvers.add(handlerMethodArgumentResolver);

            if (handlerMethodArgumentResolver.requireCommandArgument())
                argumentResolvers.add(this.commandArgumentResolvers.stream().filter(resolver ->
                        resolver.isApplicable(parameter)&& commandArgumentResolverExceptions.get(resolver.getClass()).stream().noneMatch(parameter::isAnnotationPresent)
                ).findFirst().get());
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
     *
     * @throws ArnCommandException As a wrapper of an actual exception when encountered.
     */
    private void registerCommands() throws ArnException {
        CommandDispatcher<CommandListenerWrapper> dispatcher = ((CraftServer) Bukkit.getServer()).getHandle().c().aE().a();

        for (CommandHandlerMethod method : handlers) {
            try {
                List<ArgumentBuilder> nodes = new ArrayList<>();

                // initialize lists
                List<CommandAnnotationLiteral> literals = method.getAnnotationData().getLiterals();

                List<Integer> indexesToDelete = new ArrayList<>();

                for (int i = 0; i < method.getArgumentResolvers().size(); i++)
                    if (method.getArgumentResolvers().get(i) == null) indexesToDelete.add(i);

                List<CommandArgumentResolver> nonnullResolvers = IntStream.range(0, method.getArgumentResolvers().size()).filter(i -> !indexesToDelete.contains(i)).mapToObj(method.getArgumentResolvers()::get).collect(Collectors.toList());
                List<Parameter> parametersClone = IntStream.range(0, method.getArgumentResolvers().size()).filter(i -> !indexesToDelete.contains(i)).mapToObj(method.getParameters()::get).collect(Collectors.toList());

                for (CommandAnnotationLiteral lit : literals)
                    if (lit.getOffset() == 0) nodes.add(net.minecraft.commands.CommandDispatcher.a(lit.getLiteral()));

                for (int i = 0; i < nonnullResolvers.size(); i++) {
                    CommandArgumentResolver resolver = nonnullResolvers.get(i);

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
                        if (e.getCause() != null) ArnExceptionTypes.COMMAND_ERROR.create(e.getCause()).printStackTrace();
                        return 1;
                    } catch (IllegalAccessException e) {
                        ArnExceptionTypes.COMMAND_NO_ACCESS.create().initCause(e).printStackTrace();
                        return 1;
                    }

                };

                LiteralArgumentBuilder<CommandListenerWrapper> builder = (LiteralArgumentBuilder<CommandListenerWrapper>) chainArgumentBuilders(nodes, lambda, method.getAnnotationData());

                dispatcher.register(builder);
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage(method.toString());
                throw ArnExceptionTypes.COMMAND_REGISTER_ERROR.create(method,e);
            }

        }
    }

    /**
     * Chains given argument builders into one {@link ArgumentBuilder} that can be used to register the command.
     *
     * @param nodes    List of the nodes to chain.
     * @param executes execute function to handle the command. Added to the last argument in the chain.
     * @param data     {@link CommandAnnotationData} associated with the nodes. If there is a permission required, it will
     *                 be applied to first literal of the chain.
     * @return {@code nodes[0]} with rest of the nodes attached to it.
     */
    private static ArgumentBuilder<?, ?> chainArgumentBuilders(List<ArgumentBuilder> nodes, com.mojang.brigadier.Command<CommandListenerWrapper> executes, CommandAnnotationData data) {
        if (nodes.isEmpty()) return null;

        if (!data.getPermission().isEmpty())
            nodes.set(0, nodes.get(0).requires(o -> ((CommandListenerWrapper) o).getBukkitSender().hasPermission(data.getPermission())));

        ArgumentBuilder chainedBuilder = nodes.get(nodes.size() - 1).executes(executes);

        for (int i = nodes.size() - 2; i >= 0; i--) {
            chainedBuilder = nodes.get(i).then(chainedBuilder);
        }

        return chainedBuilder;
    }

    /**
     * Finds last element that matches the given condition.
     *
     * @param list      Any list.
     * @param condition A condition.
     * @param <T>       Type of the elements in the list.
     * @return Last element that matches the given condition in the list.
     */
    private static <T> int findLastIndex(List<T> list, Predicate<T> condition) {
        for (int i = list.size() - 1; i >= 0; i--) {
            if (condition.test(list.get(i))) {
                return i;
            }
        }
        return -1; // Return null if no match is found
    }

}