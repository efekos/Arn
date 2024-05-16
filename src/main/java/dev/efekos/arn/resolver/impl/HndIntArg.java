package dev.efekos.arn.resolver.impl;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.efekos.arn.annotation.CommandArgument;
import dev.efekos.arn.handler.CommandHandlerMethod;
import dev.efekos.arn.resolver.CommandHandlerMethodArgumentResolver;
import net.minecraft.commands.CommandListenerWrapper;

import java.lang.reflect.Parameter;

public class HndIntArg implements CommandHandlerMethodArgumentResolver {

    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(CommandArgument.class) && (parameter.getType().equals(int.class) || parameter.getType().equals(Integer.class));
    }

    @Override
    public Integer resolve(Parameter parameter, CommandHandlerMethod method, CommandContext<CommandListenerWrapper> context) {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        return IntegerArgumentType.getInteger(context, s.isEmpty() ?parameter.getName():s);
    }
}
