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
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.efekos.arn.common.annotation.*;
import dev.efekos.arn.common.annotation.block.BlockCommandBlock;
import dev.efekos.arn.common.annotation.block.BlockConsole;
import dev.efekos.arn.common.annotation.block.BlockPlayer;
import dev.efekos.arn.argument.CustomArgumentType;
import dev.efekos.arn.common.config.ArnConfigurer;
import dev.efekos.arn.common.data.ExceptionHandlerMethod;
import dev.efekos.arn.common.data.ExceptionMap;
import dev.efekos.arn.common.exception.*;
import dev.efekos.arn.config.BaseArnConfigurer;
import dev.efekos.arn.data.*;
import dev.efekos.arn.exception.type.ArnExceptionTypes;
import dev.efekos.arn.common.resolver.CommandArgumentResolver;
import dev.efekos.arn.common.resolver.CommandHandlerMethodArgumentResolver;
import dev.efekos.arn.resolver.impl.command.CmdCustomArg;
import dev.efekos.arn.resolver.impl.command.CmdEnumArg;
import dev.efekos.arn.resolver.impl.handler.HndCustomArg;
import dev.efekos.arn.resolver.impl.handler.HndEnumArg;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.v1_21_R1.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Supplier;
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
public final class Arn extends MethodDump {

    /**
     * An exception type thrown by command handler when a command is blocked to {@link ConsoleCommandSender}s, but the
     * command sender is a {@link ConsoleCommandSender}.
     */
    public static final SimpleCommandExceptionType CONSOLE_BLOCKED_EXCEPTION = new SimpleCommandExceptionType(Component.literal("This command can't be used by the console."));
    /**
     * An exception type thrown by command handler when a command is blocked to {@link BlockCommandSender}s, but the
     * command sender is a {@link BlockCommandSender}.
     */
    public static final SimpleCommandExceptionType CM_BLOCKED_EXCEPTION = new SimpleCommandExceptionType(Component.literal("This command can't be used by command blocks."));
    /**
     * An exception type thrown by command handler when a command is blocked to {@link Player}s, but the command sender
     * is a {@link Player}.
     */
    public static final SimpleCommandExceptionType PLAYER_BLOCKED_EXCEPTION = new SimpleCommandExceptionType(Component.literal("This command can't be used by players."));
    /**
     * Generic exception type used to handle {@link ArnSyntaxException}s.
     */
    public static final DynamicCommandExceptionType GENERIC = new DynamicCommandExceptionType(o -> Component.literal((String) o));
    /**
     * Local instance of {@link Arn}, used by {@link #run(Class)}.
     */
    private static final Arn instance = new Arn();
    /**
     * A list of classes that are a sender. There can't be more than one parameter with one of these classes in a
     * {@link CommandHandlerMethod}, excluding ones annotated with {@link CommandArgument}.
     */
    private static final List<Class<? extends CommandSender>> REQUIRED_SENDER_CLASSES = Arrays.asList(CommandSender.class, Player.class, ConsoleCommandSender.class, BlockCommandSender.class);
    /**
     * Argument formatting colors. When executing helper methods, argument colors will be in this order of colors. When
     * there is more than 5 arguments, color goes back to the first element and repeats the same colors.
     */
    private static final List<ChatColor> ARGUMENT_DISPLAY_COLORS = Arrays.asList(ChatColor.AQUA, ChatColor.YELLOW, ChatColor.GREEN, ChatColor.LIGHT_PURPLE, ChatColor.GOLD);
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
     * An {@link ExceptionMap} storing annotation exceptions of {@link CommandArgumentResolver}s.
     */
    private final ExceptionMap<CommandArgumentResolver> commandArgumentResolverExceptions = new ExceptionMap<>();
    /**
     * An {@link ExceptionMap} storing annotation exceptions of {@link CommandHandlerMethodArgumentResolver}s.
     */
    private final ExceptionMap<CommandHandlerMethodArgumentResolver> handlerExceptions = new ExceptionMap<>();
    /**
     * A map containing an instance of every type annotated with {@link Container}. Used to instantiate {@link ArnConfigurer}s.
     */
    private final Map<String, Object> containerInstanceMap = new HashMap<>();
    /**
     * Whether was {@link #configure()} called or not. Used to prevent {@link #configure()} from being called more than
     * once.
     */
    private boolean configured;


    /**
     * Creates a new instance of Arn.
     */
    private Arn() {
    }

    /**
     * Main method used to run Arn. Scans every class under the package of {@code mainClass}, applies {@link ArnConfigurer}s
     * to base configuration, and registers found {@link CommandHandlerMethod}s.
     *
     * @param mainClass Main class whose package will be scanned. Recommended to make it your {@link JavaPlugin} class.
     */
    public static void run(Class<?> mainClass) {
        Reflections reflections = new Reflections(mainClass.getPackage().getName());

        try {
            instance.createContainerInstances(reflections);

            if (!instance.configured) instance.configure();
            instance.scanConfigurers(reflections);

            instance.scanEnumArguments(reflections);
            instance.scanCustomArguments(reflections);
            instance.scanExceptionHandlerMethods(reflections);

            instance.scanCommands(reflections);
            instance.registerCommands();
            instance.registerHelpers(reflections);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Scans {@link Container}s annotated with {@link CustomArgumentType} and creates a resolver for them.
     *
     * @param reflections Main reflections.
     */
    private void scanCustomArguments(Reflections reflections) {
        for (Class<?> customArgumentClass : reflections.getTypesAnnotatedWith(Container.class).stream().filter(aClass -> Arrays.asList(aClass.getInterfaces()).contains(CustomArgumentType.class)).toList()) {
            CustomArgumentType<?> o = (CustomArgumentType<?>) containerInstanceMap.get(customArgumentClass.getName());

            handlerMethodArgumentResolvers.add(new HndCustomArg(o));
            commandArgumentResolvers.add(new CmdCustomArg(o));
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
        List<Class<?>> classes = reflections.getTypesAnnotatedWith(Container.class).stream().filter(aClass -> aClass.isAnnotationPresent(CustomArgument.class)).toList();
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
        if (exceptions.size() > 1 || (!exceptions.isEmpty() && exceptions.stream().anyMatch(aClass -> !aClass.equals(CommandSyntaxException.class) && !aClass.equals(ArnSyntaxException.class))))
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

        if (handlers.stream().anyMatch(method1 -> commandHandlerMethod.getSignature().equals(method1.getSignature())))
            throw ArnExceptionTypes.HM_DUPLICATE.create(commandHandlerMethod);
        for (CommandAnnotationLiteral literal : commandHandlerMethod.getAnnotationData().getLiterals()) {
            if (literal.getOffset() < 0) throw ArnExceptionTypes.LITERAL_NEG_OFFSET.create(annotation);
            if (!literal.getLiteral().matches("^[a-z]+$"))
                throw ArnExceptionTypes.LITERAL_ILLEGAL.create(literal, annotation);
        }
        handlers.add(commandHandlerMethod);
    }

    /**
     * Creates a {@link CommandHandlerMethod} using {@code annotation} and {@code method}.
     *
     * @param annotation The {@link Command} annotation of {@code method}.
     * @param method     A {@link Method} that is annotated with {@code annotation}.
     * @return Created {@link CommandHandlerMethod}.
     * @throws ArnException See {@link ArnExceptionTypes#HM_NO_RESOLVER_ACCESS}.
     */
    private CommandHandlerMethod createHandlerMethod(Command annotation, Method method) throws ArnException {
        CommandHandlerMethod commandHandlerMethod = new CommandHandlerMethod();


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
        StringBuilder signature = buildSignature(method, handlerMethodResolvers, argumentResolvers);

        commandHandlerMethod.setArgumentResolvers(argumentResolvers);
        commandHandlerMethod.setHandlerMethodResolvers(handlerMethodResolvers);

        commandHandlerMethod.setSignature(signature.toString());
        return commandHandlerMethod;
    }


    private StringBuilder buildSignature(Method method, ArrayList<CommandHandlerMethodArgumentResolver> handlerMethodResolvers, ArrayList<CommandArgumentResolver> argumentResolvers) throws ArnCommandException {
        StringBuilder signatureBuilder = new StringBuilder();
        signatureBuilder.append(method.getAnnotation(Command.class).value());
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
                        resolver.isApplicable(parameter) && commandArgumentResolverExceptions.get(resolver.getClass()).stream().noneMatch(parameter::isAnnotationPresent)
                ).findFirst().get());
            else argumentResolvers.add(null);
        }
        signatureBuilder.append(")");
        return signatureBuilder;
    }


    /**
     * Registers every {@link CommandHandlerMethod} in {@link #handlers}.
     *
     * @throws ArnCommandException As a wrapper of an actual exception when encountered.
     */
    private void registerCommands() throws ArnException {
        CommandDispatcher<CommandSourceStack> dispatcher = ((CraftServer) Bukkit.getServer()).getHandle().getServer().getCommands().getDispatcher();

        for (CommandHandlerMethod method : handlers) {
            try {
                List<ArgumentBuilder> nodes = new ArrayList<>();

                // initialize lists
                List<CommandAnnotationLiteral> literals = method.getAnnotationData().getLiterals();

                List<Integer> indexesToDelete = new ArrayList<>();

                for (int i = 0; i < method.getArgumentResolvers().size(); i++)
                    if (method.getArgumentResolvers().get(i) == null) indexesToDelete.add(i);

                List<CommandArgumentResolver> nonnullResolvers = IntStream.range(0, method.getArgumentResolvers().size()).filter(i -> !indexesToDelete.contains(i)).mapToObj(method.getArgumentResolvers()::get).toList();
                List<Parameter> parametersClone = IntStream.range(0, method.getArgumentResolvers().size()).filter(i -> !indexesToDelete.contains(i)).mapToObj(method.getParameters()::get).toList();

                for (CommandAnnotationLiteral lit : literals)
                    if (lit.getOffset() == 0) nodes.add(Commands.literal(lit.getLiteral()));

                for (int i = 0; i < nonnullResolvers.size(); i++) {
                    CommandArgumentResolver resolver = nonnullResolvers.get(i);

                    if (i != 0) for (CommandAnnotationLiteral lit : literals)
                        if (lit.getOffset() == i)
                            nodes.add(Commands.literal(lit.getLiteral()));

                    ArgumentBuilder builder = resolver.apply(parametersClone.get(i));
                    if (builder != null) nodes.add(builder);
                }

                for (CommandAnnotationLiteral lit : literals)
                    if (lit.getOffset() == nonnullResolvers.size() && lit.getOffset() != 0)
                        nodes.add(Commands.literal(lit.getLiteral()));
                com.mojang.brigadier.Command<CommandSourceStack> lambda = createCommandLambda(method);

                LiteralArgumentBuilder<CommandSourceStack> builder = (LiteralArgumentBuilder<CommandSourceStack>) chainArgumentBuilders(nodes, lambda, method.getAnnotationData());

                dispatcher.register(builder);
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage(method.toString());
                throw ArnExceptionTypes.COMMAND_REGISTER_ERROR.create(method, e);
            }

        }
    }

    private com.mojang.brigadier.Command<CommandSourceStack> createCommandLambda(CommandHandlerMethod method) {
        return commandContext -> {

            CommandSender sender = commandContext.getSource().getBukkitSender();
            if (!method.getAnnotationData().getPermission().isEmpty() && !sender.hasPermission(method.getAnnotationData().getPermission()))
                return 1;
            if (method.isBlocksConsole() && sender instanceof ConsoleCommandSender)
                throw CONSOLE_BLOCKED_EXCEPTION.create();
            if (method.isBlocksCommandBlock() && sender instanceof BlockCommandSender)
                throw CM_BLOCKED_EXCEPTION.create();
            if (method.isBlocksPlayer() && sender instanceof Player) throw PLAYER_BLOCKED_EXCEPTION.create();

            List<Object> objects = fillResolvers(method, commandContext);

            Method actualMethodToInvoke = method.getMethod();

            try {
                actualMethodToInvoke.setAccessible(true);
                return (int) actualMethodToInvoke.invoke(containerInstanceMap.get(method.getMethod().getDeclaringClass().getName()), objects.toArray());
            } catch (InvocationTargetException e) {
                Throwable ex = e.getCause();
                if (ex == null) return 1;
                if (ex instanceof CommandSyntaxException) throw (CommandSyntaxException) ex;
                else if (ex instanceof ArnSyntaxException) throw GENERIC.create(ex.getMessage());
                else try {

                        Optional<dev.efekos.arn.common.data.ExceptionHandlerMethod> handlerMethodOptional = findHandlerMethod(ex);
                        if(handlerMethodOptional.isEmpty()) throw GENERIC.create(ex.getMessage());
                        ExceptionHandlerMethod handlerMethod = handlerMethodOptional.get();
                        List<Object> list = handlerMethod.fillParams(ex, commandContext);
                        Method actualHandlerMethod = handlerMethod.getMethod();
                        actualHandlerMethod.invoke(containerInstanceMap.get(actualHandlerMethod.getDeclaringClass().getName()),list.toArray());

                } catch (Exception exe) {throw GENERIC.create(exe.getMessage());}
                return 1;
            } catch (IllegalAccessException e) {
                ArnExceptionTypes.COMMAND_NO_ACCESS.create().initCause(e).printStackTrace();
                return 1;
            }

        };
    }

    /**
     * Scans classes annotated with {@link Helper}
     *
     * @param reflections A {@link Reflections} object to use finding {@link Helper}s.
     */
    private void registerHelpers(Reflections reflections) {
        CommandDispatcher<CommandSourceStack> dispatcher = ((CraftServer) Bukkit.getServer()).getHandle().getServer().getCommands().getDispatcher();

        for (Class<?> helperClass : reflections.getTypesAnnotatedWith(Container.class).stream().filter(aClass -> aClass.isAnnotationPresent(Helper.class)).toList()) {
            List<CommandHandlerMethod> associatedHelperMethods = handlers.stream().filter(commandHandlerMethod -> commandHandlerMethod.getMethod().getDeclaringClass().equals(helperClass)).toList();

            com.mojang.brigadier.Command<CommandSourceStack> lambda = (s) -> {
                CommandSender sender = s.getSource().getBukkitSender();

                for (CommandHandlerMethod helperMethod : associatedHelperMethods) {
                    Supplier<Boolean> isDisabled = sender instanceof Player ? helperMethod::isBlocksPlayer : (sender instanceof ConsoleCommandSender ? helperMethod::isBlocksConsole : helperMethod::isBlocksCommandBlock);
                    if (isDisabled.get()) continue;

                    String permission = helperMethod.getAnnotationData().getPermission();
                    if (permission != null && !sender.hasPermission(permission)) continue;

                    StringBuilder builder = new StringBuilder().append(ChatColor.GRAY + "/");

                    int adcI = 0;
                    List<Parameter> a = helperMethod.getParameters().stream().filter(parameter -> parameter.isAnnotationPresent(CommandArgument.class)).toList();

                    for (CommandAnnotationLiteral lit : helperMethod.getAnnotationData().getLiterals())
                        if (lit.getOffset() == 0) builder.append(ChatColor.GRAY + lit.getLiteral() + " ");


                    for (int i = 0; i < a.size(); i++) {

                        if (i != 0)
                            for (CommandAnnotationLiteral lit : helperMethod.getAnnotationData().getLiterals())
                                if (lit.getOffset() == i) builder.append(ChatColor.GRAY + lit.getLiteral() + " ");

                        Parameter parameter = a.get(i);
                        builder.append(ARGUMENT_DISPLAY_COLORS.get((adcI++) % 5) + "<" + parameter.getName() + "> ");
                    }

                    BaseComponent component = TextComponent.fromLegacy(builder.toString());

                    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(new TextComponent(helperMethod.getAnnotationData().getDescription()))));
                    sender.spigot().sendMessage(component);
                }

                return 0;
            };


            ArrayList<CommandAnnotationLiteral> literals = new ArrayList<>();
            for (String s : helperClass.getAnnotation(Helper.class).value().split("\\" + CommandAnnotationLiteral.SEPARATOR_CHAR_STRING))
                literals.add(CommandAnnotationLiteral.parse(s));

            List<ArgumentBuilder> builders = literals.stream().map(commandAnnotationLiteral -> Commands.literal(commandAnnotationLiteral.getLiteral())).collect(Collectors.toList());
            ArgumentBuilder<?, ?> finalNode = chainArgumentBuilders(builders, lambda, null);

            dispatcher.register(((LiteralArgumentBuilder) finalNode));

        }

    }

}