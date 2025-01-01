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

package dev.efekos.arn.common.base;

import dev.efekos.arn.common.CommandAnnotationData;
import dev.efekos.arn.common.annotation.Command;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
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

    private final List<Class<?>> blockedSenders = new ArrayList<>();
    private String command;
    private Method method;
    private CommandAnnotationData annotationData;
    private List<Parameter> parameters;
    private List<Cmd> argumentResolvers;
    private List<Hnd> handlerMethodResolvers;
    private String signature;
    private boolean blocksConsole;
    private boolean blocksCommandBlock;
    private boolean blocksPlayer;
    private Class<?> includedSender;

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
                ", blockedSenders=" + blockedSenders +
                ", includedSender=" + includedSender +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseCommandHandlerMethod<Cmd, Hnd> that = (BaseCommandHandlerMethod<Cmd, Hnd>) o;
        return blocksConsole == that.blocksConsole && blocksCommandBlock == that.blocksCommandBlock && blocksPlayer == that.blocksPlayer && Objects.equals(command, that.command) && Objects.equals(method, that.method) && Objects.equals(annotationData, that.annotationData) && Objects.equals(parameters, that.parameters) && Objects.equals(argumentResolvers, that.argumentResolvers) && Objects.equals(handlerMethodResolvers, that.handlerMethodResolvers) && Objects.equals(signature, that.signature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(command, method, annotationData, parameters, argumentResolvers, handlerMethodResolvers, signature, blocksConsole, blocksCommandBlock, blocksPlayer);
    }

    /**
     * Returns whether is this command blocked to the console or not.
     * @return Whether is this command blocked to the console.
     */
    public boolean isBlocksConsole() {
        return blocksConsole;
    }

    /**
     * Changes should this command block the console or no.
     * @param blocksConsole New value.
     */
    public void setBlocksConsole(boolean blocksConsole) {
        this.blocksConsole = blocksConsole;
    }

    /**
     * Returns whether is this command blocked to command blocks or not.
     * @return Whether is this command blocked to command blocks.
     */
    public boolean isBlocksCommandBlock() {
        return blocksCommandBlock;
    }

    /**
     * Changes should this command be available on commands blocks or no.
     * @param blocksCommandBlock New value.
     */
    public void setBlocksCommandBlock(boolean blocksCommandBlock) {
        this.blocksCommandBlock = blocksCommandBlock;
    }

    /**
     * Returns whether is this command blocked to players or not.
     * @return Whether is this command blocked to players.
     */
    public boolean isBlocksPlayer() {
        return blocksPlayer;
    }

    /**
     * Changes should this command be unavailable to players.
     * @param blocksPlayer New value.
     */
    public void setBlocksPlayer(boolean blocksPlayer) {
        this.blocksPlayer = blocksPlayer;
    }

    /**
     * Returns unparsed literals of this command, which is exactly same with the value of the {@link Command} annotation
     * that was applied to the method of this command.
     * @return {@link Command#value()} of {@link #method}.
     */
    public String getCommand() {
        return command;
    }

    /**
     * Changes unparsed literal string of this command. Note that literals are not parsed again when this method is
     * called, and new literals of this command should be changed using {@link #getAnnotationData()} >
     * {@link CommandAnnotationData#setLiterals(List)}.
     * @param command New value.
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * Returns the base method of this command.
     * @return Method from Java Reflection API associated with this CommandHandlerMethod.
     */
    public Method getMethod() {
        return method;
    }

    /**
     * Changes the base method of this command.
     * @param method New value.
     */
    public void setMethod(Method method) {
        this.method = method;
    }

    /**
     * Returns the data gathered from command-related annotations such as {@link Command},
     * {@link dev.efekos.arn.common.annotation.Description} or {@link dev.efekos.arn.common.annotation.Permission}.
     * @return Changeable version of {@link Command} on {@link #method}.
     */
    public CommandAnnotationData getAnnotationData() {
        return annotationData;
    }

    /**
     * Changes data about the annotations applied to this command.
     * @param annotationData New value.
     */
    public void setAnnotationData(CommandAnnotationData annotationData) {
        this.annotationData = annotationData;
    }

    /**
     * Returns a list of parameters this command's base method has.
     * @return Parameter list.
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

    /**
     * Check if the given sender type is blocked.
     * @param sender Class of a sender type.
     * @return Whether sender is blocked by this command or not.
     */
    public boolean doesBlockSenderType(Class<?> sender) {
        return blockedSenders.contains(sender);
    }

    /**
     * Checks if the given sender is blocked.
     * @param sender Command sender.
     * @return Whether the given sender is blocked by this commando r not.
     */
    public boolean doesBlockSender(Object sender) {
        return sender != null && doesBlockSenderType(sender.getClass());
    }

    /**
     * Add given sender type to blocked command sender types which will not be able tu use this command.
     * @param sender Sender type.
     */
    public void addSenderBlock(Class<?> sender) {
        blockedSenders.add(sender);
    }

    /**
     * Returns the included sender of this command. When an included sender is present on a command, only that specific
     * sender type should be able to use the command.
     * @return Included sender type of this command
     */
    @Nullable
    public Class<?> getIncludedSender() {
        return includedSender;
    }

    /**
     * Changes the included sender type of this command. When an included command sender type is present on a command,
     * only that specific command sender type should be able to use this command.
     * @param includedSender New value.
     */
    public void setIncludedSender(Class<?> includedSender) {
        this.includedSender = includedSender;
    }

}