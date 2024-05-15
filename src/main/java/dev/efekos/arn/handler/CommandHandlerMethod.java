package dev.efekos.arn.handler;

import lombok.Data;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

@Data
public class CommandHandlerMethod {
    private String command;
    private Method method;
    private List<Parameter> parameters;
}