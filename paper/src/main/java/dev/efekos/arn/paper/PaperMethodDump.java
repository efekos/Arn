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

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.efekos.arn.common.annotation.Container;
import dev.efekos.arn.common.annotation.ExceptionHandler;
import dev.efekos.arn.common.exception.ArnSyntaxException;
import dev.efekos.arn.paper.face.PaperHndResolver;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public sealed class PaperMethodDump permits PaperArn {

    /**
     * An exception type thrown by command handler when a command is blocked to
     * {@link ConsoleCommandSender}s, but the
     * command sender is a {@link ConsoleCommandSender}.
     */
    public static final SimpleCommandExceptionType CONSOLE_BLOCKED_EXCEPTION = new SimpleCommandExceptionType(
            new LiteralMessage("This command can't be used by the console."));
    /**
     * An exception type thrown by command handler when a command is blocked to
     * {@link BlockCommandSender}s, but the
     * command sender is a {@link BlockCommandSender}.
     */
    public static final SimpleCommandExceptionType CM_BLOCKED_EXCEPTION = new SimpleCommandExceptionType(
            new LiteralMessage("This command can't be used by command blocks."));
    /**
     * An exception type thrown by command handler when a command is blocked to
     * {@link Player}s, but the command sender
     * is a {@link Player}.
     */
    public static final SimpleCommandExceptionType PLAYER_BLOCKED_EXCEPTION = new SimpleCommandExceptionType(
            new LiteralMessage("This command can't be used by players."));
    /**
     * Generic exception type used to handle {@link ArnSyntaxException}s.
     */
    public static final DynamicCommandExceptionType GENERIC = new DynamicCommandExceptionType(
            o -> new LiteralMessage(o.toString()));
    protected final List<PaperExceptionMethod> exceptionMethods = new ArrayList<>();

    protected static List<Object> fillResolvers(PaperCommandMethod method,
                                                CommandContext<CommandSourceStack> commandContext) throws ArnSyntaxException {
        List<Object> objects = new ArrayList<>();

        for (int i = 0; i < method.getHandlerMethodResolvers().size(); i++) {
            PaperHndResolver resolver = method.getHandlerMethodResolvers().get(i);
            objects.add(resolver.resolve(method.getParameters().get(i), method, commandContext));
        }
        return objects;
    }

    protected Optional<PaperExceptionMethod> findHandlerMethod(Throwable e) {
        for (PaperExceptionMethod method : exceptionMethods)
            if (method.getExceptionClass().isAssignableFrom(e.getClass()))
                return Optional.of(method);
        return Optional.empty();
    }

    protected void scanExceptionHandlerMethods(Reflections reflections, List<Class<?>> exclusions) {
        for (Class<?> aClass : reflections.getTypesAnnotatedWith(Container.class)) {
            if (exclusions.contains(aClass)) continue;
            for (Method method : aClass.getDeclaredMethods()) {
                if (!method.isAnnotationPresent(ExceptionHandler.class))
                    continue;
                ExceptionHandler annotation = method.getAnnotation(ExceptionHandler.class);
                PaperExceptionMethod handlerMethod = new PaperExceptionMethod(method, annotation.value());
                exceptionMethods.add(handlerMethod);
            }
        }

    }

    protected <T extends Annotation> T getApplied(Method method, Class<T> annotation) {
        return Optional.ofNullable(method.getAnnotation(annotation)).orElse(Optional.ofNullable(method.getDeclaringClass().getAnnotation(annotation)).orElse(method.getDeclaringClass().getPackage().getAnnotation(annotation)));
    }

    protected boolean isApplied(Method method, Class<? extends Annotation> annotation) {
        return Optional.ofNullable(getApplied(method, annotation)).isPresent();
    }


}