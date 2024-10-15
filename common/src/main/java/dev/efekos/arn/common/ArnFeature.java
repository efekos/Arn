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

package dev.efekos.arn.common;

/**
 * An enumerator used to determine what features of Arn supported in the current environment.
 */
public enum ArnFeature {

    /**
     * All features. {@link dev.efekos.arn.common.base.ArnInstance#doesSupport(ArnFeature)} will always return {@code true}
     * if an {@link dev.efekos.arn.common.base.ArnInstance} contains this feature in its feature list.
     */
    ALL,

    /**
     * Commands you can register using {@link dev.efekos.arn.common.annotation.Command} on a method inside a class
     * annotated with {@link dev.efekos.arn.common.annotation.Container}.
     */
    COMMANDS,

    /**
     * Being able to turn any enum class into a custom argument type using {@link dev.efekos.arn.common.annotation.Container}
     * and {@link dev.efekos.arn.common.annotation.CustomArgument} annotations.
     */
    ENUM_ARGUMENTS,

    /**
     * Being able to create advanced custom argument types using {@link dev.efekos.arn.common.base.BaseCustomArgumentType}
     */
    CUSTOM_ARGUMENTS,

    /**
     * Being able to exclude specific classes from scanning despite if they are annotated with {@link dev.efekos.arn.common.annotation.Container}
     * or not using {@link dev.efekos.arn.common.base.ArnInstance#excludeClass(Class)}.
     */
    EXCLUSION,

    /**
     * Exception handler methods you can register using {@link dev.efekos.arn.common.annotation.ExceptionHandler} method.
     */
    EXCEPTION_HANDLERS

}
