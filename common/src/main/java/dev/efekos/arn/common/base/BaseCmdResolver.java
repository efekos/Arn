/*
 * MIT License
 *
 * Copyright (c) 2025 efekos
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

package dev.efekos.arn.common.base;

import dev.efekos.arn.common.annotation.CommandArgument;

import java.lang.reflect.Parameter;


/**
 * Represents a resolver that can create an {@link T} from a {@link Parameter} of a
 * {@link BaseCommandHandlerMethod}. Unlike {@link BaseHndResolver}s, there can be
 * {@link Parameter}s that doesn't have a CommandArgumentResolver. If
 * {@link BaseHndResolver#requireCommandArgument()} returns {@code false} for a parameter, Arn
 * won't search for a CommandArgumentResolver for that parameter.
 *
 * @author efekos
 * @since 0.1
 */
public interface BaseCmdResolver<T> {

    /**
     * Returns whether this {@link BaseCmdResolver} can resolve {@code parameter}. Keep in mind that there
     * shouldn't be more than one {@link BaseCmdResolver} that can resolver the same parameter.
     *
     * @param parameter A parameter of a {@link BaseCommandHandlerMethod}.
     * @return {@code true} if this {@link Parameter} should be resolved using this {@link BaseCmdResolver},
     * {@code false} otherwise.
     */
    boolean isApplicable(Parameter parameter);

    /**
     * Creates a {@link T} that will represent {@code parameter} in the command structure.
     *
     * @param parameter A parameter of a {@link BaseCommandHandlerMethod}.
     * @return An {@link T} that represents {@code parameter}.
     */
    T apply(Parameter parameter);

    default String getName(Parameter parameter) {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        return s.isEmpty() ? parameter.getName() : s;
    }

}
