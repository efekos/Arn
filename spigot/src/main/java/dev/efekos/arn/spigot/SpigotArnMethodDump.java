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

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.efekos.arn.common.annotation.Container;
import dev.efekos.arn.common.annotation.ExceptionHandler;
import dev.efekos.arn.common.data.CommandAnnotationData;
import dev.efekos.arn.common.exception.ArnSyntaxException;
import dev.efekos.arn.spigot.face.SpigotHndResolver;
import net.minecraft.commands.CommandSourceStack;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * A class to dump utility methods used in {@link SpigotArn} as a way to separate it
 * into two classes.
 *
 * @since 0.4
 */
sealed class SpigotArnMethodDump permits SpigotArn {

    private final List<SpigotExceptionHandlerMethod> baseSpigotExceptionHandlerMethods = new ArrayList<>();

    /**
     * Finds last element that matches the given condition.
     *
     * @param list      Any list.
     * @param condition A condition.
     * @param <T>       Type of the elements in the list.
     * @return Last element that matches the given condition in the list.
     */
    protected static <T> int findLastIndex(List<T> list, Predicate<T> condition) {
        for (int i = list.size() - 1; i >= 0; i--) {
            if (condition.test(list.get(i))) {
                return i;
            }
        }
        return -1; // Return null if no match is found
    }

    protected static List<Object> fillResolvers(SpigotCommandHandlerMethod method,
                                                CommandContext<CommandSourceStack> commandContext) throws ArnSyntaxException {
        List<Object> objects = new ArrayList<>();

        for (int i = 0; i < method.getHandlerMethodResolvers().size(); i++) {
            SpigotHndResolver resolver = method.getHandlerMethodResolvers().get(i);
            objects.add(resolver.resolve(method.getParameters().get(i), method, commandContext));
        }
        return objects;
    }

    protected Optional<SpigotExceptionHandlerMethod> findHandlerMethod(Throwable e) {
        for (SpigotExceptionHandlerMethod method : baseSpigotExceptionHandlerMethods)
            if (method.getExceptionClass().isAssignableFrom(e.getClass()))
                return Optional.of(method);
        return Optional.empty();
    }

    protected void scanExceptionHandlerMethods(Reflections reflections) {
        for (Class<?> aClass : reflections.getTypesAnnotatedWith(Container.class))
            for (Method method : aClass.getDeclaredMethods()) {
                if (!method.isAnnotationPresent(ExceptionHandler.class))
                    continue;
                ExceptionHandler annotation = method.getAnnotation(ExceptionHandler.class);
                SpigotExceptionHandlerMethod handlerMethod = new SpigotExceptionHandlerMethod(method, annotation.value());
                baseSpigotExceptionHandlerMethods.add(handlerMethod);
            }

    }

    /**
     * Chains given argument builders into one {@link ArgumentBuilder} that can be
     * used to register the command.
     *
     * @param nodes    List of the nodes to chain.
     * @param executes execute function to handle the command. Added to the last
     *                 argument in the chain.
     * @param data     {@link CommandAnnotationData} associated with the nodes. If
     *                 there is a permission required, it will
     *                 be applied to first literal of the chain.
     * @return {@code nodes[0]} with rest of the nodes attached to it.
     */
    protected ArgumentBuilder<CommandSourceStack, ?> chainArgumentBuilders(
            List<ArgumentBuilder<CommandSourceStack, ?>> nodes,
            com.mojang.brigadier.Command<CommandSourceStack> executes, CommandAnnotationData data) {
        if (nodes.isEmpty())
            return null;

        ArgumentBuilder<CommandSourceStack, ?> chainedBuilder = nodes.getLast().executes(executes);

        for (int i = nodes.size() - 2; i >= 0; i--)
            chainedBuilder = nodes.get(i).then(chainedBuilder);

        if (!data.getPermission().isEmpty())
            chainedBuilder = chainedBuilder.requires(o -> o.hasPermission(0, data.getPermission()));
        return chainedBuilder;
    }

}
