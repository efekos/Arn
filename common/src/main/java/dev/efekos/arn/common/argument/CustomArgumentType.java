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

package dev.efekos.arn.common.argument;

import dev.efekos.arn.common.exception.ArnSyntaxException;

import java.util.List;

/**
 * An interface used to create custom argument types. When scanned by Arn, {@link Type} becomes a usable argument type
 * that is handled by the implementation of this interface.
 *
 * @param <Type> Type of the custom argument.
 * @author efekos
 * @since 0.3.1
 */
public interface CustomArgumentType<Type, Registration, Sender> {

    /**
     * Returns class instance of the custom argument.
     *
     * @return A {@link Class} instance.
     */
    Class<Type> getType();

    /**
     * Returns a {@link Registration}, specifying how this argument should be registered.
     *
     * @return A {@link Registration}.
     */
    Registration getRegistration();

    /**
     * Suggests a list of strings to the given command sender.
     *
     * @param sender Any command sender.
     * @return A list of suggestions.
     */
    List<String> suggest(Sender sender);

    /**
     * Parses the given argument.
     *
     * @param sender Sender who sent this argument.
     * @param arg    The argument value.
     * @return Parsed object.
     * @throws ArnSyntaxException If {@code arg} is invalid.
     */
    Type parse(Sender sender, String arg) throws ArnSyntaxException;

}