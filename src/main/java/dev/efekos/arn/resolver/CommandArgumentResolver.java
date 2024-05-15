package dev.efekos.arn.resolver;

import dev.efekos.arn.handler.CommandHandlerMethod;

import java.lang.reflect.Parameter;

public interface CommandArgumentResolver {

    boolean isApplicable(Parameter parameter);
    Object resolve(Parameter parameter, CommandHandlerMethod method);

}
