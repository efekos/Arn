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

package dev.efekos.arn.exception;

import dev.efekos.arn.data.CommandHandlerMethod;

/**
 * An exception type thrown by {@link dev.efekos.arn.Arn} when there is something wrong with a
 * {@link CommandHandlerMethod}. It might be with the command itself, or something happening while executing the
 * {@link CommandHandlerMethod#getMethod()} method.
 *
 * @author efekos
 * @since 0.1
 */
public class ArnCommandException extends ArnException {

    /**
     * Creates a new exception.
     */
    public ArnCommandException() {
    }

    /**
     * Creates a new exception.
     *
     * @param message Exception message.
     */
    public ArnCommandException(String message) {
        super(message);
    }

    /**
     * Creates a new exception.
     *
     * @param message Exception message.
     * @param cause   Exception cause.
     */
    public ArnCommandException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new exception.
     *
     * @param cause Exception cause.
     */
    public ArnCommandException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new exception.
     *
     * @param message            Exception message.
     * @param cause              Exception cause.
     * @param enableSuppression  Whether suppression should be enabled.
     * @param writableStackTrace Whether stack trace is writeable
     */
    public ArnCommandException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
