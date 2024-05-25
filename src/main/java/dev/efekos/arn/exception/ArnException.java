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

/**
 * Base exception type thrown by Arn.
 *
 * @author efekos
 * @since 0.3
 */
public class ArnException extends Exception {


    /**
     * Creates a new exception.
     */
    public ArnException() {
    }

    /**
     * Creates a new exception.
     *
     * @param message Exception message.
     */
    public ArnException(String message) {
        super(message);
    }

    /**
     * Creates a new exception.
     *
     * @param message Exception message.
     * @param cause   Exception cause.
     */
    public ArnException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new exception.
     *
     * @param cause Exception cause.
     */
    public ArnException(Throwable cause) {
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
    public ArnException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
