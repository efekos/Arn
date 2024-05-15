package dev.efekos.arn.handler;

import lombok.Data;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Data
public class CommandHandlerMethod {
    private String command;
    private Method method;
    private Map<String, Annotation[]> annotations = new HashMap<>();
}