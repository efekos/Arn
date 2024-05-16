package dev.efekos.arn.resolver.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.efekos.arn.annotation.CommandArgument;
import dev.efekos.arn.handler.CommandHandlerMethod;
import dev.efekos.arn.resolver.CommandHandlerMethodArgumentResolver;
import net.minecraft.commands.CommandListenerWrapper;

import java.lang.reflect.Parameter;

public class HndStringArg implements CommandHandlerMethodArgumentResolver {

    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(CommandArgument.class) && parameter.getType().equals(String.class);
    }

    @Override
    public String resolve(Parameter parameter, CommandHandlerMethod method, CommandContext<CommandListenerWrapper> context) {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        return StringArgumentType.getString(context, s.isEmpty() ?parameter.getName():s);
    }
}
