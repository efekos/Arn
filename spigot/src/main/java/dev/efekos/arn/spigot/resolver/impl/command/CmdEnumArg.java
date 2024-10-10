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

package dev.efekos.arn.spigot.resolver.impl.command;


import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import dev.efekos.arn.common.annotation.CommandArgument;
import dev.efekos.arn.common.annotation.CustomArgument;
import dev.efekos.arn.common.resolver.CommandArgumentResolver;
import dev.efekos.arn.spigot.Arn;
import net.minecraft.commands.Commands;

import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * An implementation of {@link CommandArgumentResolver}. Resolves custom {@link Enum} arguments that is annotated with
 * {@link CustomArgument}.
 *
 * @author efekos
 * @since 0.1
 */
public final class CmdEnumArg implements CommandArgumentResolver {

    /**
     * Main {@link Enum} class this resolver will handle.
     */
    private final Class<? extends Enum<?>> enumClass;
    /**
     * Constants of {@link #enumClass} in lower-case.
     */
    private final List<String> constants;

    /**
     * Creates a new enumerator resolver. This class is not a static argument resolver. It is dynamically added by
     * {@link Arn} for every {@link Enum} that is annotated with {@link CustomArgument}.
     *
     * @param enumClass Main {@link Enum} class this resolver will handle.
     */
    public CmdEnumArg(Class<? extends Enum<?>> enumClass) {
        this.enumClass = enumClass;
        this.constants = Arrays.stream(enumClass.getEnumConstants()).map(Enum::name).map(s -> s.toLowerCase(Locale.ENGLISH)).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.getType().equals(enumClass) && parameter.isAnnotationPresent(CommandArgument.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArgumentBuilder apply(Parameter parameter) {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        return Commands.argument(s.isEmpty() ? parameter.getName() : s, StringArgumentType.word()).suggests((context, builder) -> {
            for (String constant : constants)
                if (constant.startsWith(builder.getRemainingLowerCase())) builder.suggest(constant);
            return builder.buildFuture();
        });
    }
}
