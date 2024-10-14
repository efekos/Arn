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

package dev.efekos.arn.spigot;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.efekos.arn.common.ArnFeature;
import dev.efekos.arn.common.CommandAnnotationData;
import dev.efekos.arn.common.CommandAnnotationLiteral;
import dev.efekos.arn.common.annotation.*;
import dev.efekos.arn.common.base.ArnInstance;
import dev.efekos.arn.common.base.BaseArnConfigurer;
import dev.efekos.arn.common.base.BaseCmdResolver;
import dev.efekos.arn.common.base.BaseHndResolver;
import dev.efekos.arn.common.exception.*;
import dev.efekos.arn.spigot.face.CustomArgumentType;
import dev.efekos.arn.spigot.face.SpArnConfig;
import dev.efekos.arn.spigot.face.SpigotCmdResolver;
import dev.efekos.arn.spigot.face.SpigotHndResolver;
import dev.efekos.arn.spigot.resolver.command.CmdCustomArg;
import dev.efekos.arn.spigot.resolver.command.CmdEnumArg;
import dev.efekos.arn.spigot.resolver.handler.HndCustomArg;
import dev.efekos.arn.spigot.resolver.handler.HndEnumArg;
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
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Main class of Arn, used to run command scanning and registration. Handles
 * scanning {@link Container}s, applying
 * {@link SpArnConfig}s, creating {@link SpigotCommandHandlerMethod}s and
 * registering commands. {@link SpigotArn#run(Class, Object)} must
 * be called in {@link JavaPlugin#onEnable()} to register commands.
 *
 * @author efekos
 * @since 0.1
 */
public final class SpigotArn extends SpigotArnMethodDump implements ArnInstance {

    /**
     * An exception type thrown by command handler when a command is blocked to
     * {@link ConsoleCommandSender}s, but the
     * command sender is a {@link ConsoleCommandSender}.
     */
    public static final SimpleCommandExceptionType CONSOLE_BLOCKED_EXCEPTION = new SimpleCommandExceptionType(
            Component.literal("This command can't be used by the console."));
    /**
     * An exception type thrown by command handler when a command is blocked to
     * {@link BlockCommandSender}s, but the
     * command sender is a {@link BlockCommandSender}.
     */
    public static final SimpleCommandExceptionType CM_BLOCKED_EXCEPTION = new SimpleCommandExceptionType(
            Component.literal("This command can't be used by command blocks."));
    /**
     * An exception type thrown by command handler when a command is blocked to
     * {@link Player}s, but the command sender
     * is a {@link Player}.
     */
    public static final SimpleCommandExceptionType PLAYER_BLOCKED_EXCEPTION = new SimpleCommandExceptionType(
            Component.literal("This command can't be used by players."));
    /**
     * Generic exception type used to handle {@link ArnSyntaxException}s.
     */
    public static final DynamicCommandExceptionType GENERIC = new DynamicCommandExceptionType(
            o -> Component.literal((String) o));


    private static final List<Class<? extends CommandSender>> REQUIRED_SENDER_CLASSES = Arrays
            .asList(CommandSender.class, Player.class, ConsoleCommandSender.class, BlockCommandSender.class);
    private static final List<ChatColor> ARGUMENT_DISPLAY_COLORS = Arrays.asList(ChatColor.AQUA, ChatColor.YELLOW,
            ChatColor.GREEN, ChatColor.LIGHT_PURPLE, ChatColor.GOLD);
    private final List<SpigotHndResolver> handlerMethodArgumentResolvers = new ArrayList<>();
    private final List<SpigotCmdResolver> commandArgumentResolvers = new ArrayList<>();
    private final List<SpigotCommandHandlerMethod> handlers = new ArrayList<>();
    private final ExceptionMap<SpigotCmdResolver> commandArgumentResolverExceptions = new ExceptionMap<>();
    private final ExceptionMap<SpigotHndResolver> handlerExceptions = new ExceptionMap<>();
    private final Map<String, Object> containerInstanceMap = new HashMap<>();
    private final List<Class<?>> exclusions = new ArrayList<>();
    private boolean configured;

    /**
     * Main method used to run Arn. Scans every class under the package of
     * {@code mainClass}, applies {@link SpArnConfig}s
     * to base configuration, and registers found
     * {@link SpigotCommandHandlerMethod}s.
     *
     * @param mainClass Main class whose package will be scanned. Recommended to
     *                  make it your {@link JavaPlugin} class.
     */
    public <T> void run(Class<T> mainClass, T instance) {
        if (!(instance instanceof Plugin))
            throw new IllegalStateException("Arn#run was called with a " + mainClass.getName() + " instance which isn't a Plugin.");
        Reflections reflections = new Reflections(mainClass.getPackage().getName());

        try {
            createContainerInstances(reflections);

            if (!configured)
                configure();
            scanConfigurers(reflections);

            scanEnumArguments(reflections);
            scanCustomArguments(reflections);
            scanExceptionHandlerMethods(reflections);

            scanCommands(reflections);
            registerCommands();
            registerHelpers(reflections);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected SpigotArn error. Please report this to github: https://github.com/efekos/Arn", e);
        }
    }

    private void scanCustomArguments(Reflections reflections) {
        for (Class<?> customArgumentClass : reflections.getTypesAnnotatedWith(Container.class).stream()
                .filter(aClass -> !exclusions.contains(aClass) && Arrays.asList(aClass.getInterfaces()).contains(CustomArgumentType.class)).toList()) {
            CustomArgumentType<?> o = (CustomArgumentType<?>) containerInstanceMap.get(customArgumentClass.getName());

            handlerMethodArgumentResolvers.add(new HndCustomArg(o));
            commandArgumentResolvers.add(new CmdCustomArg(o));
        }
    }

    private void scanEnumArguments(Reflections reflections) throws ArnException {
        List<Class<?>> classes = reflections.getTypesAnnotatedWith(Container.class).stream()
                .filter(aClass -> aClass.isAnnotationPresent(CustomArgument.class) && aClass.isEnum() && !exclusions.contains(aClass)).toList();
        for (Class<?> aClass : classes) {
            Class<? extends Enum<?>> enumC = (Class<? extends Enum<?>>) aClass;

            CustomArgument customArgument = enumC.getAnnotation(CustomArgument.class);
            try {
                NamespacedKey.fromString(customArgument.value());
            } catch (Exception e) {
                throw SpigotArnExceptions.CA_VALUE_NOT_KEY.create(aClass);
            }

            if (enumC.getEnumConstants().length == 0)
                throw SpigotArnExceptions.CA_NO_CONSTANTS.create(enumC);
            if (Arrays.stream(enumC.getEnumConstants())
                    .anyMatch(constant -> !constant.name().toUpperCase(Locale.ENGLISH).equals(constant.name())))
                throw SpigotArnExceptions.CA_LOWERCASE.create(enumC);

            handlerMethodArgumentResolvers.add(new HndEnumArg(enumC));
            commandArgumentResolvers.add(new CmdEnumArg(enumC));
        }
    }

    private void createContainerInstances(Reflections reflections) throws ArnException {
        for (Class<?> clazz : reflections.getTypesAnnotatedWith(Container.class)) {
            if (clazz.isInterface() || clazz.isAnnotation() || clazz.isEnum())
                continue;

            try {
                Constructor<?> ctor = clazz.getConstructor();
                ctor.setAccessible(true);
                Object o = ctor.newInstance();
                containerInstanceMap.put(clazz.getName(), o);
            } catch (Exception e) {
                throw SpigotArnExceptions.CONTAINER_INSTANTIATE.create(clazz, e);
            }
        }
    }

    private void configure() {
        SpigotArnConfig configurer = new SpigotArnConfig();
        configurer.addArgumentResolvers(commandArgumentResolvers);
        configurer.addHandlerMethodArgumentResolvers(handlerMethodArgumentResolvers);
        configurer.putArgumentResolverExceptions(commandArgumentResolverExceptions);
        configurer.putHandlerMethodArgumentResolverExceptions(handlerExceptions);
        configured = true;
    }

    private void scanConfigurers(Reflections reflections) {
        List<Class<?>> configurers = reflections.getTypesAnnotatedWith(Container.class).stream()
                .filter(SpArnConfig.class::isAssignableFrom).toList();

        for (Class<?> configurer : configurers) {
            if (exclusions.contains(configurer)) continue;
            Class<? extends SpArnConfig> clazz = (Class<? extends SpArnConfig>) configurer;

            SpArnConfig configurerInstance = (SpArnConfig) containerInstanceMap.get(clazz.getName());
            configurerInstance.addHandlerMethodArgumentResolvers(handlerMethodArgumentResolvers);
            configurerInstance.addArgumentResolvers(commandArgumentResolvers);
            configurerInstance.putArgumentResolverExceptions(commandArgumentResolverExceptions);
            configurerInstance.putHandlerMethodArgumentResolverExceptions(handlerExceptions);
        }
    }

    private void scanCommands(Reflections reflections) throws ArnException {
        Set<Class<?>> containers = reflections.getTypesAnnotatedWith(Container.class);

        for (Class<?> container : containers)
            if (!exclusions.contains(container))
                for (Method method : container.getMethods())
                    if (method.isAnnotationPresent(Command.class))
                        command(method.getAnnotation(Command.class), method);

    }

    private void command(Command annotation, Method method) throws ArnException {
        // Errors
        if (!method.getReturnType().equals(int.class))
            throw SpigotArnExceptions.HM_NOT_INT.create(method, annotation);
        List<Class<?>> exceptions = Arrays.asList(method.getExceptionTypes());
        if (exceptions.size() > 1 || (!exceptions.isEmpty() && exceptions.stream().anyMatch(
                aClass -> !aClass.equals(CommandSyntaxException.class) && !aClass.equals(ArnSyntaxException.class))))
            throw SpigotArnExceptions.HM_THROWS.create(method, annotation, exceptions);

        long count = Arrays.stream(method.getParameters())
                .filter(parameter -> REQUIRED_SENDER_CLASSES.contains(parameter.getType())
                        && !parameter.isAnnotationPresent(CommandArgument.class))
                .count();
        if (count > 1)
            throw SpigotArnExceptions.HM_MULTIPLE_SENDERS.create(method, annotation);

        for (Parameter parameter : method.getParameters()) {
            if (handlerMethodArgumentResolvers.stream().noneMatch(car -> car.isApplicable(parameter)))
                throw SpigotArnExceptions.HM_NOT_APPLICABLE.create(method, annotation, parameter);
            if (handlerMethodArgumentResolvers.stream()
                    .anyMatch(car -> car.isApplicable(parameter) && car.requireCommandArgument())
                    && commandArgumentResolvers.stream().noneMatch(car -> car.isApplicable(parameter)))
                throw SpigotArnExceptions.HM_NOT_APPLICABLE.create(method, annotation, parameter);
        }

        SpigotCommandHandlerMethod commandHandlerMethod = createHandlerMethod(annotation, method);

        if (handlers.stream().anyMatch(method1 -> commandHandlerMethod.getSignature().equals(method1.getSignature())))
            throw SpigotArnExceptions.HM_DUPLICATE.create(commandHandlerMethod);
        for (CommandAnnotationLiteral literal : commandHandlerMethod.getAnnotationData().getLiterals()) {
            if (literal.getOffset() < 0)
                throw SpigotArnExceptions.LITERAL_NEG_OFFSET.create(annotation);
            if (!literal.getLiteral().matches("^[a-z]+$"))
                throw SpigotArnExceptions.LITERAL_ILLEGAL.create(literal, annotation);
        }
        handlers.add(commandHandlerMethod);
    }

    private SpigotCommandHandlerMethod createHandlerMethod(Command annotation, Method method) throws ArnException {
        SpigotCommandHandlerMethod commandHandlerMethod = new SpigotCommandHandlerMethod();

        commandHandlerMethod.setCommand(annotation.value());
        commandHandlerMethod.setMethod(method);
        commandHandlerMethod.setParameters(Arrays.asList(method.getParameters()));
        commandHandlerMethod.setBlocksCommandBlock(isApplied(method, BlockCommandBlock.class));
        commandHandlerMethod.setBlocksConsole(isApplied(method, BlockConsole.class));
        commandHandlerMethod.setBlocksPlayer(isApplied(method, BlockPlayer.class));

        if (isApplied(method, OnlyAllowSender.class))
            commandHandlerMethod.setIncludedSender(getApplied(method, OnlyAllowSender.class).value());
        else if (isApplied(method, BlockSenderTypes.class))
            for (Class<?> aClass : getApplied(method, BlockSenderTypes.class).value())
                commandHandlerMethod.addSenderBlock(aClass);

        CommandAnnotationData baseAnnData = new CommandAnnotationData(annotation);

        if (baseAnnData.getDescription().isEmpty())
            baseAnnData.setDescription(Optional.ofNullable(getApplied(method, Description.class)).map(Description::value).orElse("No description provided."));
        if (baseAnnData.getPermission().isEmpty())
            baseAnnData.setPermission(Optional.ofNullable(getApplied(method, Permission.class)).map(Permission::value).orElse(""));

        ArrayList<CommandAnnotationLiteral> literals = new ArrayList<>();
        for (String s : annotation.value().split("\\" + CommandAnnotationLiteral.SEPARATOR_CHAR_STRING))
            literals.add(CommandAnnotationLiteral.parse(s));

        baseAnnData.setLiterals(literals);

        commandHandlerMethod.setAnnotationData(baseAnnData);

        ArrayList<SpigotCmdResolver> argumentResolvers = new ArrayList<>();
        ArrayList<SpigotHndResolver> handlerMethodResolvers = new ArrayList<>();
        StringBuilder signature = buildSignature(method, handlerMethodResolvers, argumentResolvers);

        commandHandlerMethod.setArgumentResolvers(argumentResolvers);
        commandHandlerMethod.setHandlerMethodResolvers(handlerMethodResolvers);

        commandHandlerMethod.setSignature(signature.toString());
        return commandHandlerMethod;
    }

    private StringBuilder buildSignature(Method method, ArrayList<SpigotHndResolver> handlerMethodResolvers,
                                         ArrayList<SpigotCmdResolver> argumentResolvers) throws ArnCommandException {
        StringBuilder signatureBuilder = new StringBuilder();
        signatureBuilder.append(method.getAnnotation(Command.class).value());
        signatureBuilder.append("(");
        for (int i = 0; i < method.getParameters().length; i++) {
            Parameter parameter = method.getParameters()[i];

            if (i != 0)
                signatureBuilder.append(",");
            signatureBuilder.append(parameter.getType().getName());
            SpigotHndResolver handlerMethodArgumentResolver = this.handlerMethodArgumentResolvers
                    .stream()
                    .filter(resolver -> resolver.isApplicable(parameter) && handlerExceptions.get(resolver.getClass())
                            .stream().noneMatch(parameter::isAnnotationPresent))
                    .findFirst().orElseThrow(() -> SpigotArnExceptions.HM_NO_RESOLVER_ACCESS
                            .create(signatureBuilder.append(")").toString()));

            handlerMethodResolvers.add(handlerMethodArgumentResolver);

            if (handlerMethodArgumentResolver.requireCommandArgument())
                argumentResolvers.add(this.commandArgumentResolvers.stream()
                        .filter(resolver -> resolver.isApplicable(parameter) && commandArgumentResolverExceptions
                                .get(resolver.getClass()).stream().noneMatch(parameter::isAnnotationPresent))
                        .findFirst().orElseThrow());
            else
                argumentResolvers.add(null);
        }
        signatureBuilder.append(")");
        return signatureBuilder;
    }

    private void registerCommands() throws ArnException {
        CommandDispatcher<CommandSourceStack> dispatcher = ((CraftServer) Bukkit.getServer()).getHandle().getServer()
                .getCommands().getDispatcher();

        for (SpigotCommandHandlerMethod method : handlers) {
            try {
                List<ArgumentBuilder<CommandSourceStack, ?>> nodes = new ArrayList<>();

                // initialize lists
                List<CommandAnnotationLiteral> literals = method.getAnnotationData().getLiterals();

                List<Integer> indexesToDelete = new ArrayList<>();

                for (int i = 0; i < method.getArgumentResolvers().size(); i++)
                    if (method.getArgumentResolvers().get(i) == null)
                        indexesToDelete.add(i);

                List<SpigotCmdResolver> nonnullResolvers = IntStream.range(0, method.getArgumentResolvers().size())
                        .filter(i -> !indexesToDelete.contains(i)).mapToObj(method.getArgumentResolvers()::get)
                        .toList();
                List<Parameter> parametersClone = IntStream.range(0, method.getArgumentResolvers().size())
                        .filter(i -> !indexesToDelete.contains(i)).mapToObj(method.getParameters()::get).toList();

                Predicate<CommandSourceStack> senderPredicate = commandSourceStack -> (method.getIncludedSender()==null || method.getIncludedSender()==commandSourceStack.getBukkitSender().getClass()) &&
                        (!method.isBlocksCommandBlock() || !(commandSourceStack.getBukkitSender() instanceof BlockCommandSender)) &&
                        (!method.isBlocksPlayer() || !(commandSourceStack.getBukkitSender() instanceof Player)) &&
                        (!method.isBlocksConsole() || !(commandSourceStack.getBukkitSender() instanceof ConsoleCommandSender)) &&
                        !method.doesBlockSender(commandSourceStack.getBukkitSender());
                Predicate<CommandSourceStack> permissionPredicate = method.getAnnotationData().getPermission().isEmpty() ? s -> true : s -> s.getBukkitSender().hasPermission(method.getAnnotationData().getPermission());

                for (CommandAnnotationLiteral lit : literals)
                    if (lit.getOffset() == 0) {
                        nodes.add(Commands.literal(lit.getLiteral()).requires(permissionPredicate).requires(senderPredicate));
                    }

                for (int i = 0; i < nonnullResolvers.size(); i++) {
                    SpigotCmdResolver resolver = nonnullResolvers.get(i);

                    if (i != 0)
                        for (CommandAnnotationLiteral lit : literals)
                            if (lit.getOffset() == i)
                                nodes.add(Commands.literal(lit.getLiteral()).requires(permissionPredicate));

                    ArgumentBuilder<CommandSourceStack, ?> builder = resolver.apply(parametersClone.get(i));
                    if (builder != null)
                        nodes.add(builder);
                }

                for (CommandAnnotationLiteral lit : literals)
                    if (lit.getOffset() == nonnullResolvers.size() && lit.getOffset() != 0)
                        nodes.add(Commands.literal(lit.getLiteral()));
                com.mojang.brigadier.Command<CommandSourceStack> lambda = createCommandLambda(method);

                LiteralArgumentBuilder<CommandSourceStack> builder = (LiteralArgumentBuilder<CommandSourceStack>) chainArgumentBuilders(
                        nodes, lambda, method.getAnnotationData());

                dispatcher.register(builder);
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage(method.toString());
                throw SpigotArnExceptions.COMMAND_REGISTER_ERROR.create(method, e);
            }

        }
    }

    private com.mojang.brigadier.Command<CommandSourceStack> createCommandLambda(SpigotCommandHandlerMethod method) {
        return commandContext -> {

            CommandSender sender = commandContext.getSource().getBukkitSender();
            if (!method.getAnnotationData().getPermission().isEmpty()
                    && !sender.hasPermission(method.getAnnotationData().getPermission()))
                return 1;
            if (method.isBlocksConsole() && sender instanceof ConsoleCommandSender)
                throw CONSOLE_BLOCKED_EXCEPTION.create();
            if (method.isBlocksCommandBlock() && sender instanceof BlockCommandSender)
                throw CM_BLOCKED_EXCEPTION.create();
            if (method.isBlocksPlayer() && sender instanceof Player)
                throw PLAYER_BLOCKED_EXCEPTION.create();

            List<Object> objects;
            try {
                objects = fillResolvers(method, commandContext);
            } catch (ArnSyntaxException e) {
                throw SpigotArn.GENERIC.create(e.getMessage());
            }

            Method actualMethodToInvoke = method.getMethod();

            try {
                actualMethodToInvoke.setAccessible(true);
                return (int) actualMethodToInvoke.invoke(
                        containerInstanceMap.get(method.getMethod().getDeclaringClass().getName()), objects.toArray());
            } catch (InvocationTargetException e) {
                Throwable ex = e.getCause();
                if (ex == null)
                    return 1;
                if (ex instanceof CommandSyntaxException)
                    throw (CommandSyntaxException) ex;
                else if (ex instanceof ArnSyntaxException)
                    throw GENERIC.create(ex.getMessage());
                else
                    try {

                        Optional<SpigotExceptionHandlerMethod> handlerMethodOptional = findHandlerMethod(ex);
                        if (handlerMethodOptional.isEmpty())
                            throw GENERIC.create(ex.getMessage());
                        SpigotExceptionHandlerMethod handlerMethod = handlerMethodOptional.get();
                        List<Object> list = handlerMethod.fillParams(ex, commandContext);
                        Method actualHandlerMethod = handlerMethod.getMethod();
                        actualHandlerMethod.invoke(
                                containerInstanceMap.get(actualHandlerMethod.getDeclaringClass().getName()),
                                list.toArray());

                    } catch (Exception exe) {
                        throw GENERIC.create(exe.getMessage());
                    }
                return 1;
            } catch (IllegalAccessException e) {
                SpigotArnExceptions.COMMAND_NO_ACCESS.create().initCause(e).printStackTrace();
                return 1;
            }

        };
    }

    @Override
    public ArnInstance excludeClass(Class<?> clazz) {
        exclusions.add(clazz);
        return this;
    }

    private void registerHelpers(Reflections reflections) {
        CommandDispatcher<CommandSourceStack> dispatcher = ((CraftServer) Bukkit.getServer()).getHandle().getServer()
                .getCommands().getDispatcher();

        for (Class<?> helperClass : reflections.getTypesAnnotatedWith(Container.class).stream()
                .filter(aClass -> !exclusions.contains(aClass) && aClass.isAnnotationPresent(Helper.class)).toList()) {
            List<SpigotCommandHandlerMethod> associatedHelperMethods = handlers.stream().filter(
                            commandHandlerMethod -> commandHandlerMethod.getMethod().getDeclaringClass().equals(helperClass))
                    .toList();

            com.mojang.brigadier.Command<CommandSourceStack> lambda = (s) -> {
                CommandSender sender = s.getSource().getBukkitSender();

                for (SpigotCommandHandlerMethod helperMethod : associatedHelperMethods) {
                    Supplier<Boolean> isDisabled = sender instanceof Player ? helperMethod::isBlocksPlayer
                            : (sender instanceof ConsoleCommandSender ? helperMethod::isBlocksConsole
                            : helperMethod::isBlocksCommandBlock);
                    if (isDisabled.get())
                        continue;

                    String permission = helperMethod.getAnnotationData().getPermission();
                    if (permission != null && !sender.hasPermission(permission))
                        continue;

                    StringBuilder builder = new StringBuilder().append(ChatColor.GRAY).append("/");

                    int adcI = 0;
                    List<Parameter> a = helperMethod.getParameters().stream()
                            .filter(parameter -> parameter.isAnnotationPresent(CommandArgument.class)).toList();

                    for (CommandAnnotationLiteral lit : helperMethod.getAnnotationData().getLiterals())
                        if (lit.getOffset() == 0)
                            builder.append(ChatColor.GRAY).append(lit.getLiteral()).append(" ");

                    for (int i = 0; i < a.size(); i++) {

                        if (i != 0)
                            for (CommandAnnotationLiteral lit : helperMethod.getAnnotationData().getLiterals())
                                if (lit.getOffset() == i)
                                    builder.append(ChatColor.GRAY).append(lit.getLiteral()).append(" ");

                        Parameter parameter = a.get(i);
                        builder.append(ARGUMENT_DISPLAY_COLORS.get((adcI++) % 5)).append("<").append(parameter.getName()).append("> ");
                    }

                    BaseComponent component = TextComponent.fromLegacy(builder.toString());

                    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new Text(new TextComponent(helperMethod.getAnnotationData().getDescription()))));
                    sender.spigot().sendMessage(component);
                }

                return 0;
            };

            ArrayList<CommandAnnotationLiteral> literals = new ArrayList<>();
            for (String s : helperClass.getAnnotation(Helper.class).value()
                    .split("\\" + CommandAnnotationLiteral.SEPARATOR_CHAR_STRING))
                literals.add(CommandAnnotationLiteral.parse(s));

            List<ArgumentBuilder<CommandSourceStack, ?>> builders = literals.stream()
                    .map(commandAnnotationLiteral -> Commands.literal(commandAnnotationLiteral.getLiteral()))
                    .collect(Collectors.toList());
            ArgumentBuilder<CommandSourceStack, ?> finalNode = chainArgumentBuilders(builders, lambda, null);

            dispatcher.register(((LiteralArgumentBuilder<CommandSourceStack>) finalNode));
        }

    }

    @Override
    public List<ArnFeature> getSupportedFeatures() {
        return List.of(ArnFeature.ALL);
    }

}