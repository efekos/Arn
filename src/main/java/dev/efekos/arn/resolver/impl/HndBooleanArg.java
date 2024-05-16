package dev.efekos.arn.resolver.impl;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.efekos.arn.annotation.CommandArgument;
import dev.efekos.arn.handler.CommandHandlerMethod;
import dev.efekos.arn.resolver.CommandHandlerMethodArgumentResolver;
import net.minecraft.commands.CommandListenerWrapper;

import java.lang.reflect.Parameter;

public class HndBooleanArg implements CommandHandlerMethodArgumentResolver {

    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(CommandArgument.class) && (parameter.getType().equals(boolean.class)||parameter.getType().equals(Boolean.class));
    }

    @Override
    public Object resolve(Parameter parameter, CommandHandlerMethod method, CommandContext<CommandListenerWrapper> context) {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        return BoolArgumentType.getBool(context, s.isEmpty() ?parameter.getName():s);
    }

}
