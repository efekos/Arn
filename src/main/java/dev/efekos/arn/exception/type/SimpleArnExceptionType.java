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

package dev.efekos.arn.exception.type;

import dev.efekos.arn.exception.ArnException;

import java.util.function.Supplier;

/**
 * An exception creator that creates a simple exception without using any arguments.
 *
 * @param <E>  Type of the actual exception.
 * @author efekos
 * @since 0.3
 */
public final class SimpleArnExceptionType<E extends ArnException> {

    /**
     * The function used to create the exception.
     */
    private final Supplier<E> lambda;

    /**
     * Creates a new exception type.
     *
     * @param lambda Supplier function to create the exception.
     */
    public SimpleArnExceptionType(Supplier<E> lambda) {
        this.lambda = lambda;
    }

    /**
     * Creates an exception using {@link #lambda}.
     *
     * @return Created exception.
     */
    public E create() {
        return lambda.get();
    }

}
