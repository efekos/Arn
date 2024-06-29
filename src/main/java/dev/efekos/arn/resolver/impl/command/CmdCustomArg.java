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

package dev.efekos.arn.resolver.impl.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import dev.efekos.arn.annotation.CommandArgument;
import dev.efekos.arn.argument.CustomArgumentType;
import dev.efekos.arn.resolver.CommandArgumentResolver;
import net.minecraft.commands.Commands;

import java.lang.reflect.Parameter;

/**
 * An implementation of {@link CommandArgumentResolver}. Resolves {@link CustomArgumentType}s.
 *
 * @author efekos
 * @since 0.3.1
 */
public final class CmdCustomArg implements CommandArgumentResolver {

    /**
     * An instance of the {@link CustomArgumentType} this resolver resolves.
     */
    private final CustomArgumentType<?> customArgumentType;

    /**
     * Creates a new resolver.
     *
     * @param customArgumentType An instance of the {@link CustomArgumentType} this resolver resolves.
     */
    public CmdCustomArg(CustomArgumentType<?> customArgumentType) {
        this.customArgumentType = customArgumentType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(CommandArgument.class) && parameter.getType().equals(customArgumentType.getType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArgumentBuilder apply(Parameter parameter) {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        return Commands.argument(s.isEmpty() ? parameter.getName() : s, StringArgumentType.string()).suggests((commandContext, suggestionsBuilder) -> {
            for (String ss : customArgumentType.suggest(commandContext.getSource().getBukkitSender())) {
                suggestionsBuilder.suggest(ss);
            }
            return suggestionsBuilder.buildFuture();
        });
    }
}
