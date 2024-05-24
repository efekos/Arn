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

import dev.efekos.arn.annotation.Command;
import dev.efekos.arn.data.CommandAnnotationLiteral;
import dev.efekos.arn.data.CommandHandlerMethod;
import dev.efekos.arn.exception.ArnArgumentException;
import dev.efekos.arn.exception.ArnCommandException;
import dev.efekos.arn.exception.ArnContainerException;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

public class ArnExceptionTypes {

    // CustomArgument
    public static final DynamicArnExceptionType<Class<?>> CA_NOT_ENUM = new DynamicArnExceptionType<>(o -> new ArnArgumentException(o.getName() + "is not an enum but is annotated with CustomArgument."));
    public static final DynamicArnExceptionType<Class<?>> CA_VALUE_NOT_KEY = new DynamicArnExceptionType<>(o -> new ArnArgumentException("CustomArgument value of " + o.getName() + " is not a valid namespaced key."));
    public static final DynamicArnExceptionType<Class<? extends Enum<?>>> CA_NO_CONSTANTS = new DynamicArnExceptionType<>(o -> new ArnArgumentException(o.getName() + " must have at least one constant tobe a CustomArgument."));
    public static final DynamicArnExceptionType<Class<? extends Enum<?>>> CA_LOWERCASE = new DynamicArnExceptionType<>(o -> new ArnArgumentException(o.getName() + " can't have constants with lower-case letters when annotated with CustomArgument."));

    // Container
    public static final Dynamic2ArnExceptionType<Class<?>,Exception> CONTAINER_INSTANTIATE = new Dynamic2ArnExceptionType<>((o, o2) -> new ArnContainerException("There was an error while trying to instantiate "+ o + ".", o2));

    // Handler Method
    public static final Dynamic2ArnExceptionType<Method, Command> HM_NOT_INT = new Dynamic2ArnExceptionType<>((o, o2) -> new ArnCommandException("Handler method '" + o.getName() + "' for command '" + o2.value() + "' does not return 'int'"));
    public static final Dynamic3ArnExceptionType<Method, Command, List<Class<?>>> HM_THROWS = new Dynamic3ArnExceptionType<>((o, o2, o3) -> new ArnCommandException("Handler methods are only allowed to throw com.mojang.brigaider.exceptions.CommandSyntaxException, '" + o.getName() + "' for command '" + o2.value() + "' throws " + o3.get(0).getName() + "."));
    public static final Dynamic2ArnExceptionType<Method, Command> HM_MULTIPLE_SENDERS = new Dynamic2ArnExceptionType<>((o, o2) -> new ArnCommandException("Handler method '" + o.getName() + "' for command '" + o2.value() + "' must contain maximum one parameter that is a CommandSender."));
    public static final Dynamic3ArnExceptionType<Method, Command, Parameter> HM_NOT_APPLICABLE = new Dynamic3ArnExceptionType<>((o, o2, o3) -> new ArnCommandException("Handler method '" + o.getName() + "' for command '" + o2.value() + "' has a parameter '" + o3.getName() + "' that isn't applicable for anything."));
    public static final DynamicArnExceptionType<CommandHandlerMethod> HM_DUPLICATE = new DynamicArnExceptionType<>(o -> new ArnCommandException("Duplicate command '" + o.getSignature() + "'"));
    public static final DynamicArnExceptionType<String> HM_NO_RESOLVER_ACCESS = new DynamicArnExceptionType<>(o -> new ArnCommandException("Checked handlerMethodArgumentResolver isn't present on command '" + o + "'. This might be an issue related to Arn, please create an issue on GitHub: https://github.com/efekos/Arn/issues"));

    // Literal
    public static final DynamicArnExceptionType<Command> LITERAL_NEG_OFFSET = new DynamicArnExceptionType<>(o -> new ArnCommandException("Command '" + o.value() + "' has a literal with a negative offset value."));
    public static final Dynamic2ArnExceptionType<CommandAnnotationLiteral, Command> LITERAL_ILLEGAL = new Dynamic2ArnExceptionType<>((o, o2) -> new ArnCommandException("Literal '" + o.getLiteral() + "' of command '" + o2.value() + " has an illegal character."));

    // Command
    public static final DynamicArnExceptionType<Throwable> COMMAND_ERROR = new DynamicArnExceptionType<>(o -> new ArnCommandException("Caused by " + o.getClass().getSimpleName() + ": " + o.getMessage(), o));
    public static final SimpleArnExceptionType COMMAND_NO_ACCESS = new SimpleArnExceptionType(() -> new ArnCommandException("IllegalAccessException. This might be an error related to Arn, please create an issue on GitHub: https://github.com/efekos/Arn/issues"));
    public static final Dynamic2ArnExceptionType<CommandHandlerMethod,Throwable> COMMAND_REGISTER_ERROR = new Dynamic2ArnExceptionType<>((o,o2) -> new ArnCommandException("Something went wrong with registering command '" + o.getCommand() + "'",o2));

}