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

package dev.efekos.arn.paper;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.efekos.arn.common.ArnInstance;
import dev.efekos.arn.common.annotation.Command;
import dev.efekos.arn.common.annotation.CommandArgument;
import dev.efekos.arn.common.annotation.Container;
import dev.efekos.arn.common.annotation.block.BlockCommandBlock;
import dev.efekos.arn.common.annotation.block.BlockConsole;
import dev.efekos.arn.common.annotation.block.BlockPlayer;
import dev.efekos.arn.common.data.CommandAnnotationData;
import dev.efekos.arn.common.data.CommandAnnotationLiteral;
import dev.efekos.arn.common.data.ExceptionMap;
import dev.efekos.arn.common.exception.ArnCommandException;
import dev.efekos.arn.common.exception.ArnException;
import dev.efekos.arn.common.exception.ArnSyntaxException;
import dev.efekos.arn.paper.face.PaperArnConfig;
import dev.efekos.arn.paper.face.PaperCmdResolver;
import dev.efekos.arn.paper.face.PaperHndResolver;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public final class PaperArn extends PaperMethodDump implements ArnInstance {

    // You cannot imagine the happiness in me seeing all these lists clearly without any comments
    private final List<PaperCmdResolver> commandResolvers = new ArrayList<>();
    private final ExceptionMap<PaperCmdResolver> commandResolverExceptions = new ExceptionMap<>();
    private final ExceptionMap<PaperHndResolver> handlerResolverExceptions = new ExceptionMap<>();
    private final List<PaperHndResolver> handlerResolvers = new ArrayList<>();
    private final List<PaperCommandMethod> commandMethods = new ArrayList<>();
    private final List<ArgumentBuilder<CommandSourceStack, ?>> finalNodes = new ArrayList<>();
    private boolean configured = false;

    private <T> T instantiate(Class<T> clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    private void configure(Reflections reflections) {
        if (configured) return;
        configured = true;

        List<PaperArnConfig> list = new ArrayList<>(List.of(new PaperArnConfigurer()));

        for (Class<? extends PaperArnConfig> aClass : reflections.getSubTypesOf(PaperArnConfig.class)) {
            PaperArnConfig instantiate = instantiate(aClass);
            if (instantiate != null) list.add(instantiate);
        }

        for (PaperArnConfig config : list) {
            config.addArgumentResolvers(commandResolvers);
            config.addHandlerMethodArgumentResolvers(handlerResolvers);
            config.putArgumentResolverExceptions(commandResolverExceptions);
            config.putHandlerMethodArgumentResolverExceptions(handlerResolverExceptions);
        }
    }

    @Override
    public <T> void run(Class<T> mainClass, T instance) throws ArnException {
        if (!(instance instanceof Plugin plugin))
            throw new IllegalStateException("Arn#run is called with a " + mainClass.getName() + " which isn't a JavaPlugin.");
        Reflections reflections = new Reflections(mainClass.getPackage().getName());

        configure(reflections);
        scanCommands(reflections);
        scanExceptionHandlerMethods(reflections);

        registerCommands(plugin.getLifecycleManager());
    }

    private void scanCommands(Reflections reflections) throws ArnException {
        for (Class<?> aClass : reflections.getTypesAnnotatedWith(Container.class)) {
            for (Method method : aClass.getMethods()) {
                if (method.isAnnotationPresent(Command.class)) {
                    Command annotation = method.getAnnotation(Command.class);
                    command(annotation, method);
                }
            }
        }
    }

    private void command(Command ann, Method method) throws ArnException {
        // Check for return type
        if (!method.getReturnType().equals(int.class)) throw PaperArnExceptions.HM_NOT_INT.create(method, ann);

        // Check exceptions
        Optional<Class<?>> optional = Arrays.stream(method.getExceptionTypes()).filter(aClass ->
                !aClass.equals(ArnSyntaxException.class) && !aClass.equals(CommandSyntaxException.class)
        ).findFirst();
        if (optional.isPresent()) throw PaperArnExceptions.HM_THROWS.create(method, ann, optional.get());

        // Check senders
        long lC = Arrays.stream(method.getParameters()).filter(parameter -> !parameter.isAnnotationPresent(CommandArgument.class)).count();
        if (lC > 1) throw PaperArnExceptions.HM_MULTIPLE_SENDERS.create(method, ann);

        // Check applicable

        for (Parameter parameter : method.getParameters()) {
            if (handlerResolvers.stream().noneMatch(car -> car.isApplicable(parameter)))
                throw PaperArnExceptions.HM_NOT_APPLICABLE.create(method, ann, parameter);
            if (handlerResolvers.stream()
                    .anyMatch(car -> car.isApplicable(parameter) && car.requireCommandArgument())
                    && commandResolvers.stream().noneMatch(car -> car.isApplicable(parameter)))
                throw PaperArnExceptions.HM_NOT_APPLICABLE.create(method, ann, parameter);
        }


        // Create handler method
        PaperCommandMethod cmdMethod = createHandlerMethod(ann, method);

        if (commandMethods.stream().anyMatch(method1 -> cmdMethod.getSignature().equals(method1.getSignature())))
            throw PaperArnExceptions.HM_DUPLICATE.create(cmdMethod);
        for (CommandAnnotationLiteral literal : cmdMethod.getAnnotationData().getLiterals()) {
            if (literal.getOffset() < 0)
                throw PaperArnExceptions.LITERAL_NEG_OFFSET.create(ann);
            if (!literal.getLiteral().matches("^[a-z]+$"))
                throw PaperArnExceptions.LITERAL_ILLEGAL.create(literal, ann);
        }

        // Add it to list
        commandMethods.add(cmdMethod);
    }

    private PaperCommandMethod createHandlerMethod(Command annotation, Method method) throws ArnException {
        PaperCommandMethod cmdMethod = new PaperCommandMethod();

        cmdMethod.setCommand(annotation.value());
        cmdMethod.setMethod(method);
        cmdMethod.setParameters(Arrays.asList(method.getParameters()));
        cmdMethod.setBlocksCommandBlock(method.isAnnotationPresent(BlockCommandBlock.class));
        cmdMethod.setBlocksConsole(method.isAnnotationPresent(BlockConsole.class));
        cmdMethod.setBlocksPlayer(method.isAnnotationPresent(BlockPlayer.class));

        CommandAnnotationData baseAnnData = new CommandAnnotationData(annotation);

        if (baseAnnData.getDescription().isEmpty())
            baseAnnData.setDescription("No description provided.");

        ArrayList<CommandAnnotationLiteral> literals = new ArrayList<>();
        for (String s : annotation.value().split("\\" + CommandAnnotationLiteral.SEPARATOR_CHAR_STRING))
            literals.add(CommandAnnotationLiteral.parse(s));

        baseAnnData.setLiterals(literals);

        cmdMethod.setAnnotationData(baseAnnData);

        ArrayList<PaperCmdResolver> cmdResolvers = new ArrayList<>();
        ArrayList<PaperHndResolver> hndResolvers = new ArrayList<>();
        StringBuilder signature = buildSignature(method, hndResolvers, cmdResolvers);

        cmdMethod.setArgumentResolvers(cmdResolvers);
        cmdMethod.setHandlerMethodResolvers(hndResolvers);

        cmdMethod.setSignature(signature.toString());
        return cmdMethod;
    }


    private StringBuilder buildSignature(Method method, ArrayList<PaperHndResolver> handlerMethodResolvers, ArrayList<PaperCmdResolver> argumentResolvers) throws ArnCommandException {
        StringBuilder signatureBuilder = new StringBuilder();
        signatureBuilder.append(method.getAnnotation(Command.class).value());
        signatureBuilder.append("(");
        for (int i = 0; i < method.getParameters().length; i++) {
            Parameter parameter = method.getParameters()[i];

            if (i != 0)
                signatureBuilder.append(",");
            signatureBuilder.append(parameter.getType().getName());
            PaperHndResolver handlerMethodArgumentResolver = handlerResolvers
                    .stream()
                    .filter(resolver -> resolver.isApplicable(parameter) && handlerResolverExceptions.get(resolver.getClass())
                            .stream().noneMatch(parameter::isAnnotationPresent))
                    .findFirst().orElseThrow(() -> PaperArnExceptions.HM_NO_RESOLVER_ACCESS
                            .create(signatureBuilder.append(")").toString()));

            handlerMethodResolvers.add(handlerMethodArgumentResolver);

            if (handlerMethodArgumentResolver.requireCommandArgument())
                argumentResolvers.add(this.commandResolvers.stream()
                        .filter(resolver -> resolver.isApplicable(parameter) && commandResolverExceptions
                                .get(resolver.getClass()).stream().noneMatch(parameter::isAnnotationPresent))
                        .findFirst().orElseThrow());
            else
                argumentResolvers.add(null);
        }
        signatureBuilder.append(")");
        return signatureBuilder;
    }

    public void registerCommands(@NotNull LifecycleEventManager<Plugin> lifecycleManager) {

        for (PaperCommandMethod method : commandMethods) {

            ArrayList<ArgumentBuilder<CommandSourceStack, ?>> nodes = new ArrayList<>();
            List<CommandAnnotationLiteral> literals = method.getAnnotationData().getLiterals();

            // Register first literals as they are head of the command, and they will need #requires for permissions
            for (CommandAnnotationLiteral lit : literals) {
                if (lit.getOffset() == 0) {
                    Predicate<CommandSourceStack> predicate = method.getAnnotationData().getPermission().isEmpty() ? s -> true : s -> s.getSender().hasPermission(method.getAnnotationData().getPermission());
                    nodes.add(Commands.literal(lit.getLiteral()).requires(predicate));
                }
            }

            // Okay this part was straight up cloned from 5-month-old spigot code and I will not bother myself doing commentary on it
            List<Integer> indexesToDelete = new ArrayList<>();

            for (int i = 0; i < method.getArgumentResolvers().size(); i++)
                if (method.getArgumentResolvers().get(i) == null)
                    indexesToDelete.add(i);

            List<PaperCmdResolver> nonnullResolvers = IntStream.range(0, method.getArgumentResolvers().size())
                    .filter(i -> !indexesToDelete.contains(i)).mapToObj(method.getArgumentResolvers()::get)
                    .toList();
            List<Parameter> parametersClone = IntStream.range(0, method.getArgumentResolvers().size())
                    .filter(i -> !indexesToDelete.contains(i)).mapToObj(method.getParameters()::get).toList();

            for (int i = 0; i < nonnullResolvers.size(); i++) {
                PaperCmdResolver resolver = nonnullResolvers.get(i);

                if (i != 0)
                    for (CommandAnnotationLiteral lit : literals)
                        if (lit.getOffset() == i)
                            nodes.add(Commands.literal(lit.getLiteral()));

                ArgumentBuilder<CommandSourceStack, ?> builder = resolver.apply(parametersClone.get(i));
                if (builder != null)
                    nodes.add(builder);
            }

            // Chain up builders
            ArgumentBuilder<CommandSourceStack, ?> finalNode = nodes.getLast().executes(createCommandLambda(method));
            for (int i = nodes.size()-2; i >= 0; i--) finalNode = nodes.get(i).then(finalNode);
            if (finalNode == null) continue;
            finalNodes.add(finalNode);
        }

        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS, e -> {
            Commands registrar = e.registrar();
            for (ArgumentBuilder<CommandSourceStack, ?> node : finalNodes) {
                registrar.register(((LiteralArgumentBuilder<CommandSourceStack>) node).build());
            }
        });

    }

    private com.mojang.brigadier.Command<CommandSourceStack> createCommandLambda(PaperCommandMethod method) {
        return commandContext -> {

            CommandSender sender = commandContext.getSource().getSender();
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
                throw GENERIC.create(e.getMessage());
            }

            Method actualMethodToInvoke = method.getMethod();

            try {
                actualMethodToInvoke.setAccessible(true);
                return (int) actualMethodToInvoke.invoke(
                        instantiate(method.getMethod().getDeclaringClass()), objects.toArray());
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

                        Optional<PaperExceptionMethod> exceptionMethodOptional = findHandlerMethod(ex);
                        if (exceptionMethodOptional.isEmpty())
                            throw GENERIC.create(ex.getMessage());
                        PaperExceptionMethod exceptionMethod = exceptionMethodOptional.get();
                        List<Object> list = exceptionMethod.fillParams(ex, commandContext);
                        Method actualMethod = exceptionMethod.getMethod();
                        actualMethod.invoke(
                                instantiate(actualMethod.getDeclaringClass()),
                                list.toArray());

                    } catch (Exception exe) {
                        throw GENERIC.create(exe.getMessage());
                    }
                return 1;
            } catch (IllegalAccessException e) {
                PaperArnExceptions.COMMAND_NO_ACCESS.create().initCause(e).printStackTrace();
                return 1;
            }

        };
    }

}
