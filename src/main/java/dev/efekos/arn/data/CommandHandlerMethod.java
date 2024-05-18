package dev.efekos.arn.data;

import dev.efekos.arn.resolver.CommandArgumentResolver;
import dev.efekos.arn.resolver.CommandHandlerMethodArgumentResolver;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Objects;

public class CommandHandlerMethod {
    private String command;
    private Method method;
    private CommandAnnotationData annotationData;
    private List<Parameter> parameters;
    private List<CommandArgumentResolver> argumentResolvers;
    private List<CommandHandlerMethodArgumentResolver> handlerMethodResolvers;
    private String signature;
    private boolean blocksConsole;
    private boolean blocksCommandBlock;
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
        CommandHandlerMethod that = (CommandHandlerMethod) o;
        return blocksConsole == that.blocksConsole && blocksCommandBlock == that.blocksCommandBlock && blocksPlayer == that.blocksPlayer && Objects.equals(command, that.command) && Objects.equals(method, that.method) && Objects.equals(annotationData, that.annotationData) && Objects.equals(parameters, that.parameters) && Objects.equals(argumentResolvers, that.argumentResolvers) && Objects.equals(handlerMethodResolvers, that.handlerMethodResolvers) && Objects.equals(signature, that.signature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(command, method, annotationData, parameters, argumentResolvers, handlerMethodResolvers, signature, blocksConsole, blocksCommandBlock, blocksPlayer);
    }

    public boolean isBlocksConsole() {
        return blocksConsole;
    }

    public void setBlocksConsole(boolean blocksConsole) {
        this.blocksConsole = blocksConsole;
    }

    public boolean isBlocksCommandBlock() {
        return blocksCommandBlock;
    }

    public void setBlocksCommandBlock(boolean blocksCommandBlock) {
        this.blocksCommandBlock = blocksCommandBlock;
    }

    public boolean isBlocksPlayer() {
        return blocksPlayer;
    }

    public void setBlocksPlayer(boolean blocksPlayer) {
        this.blocksPlayer = blocksPlayer;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public CommandAnnotationData getAnnotationData() {
        return annotationData;
    }

    public void setAnnotationData(CommandAnnotationData annotationData) {
        this.annotationData = annotationData;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    public List<CommandArgumentResolver> getArgumentResolvers() {
        return argumentResolvers;
    }

    public void setArgumentResolvers(List<CommandArgumentResolver> argumentResolvers) {
        this.argumentResolvers = argumentResolvers;
    }

    public List<CommandHandlerMethodArgumentResolver> getHandlerMethodResolvers() {
        return handlerMethodResolvers;
    }

    public void setHandlerMethodResolvers(List<CommandHandlerMethodArgumentResolver> handlerMethodResolvers) {
        this.handlerMethodResolvers = handlerMethodResolvers;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}