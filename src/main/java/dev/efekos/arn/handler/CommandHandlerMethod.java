package dev.efekos.arn.handler;

import dev.efekos.arn.annotation.CommandAnnotationData;
import dev.efekos.arn.resolver.CommandArgumentResolver;
import dev.efekos.arn.resolver.CommandHandlerMethodArgumentResolver;
import lombok.Data;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

@Data
public class CommandHandlerMethod {
    private String command;
    private Method method;
    private CommandAnnotationData annotationData;
    private List<Parameter> parameters;
    private List<CommandArgumentResolver> argumentResolvers;
    private List<CommandHandlerMethodArgumentResolver> handlerMethodResolvers;
}