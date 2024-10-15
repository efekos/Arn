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

package dev.efekos.arn.common.base;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Represents an exception handler method that was annotated with
 * {@link dev.efekos.arn.common.annotation.ExceptionHandler}. The job of this method is to provide a proper response to
 * the command sender when an exception occurs whilst executing the command.
 * @param <Context> Command context that is specific to the platform.
 */
public abstract class BaseExceptionHandlerMethod<Context> {

    protected Method method;
    protected Class<? extends Exception> exceptionClass;

    /**
     * Creates a new method.
     * @param method Base method that this instance represents.
     * @param exceptionClass Type of the exception this exception handler will catch.
     */
    public BaseExceptionHandlerMethod(Method method, Class<? extends Exception> exceptionClass) {
        this.method = method;
        this.exceptionClass = exceptionClass;
    }

    /**
     * Returns the base method of this exception handler method
     * @return Base method that this instance represents.
     */
    public Method getMethod() {
        return method;
    }

    /**
     * Changes the base method that this ExceptionHandlerMethod represents.
     * @param method New value.
     */
    public void setMethod(Method method) {
        this.method = method;
    }

    /**
     * Returns the class this ExceptionHandlerMethod is supposed to catch.
     * @return Exception type this method is for.
     */
    public Class<? extends Exception> getExceptionClass() {
        return exceptionClass;
    }

    /**
     * Changes the exception type this ExceptionHandlerMethod is supposed to catch.
     * @param exceptionClass New value.
     */
    public void setExceptionClass(Class<? extends Exception> exceptionClass) {
        this.exceptionClass = exceptionClass;
    }

    /**
     * Returns a list of objects to pass in as parameter to the base method while executing it.
     * @param ex Exception that was thrown.
     * @param commandContext Context about the command that subtypes of {@link BaseExceptionHandlerMethod} must use to
     *                       provide as many arguments as possible to the base method.
     * @return A list of objects to pass in to the base method to invoke it.
     */
    public abstract List<Object> fillParams(Throwable ex, Context commandContext);

}