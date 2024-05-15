package dev.efekos.arn.resolver;

import com.mojang.brigadier.context.CommandContext;
import dev.efekos.arn.annotation.CommandArgument;
import dev.efekos.arn.handler.CommandHandlerMethod;
import net.minecraft.commands.CommandListenerWrapper;

import java.lang.reflect.Parameter;

public class CommandHandlerMethodIntArgumentResolver implements CommandHandlerMethodArgumentResolver {

    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(CommandArgument.class) && (parameter.getType().equals(int.class) || parameter.getType().equals(Integer.class));
    }

    @Override
    public Integer resolve(Parameter parameter, CommandHandlerMethod method, CommandContext<CommandListenerWrapper> context) {
        Integer i = context.getArgument(parameter.getName(), Integer.class);
        if(i==null) return -1;
        return i;
    }
}
