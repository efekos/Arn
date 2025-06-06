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

package dev.efekos.arn.spigot;

import dev.efekos.arn.common.CommandAnnotationLiteral;
import dev.efekos.arn.common.annotation.Command;
import dev.efekos.arn.common.annotation.CustomArgument;
import dev.efekos.arn.common.base.BaseCmdResolver;
import dev.efekos.arn.common.base.BaseHndResolver;
import dev.efekos.arn.common.exception.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

;

/**
 * Utility class containing every exception type thrown by {@link SpigotArn}.
 *
 * @author efekos
 * @since 0.3
 */
public final class SpigotArnExceptions {

    /**
     * Thrown when a something that isn't an enum is annotated with
     * {@link CustomArgument}.
     */
    public static final DynamicArnExceptionType<ArnArgumentException, Class<?>> CA_NOT_ENUM = new DynamicArnExceptionType<>(
            o -> new ArnArgumentException(
                    o.getName() + "is not an enum but is annotated with CustomArgument."));

    // CustomArgument
    /**
     * Thrown when a value given to a {@link CustomArgument} annotation isn't a key.
     */
    public static final DynamicArnExceptionType<ArnArgumentException, Class<?>> CA_VALUE_NOT_KEY = new DynamicArnExceptionType<>(
            o -> new ArnArgumentException(
                    "CustomArgument value of " + o.getName() + " is not a valid namespaced key."));
    /**
     * Thrown when an enum annotated with {@link CustomArgument} doesn't have any
     * constants.
     */
    public static final DynamicArnExceptionType<ArnArgumentException, Class<? extends Enum<?>>> CA_NO_CONSTANTS = new DynamicArnExceptionType<>(
            o -> new ArnArgumentException(
                    o.getName() + " must have at least one constant tobe a CustomArgument."));
    /**
     * Thrown when an enum annotated with {@link CustomArgument} has a constant that
     * isn't
     * properly cased.
     */
    public static final DynamicArnExceptionType<ArnArgumentException, Class<? extends Enum<?>>> CA_LOWERCASE = new DynamicArnExceptionType<>(
            o -> new ArnArgumentException(
                    o.getName() + " can't have constants with lower-case letters when annotated with CustomArgument."));
    /**
     * Thrown when instantiating containers fail, probably because a container
     * didn't support empty constructors.
     */
    public static final Dynamic2ArnExceptionType<ArnContainerException, Class<?>, Exception> CONTAINER_INSTANTIATE = new Dynamic2ArnExceptionType<>(
            (o, o2) -> new ArnContainerException(
                    "There was an error while trying to instantiate " + o + ".", o2));

    // Container
    /**
     * Thrown when a method annotated with {@link Command} doesn't return
     * {@code int}.
     */
    public static final Dynamic2ArnExceptionType<ArnCommandException, Method, Command> HM_NOT_INT = new Dynamic2ArnExceptionType<>(
            (o, o2) -> new ArnCommandException(
                    "Handler method '" + o.getName() + "' for command '" + o2.value()
                            + "' does not return 'int'"));

    // Handler Method
    /**
     * Thrown when a method annotated with {@link Command} throws something else
     * than {@link com.mojang.brigadier.exceptions.CommandSyntaxException}.
     */
    public static final Dynamic3ArnExceptionType<ArnCommandException, Method, Command, List<Class<?>>> HM_THROWS = new Dynamic3ArnExceptionType<>(
            (o, o2, o3) -> new ArnCommandException(
                    "Handler methods are only allowed to throw com.mojang.brigaider.exceptions.CommandSyntaxException, '"
                            + o.getName() + "' for command '" + o2.value() + "' throws "
                            + o3.get(0).getName() + "."));
    /**
     * Thrown when a method annotated with {@link Command} has more than one
     * parameter that will be treated as the
     * sender.
     */
    public static final Dynamic2ArnExceptionType<ArnCommandException, Method, Command> HM_MULTIPLE_SENDERS = new Dynamic2ArnExceptionType<>(
            (o, o2) -> new ArnCommandException("Handler method '" + o.getName() + "' for command '"
                    + o2.value()
                    + "' must contain maximum one parameter that is a CommandSender."));
    /**
     * Thrown when a method annotated with {@link Command} has a parameter that
     * can't be linked with any
     * {@link BaseHndResolver} or
     * {@link BaseCmdResolver}.
     */
    public static final Dynamic3ArnExceptionType<ArnCommandException, Method, Command, Parameter> HM_NOT_APPLICABLE = new Dynamic3ArnExceptionType<>(
            (o, o2, o3) -> new ArnCommandException(
                    "Handler method '" + o.getName() + "' for command '" + o2.value()
                            + "' has a parameter '" + o3.getName()
                            + "' that isn't applicable for anything."));
    /**
     * Thrown when more than two {@link SpigotCommandHandlerMethod}s have the same
     * signature.
     */
    public static final DynamicArnExceptionType<ArnCommandException, SpigotCommandHandlerMethod> HM_DUPLICATE = new DynamicArnExceptionType<>(
            o -> new ArnCommandException("Duplicate command '" + o.getSignature() + "'"));
    /**
     * Thrown when a guaranteed nonnull {@link BaseHndResolver}
     * somehow
     * becomes null.
     */
    public static final DynamicArnExceptionType<ArnCommandException, String> HM_NO_RESOLVER_ACCESS = new DynamicArnExceptionType<>(
            o -> new ArnCommandException("Checked handlerMethodArgumentResolver isn't present on command '"
                    + o
                    + "'. This might be an issue related to Arn, please create an issue on GitHub: https://github.com/efekos/Arn/issues"));
    /**
     * Thrown when a {@link CommandAnnotationLiteral} of a
     * {@link SpigotCommandHandlerMethod} has a negative offset.
     */
    public static final DynamicArnExceptionType<ArnCommandException, Command> LITERAL_NEG_OFFSET = new DynamicArnExceptionType<>(
            o -> new ArnCommandException(
                    "Command '" + o.value() + "' has a literal with a negative offset value."));

    // Literal
    /**
     * Thrown when a {@link CommandAnnotationLiteral} has invalid characters, which
     * is the outside of a-z range.
     */
    public static final Dynamic2ArnExceptionType<ArnCommandException, CommandAnnotationLiteral, Command> LITERAL_ILLEGAL = new Dynamic2ArnExceptionType<>(
            (o, o2) -> new ArnCommandException(
                    "Literal '" + o.getLiteral() + "' of command '" + o2.value()
                            + " has an illegal character."));
    /**
     * Thrown when a {@link SpigotCommandHandlerMethod} throws an unexpected error.
     */
    public static final DynamicArnExceptionType<ArnCommandException, Throwable> COMMAND_ERROR = new DynamicArnExceptionType<>(
            o -> new ArnCommandException(
                    "Caused by " + o.getClass().getSimpleName() + ": " + o.getMessage(), o));

    // Command
    /**
     * Thrown when the method of a {@link SpigotCommandHandlerMethod} can't be
     * invoked, which is really strange because it was
     * made accessible right before invoking it.
     */
    public static final SimpleArnExceptionType<ArnCommandException> COMMAND_NO_ACCESS = new SimpleArnExceptionType<>(
            () -> new ArnCommandException(
                    "IllegalAccessException. This might be an error related to Arn, please create an issue on GitHub: https://github.com/efekos/Arn/issues"));
    /**
     * Thrown when registering a command fails.
     */
    public static final Dynamic2ArnExceptionType<ArnCommandException, SpigotCommandHandlerMethod, Throwable> COMMAND_REGISTER_ERROR = new Dynamic2ArnExceptionType<>(
            (o, o2) -> new ArnCommandException("Something went wrong with registering command '"
                    + o.getCommand()
                    + "'. This might be an error related to Arn, please create an issue on GitHub: https://github.com/efekos/Arn/issues",
                    o2));


    private SpigotArnExceptions() {
    }
}