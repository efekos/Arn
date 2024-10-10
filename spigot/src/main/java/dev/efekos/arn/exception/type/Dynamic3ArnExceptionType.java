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

/**
 * Dynamic exception creator that takes three arguments.
 *
 * @param <T>  Type of the first argument.
 * @param <T2> Type of the second argument.
 * @param <T3> Type of the third argument.
 * @param <E>  Type of the actual exception.
 * @author efekos
 * @since 0.3
 */
public final class Dynamic3ArnExceptionType<E extends ArnException, T, T2, T3> {

    /**
     * Lambda method that takes three arguments.
     */
    private final Lambda<T, T2, T3, E> lambda;

    /**
     * Creates a new exception type.
     *
     * @param lambda Function used to create the exception.
     */
    public Dynamic3ArnExceptionType(Lambda<T, T2, T3, E> lambda) {
        this.lambda = lambda;
    }

    /**
     * Creates an exception using {@link #lambda}.
     *
     * @param o  First object.
     * @param o2 Second object.
     * @param o3 Third object.
     * @return Created exception.
     */
    public E create(T o, T2 o2, T3 o3) {
        return lambda.create(o, o2, o3);
    }

    /**
     * Lambda method that takes three arguments and returns an {@link ArnException}.
     *
     * @param <T>  Type of the first argument.
     * @param <T2> Type of the second argument.
     * @param <T3> Type of the third argument.
     * @param <E>  Type of the actual exception.
     */
    @FunctionalInterface
    public interface Lambda<T, T2, T3, E extends ArnException> {

        /**
         * Creates an {@link ArnException}.
         *
         * @param o  First object.
         * @param o2 Second object.
         * @param o3 Third object.
         * @return An {@link ArnException}.
         */
        E create(T o, T2 o2, T3 o3);

    }

}
