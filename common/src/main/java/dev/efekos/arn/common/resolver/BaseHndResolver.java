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

package dev.efekos.arn.common.resolver;

import dev.efekos.arn.common.data.BaseCommandHandlerMethod;
import dev.efekos.arn.common.exception.ArnSyntaxException;

import java.lang.reflect.Parameter;

/**
 * Represents a resolver that can take a {@link Parameter} from a {@link BaseCommandHandlerMethod}, and figure out what
 * should be passed in to it while invoking the method.
 *
 * @author efekos
 * @since 0.1
 */
public interface BaseHndResolver<Ctx, Method extends BaseCommandHandlerMethod<?, ?>> {


    /**
     * Returns whether this {@link BaseHndResolver} can resolve {@code parameter}. Keep in mind,
     * that there shouldn't be more than one {@link BaseHndResolver} that can resolver the same
     * parameter.
     *
     * @param parameter A parameter of a {@link BaseCommandHandlerMethod}.
     * @return {@code true} if this {@link Parameter} should be resolved using this
     * {@link BaseHndResolver}, {@code false} otherwise.
     */
    boolean isApplicable(Parameter parameter);

    /**
     * Should return true if there is a {@link BaseCmdResolver} assigned to this resolver.
     *
     * @return Whether Arn should search for a {@link BaseCmdResolver} when a {@link Parameter} is resolvable
     * by this resolver.
     */
    boolean requireCommandArgument();

    /**
     * Resolves value of an argument into an {@link Object}.
     *
     * @param parameter The {@link Parameter} that was associated with this resolver in the first place.
     * @param method    Main {@link BaseCommandHandlerMethod} in case something from there is needed.
     * @param context   Command context to get arguments from the executed command.
     * @return An object to be passed in to {@code parameter}.
     * @throws ArnSyntaxException if needed.
     */
    Object resolve(Parameter parameter, Method method, Ctx context) throws ArnSyntaxException;

}