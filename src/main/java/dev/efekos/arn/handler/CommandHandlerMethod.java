package dev.efekos.arn.handler;

import dev.efekos.arn.annotation.CommandAnnotationData;
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
}