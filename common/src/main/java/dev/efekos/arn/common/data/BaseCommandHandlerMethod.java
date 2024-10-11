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

package dev.efekos.arn.common.data;

import dev.efekos.arn.common.annotation.Command;
import dev.efekos.arn.common.annotation.block.BlockCommandBlock;
import dev.efekos.arn.common.annotation.block.BlockConsole;
import dev.efekos.arn.common.annotation.block.BlockPlayer;
import dev.efekos.arn.common.exception.ArnCommandException;
import dev.efekos.arn.common.resolver.BaseCmdResolver;
import dev.efekos.arn.common.resolver.BaseHndResolver;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Objects;

/**
 * Represents a {@link Method} that is annotated with {@link Command}. Used to store data about
 * a command handler methods and register commands using those data.
 *
 * @author efekos
 * @since 0.1
 */
public abstract class BaseCommandHandlerMethod<Cmd extends BaseCmdResolver<?>, Hnd extends BaseHndResolver<?, ?>> {

    /**
     * Value of the {@link Command#value()} on {@link #method}.
     */
    private String command;
    /**
     * Main method from Java Reflection API.
     */
    private Method method;
    /**
     * An annotation data created from the {@link Command} annotation on {@link #method}.
     */
    private CommandAnnotationData annotationData;
    /**
     * {@link Method#getParameters()} of {@link #method} converted into an {@link java.util.ArrayList}.
     */
    private List<Parameter> parameters;
    /**
     * A list of {@link Cmd}s found and used by Arn.
     */
    private List<Cmd> argumentResolvers;
    /**
     * A list of {@link Hnd}s found and used by Arn.
     */
    private List<Hnd> handlerMethodResolvers;
    /**
     * A signature string that represents what this command is. Unlike a method signature, this signature starts with
     * {@link Command#value()} of {@link #method} instead of the actual method game gathered through {@link Method#getName()}.
     * This signature is generated and used by Arn to detect duplicate commands, and throw a {@link ArnCommandException}
     * when found.
     */
    private String signature;
    /**
     * Determines is this command blocked to console. When a command is blocked to console, the console will receive an
     * error message while trying to execute the command. Arn makes this value {@code true} if
     * {@link #method} has a {@link BlockConsole} annotation.
     */
    private boolean blocksConsole;
    /**
     * Determines is this command blocked to command blocks. When a command is blocked to command blocks, a command block
     * will receive an error message while trying to execute the command. Arn makes this value
     * {@code true} if {@link #method} has a {@link BlockCommandBlock} annotation.
     */
    private boolean blocksCommandBlock;
    /**
     * Determines is this command blocked to players. When a command is blocked to players, a player will receive an
     * error message while trying to execute the command. Arn makes this value {@code true} if
     * {@link #method} has a {@link BlockPlayer} annotation.
     */
    private boolean blocksPlayer;

    @Override
    public String toString() {
        return "CommandHandlerMethod{" +
                "command='" + command + '\'' +
                ", method=" + method +
                ", annotationData=" + annotationData +
                ", parameters=" + parameters +
                ", argumentResolvers=" + argumentResolvers +
                ", handlerMethodResolvers=" + handlerMethodResolvers +
                ", signature='" + signature + '\'' +
                ", blocksConsole=" + blocksConsole +
                ", blocksCommandBlock=" + blocksCommandBlock +
                ", blocksPlayer=" + blocksPlayer +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseCommandHandlerMethod that = (BaseCommandHandlerMethod) o;
        return blocksConsole == that.blocksConsole && blocksCommandBlock == that.blocksCommandBlock && blocksPlayer == that.blocksPlayer && Objects.equals(command, that.command) && Objects.equals(method, that.method) && Objects.equals(annotationData, that.annotationData) && Objects.equals(parameters, that.parameters) && Objects.equals(argumentResolvers, that.argumentResolvers) && Objects.equals(handlerMethodResolvers, that.handlerMethodResolvers) && Objects.equals(signature, that.signature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(command, method, annotationData, parameters, argumentResolvers, handlerMethodResolvers, signature, blocksConsole, blocksCommandBlock, blocksPlayer);
    }

    /**
     * Getter for {@link #blocksConsole}.
     *
     * @return Whether is this command blocked to the console.
     */
    public boolean isBlocksConsole() {
        return blocksConsole;
    }

    /**
     * Setter for {@link #blocksConsole}.
     *
     * @param blocksConsole New value.
     */
    public void setBlocksConsole(boolean blocksConsole) {
        this.blocksConsole = blocksConsole;
    }

    /**
     * Getter for {@link #blocksCommandBlock}.
     *
     * @return Whether is this command blocked to command blocks.
     */
    public boolean isBlocksCommandBlock() {
        return blocksCommandBlock;
    }

    /**
     * Setter for {@link #blocksCommandBlock}.
     *
     * @param blocksCommandBlock New value.
     */
    public void setBlocksCommandBlock(boolean blocksCommandBlock) {
        this.blocksCommandBlock = blocksCommandBlock;
    }

    /**
     * Getter for {@link #blocksPlayer}.
     *
     * @return Whether is this command blocked to players.
     */
    public boolean isBlocksPlayer() {
        return blocksPlayer;
    }

    /**
     * Setter for {@link #blocksPlayer}.
     *
     * @param blocksPlayer New value.
     */
    public void setBlocksPlayer(boolean blocksPlayer) {
        this.blocksPlayer = blocksPlayer;
    }

    /**
     * Getter for {@link #command}.
     *
     * @return {@link Command#value()} of {@link #method}.
     */
    public String getCommand() {
        return command;
    }

    /**
     * Setter for {@link #command}.
     *
     * @param command New value.
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * Getter for {@link #method}.
     *
     * @return Method from Java Reflection API associated with this CommandHandlerMethod.
     */
    public Method getMethod() {
        return method;
    }

    /**
     * Setter for {@link #method}.
     *
     * @param method New value.
     */
    public void setMethod(Method method) {
        this.method = method;
    }

    /**
     * Getter for {@link #annotationData}.
     *
     * @return Changeable version of {@link Command} on {@link #method}.
     */
    public CommandAnnotationData getAnnotationData() {
        return annotationData;
    }

    /**
     * Setter for {@link #annotationData}.
     *
     * @param annotationData New value.
     */
    public void setAnnotationData(CommandAnnotationData annotationData) {
        this.annotationData = annotationData;
    }

    /**
     * Getter for {@link #parameters}.
     *
     * @return Parameter list of {@link #method}.
     */
    public List<Parameter> getParameters() {
        return parameters;
    }

    /**
     * Setter for {@link #parameters}.
     *
     * @param parameters New value.
     */
    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    /**
     * Getter for {@link #argumentResolvers}.
     *
     * @return List of {@link Cmd}s found by Arn.
     */
    public List<Cmd> getArgumentResolvers() {
        return argumentResolvers;
    }

    /**
     * Setter for {@link #argumentResolvers}.
     *
     * @param argumentResolvers New value.
     */
    public void setArgumentResolvers(List<Cmd> argumentResolvers) {
        this.argumentResolvers = argumentResolvers;
    }


    /**
     * Getter for {@link #argumentResolvers}.
     *
     * @return List of {@link BaseHndResolver}s found by Arn.
     */
    public List<Hnd> getHandlerMethodResolvers() {
        return handlerMethodResolvers;
    }

    /**
     * Setter for {@link #handlerMethodResolvers}.
     *
     * @param handlerMethodResolvers New value.
     */
    public void setHandlerMethodResolvers(List<Hnd> handlerMethodResolvers) {
        this.handlerMethodResolvers = handlerMethodResolvers;
    }

    /**
     * Getter for {@link #signature}.
     *
     * @return Signature unique this command.
     */
    public String getSignature() {
        return signature;
    }

    /**
     * Setter for {@link #signature}.
     *
     * @param signature New value.
     */
    public void setSignature(String signature) {
        this.signature = signature;
    }
}