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

package dev.efekos.arn.resolver;

import com.mojang.brigadier.builder.ArgumentBuilder;
import dev.efekos.arn.common.resolver.CommandHandlerMethodArgumentResolver;

import java.lang.reflect.Parameter;


/**
 * Represents a resolver that can create an {@link ArgumentBuilder} from a {@link Parameter} of a
 * {@link dev.efekos.arn.common.data.CommandHandlerMethod}. Unlike {@link dev.efekos.arn.common.resolver.CommandHandlerMethodArgumentResolver}s, there can be
 * {@link Parameter}s that doesn't have a CommandArgumentResolver. If
 * {@link CommandHandlerMethodArgumentResolver#requireCommandArgument()} returns {@code false} for a parameter,
 * {@link dev.efekos.arn.Arn} won't search for a CommandArgumentResolver for that parameter.
 *
 * @author efekos
 * @since 0.1
 */
public interface CommandArgumentResolver {

    /**
     * Returns whether this {@link dev.efekos.arn.common.resolver.CommandArgumentResolver} can resolve {@code parameter}. Keep in mind that there
     * shouldn't be more than one {@link dev.efekos.arn.common.resolver.CommandArgumentResolver} that can resolver the same parameter.
     *
     * @param parameter A parameter of a {@link dev.efekos.arn.common.data.CommandHandlerMethod}.
     * @return {@code true} if this {@link Parameter} should be resolved using this {@link dev.efekos.arn.common.resolver.CommandArgumentResolver},
     * {@code false} otherwise.
     */
    boolean isApplicable(Parameter parameter);

    /**
     * Creates a {@link ArgumentBuilder} that will represent {@code parameter} in the command structure.
     *
     * @param parameter A parameter of a {@link dev.efekos.arn.common.data.CommandHandlerMethod}.
     * @return An {@link ArgumentBuilder} that represents {@code parameter}.
     */
    ArgumentBuilder apply(Parameter parameter);

}
