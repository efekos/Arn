package dev.efekos.arn.resolver;

import com.mojang.brigadier.context.CommandContext;
import dev.efekos.arn.annotation.CommandArgument;
import dev.efekos.arn.handler.CommandHandlerMethod;
import net.minecraft.commands.CommandListenerWrapper;

import java.lang.reflect.Parameter;

public class CommandHandlerMethodStringArgumentResolver implements CommandHandlerMethodArgumentResolver{

    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(CommandArgument.class) && parameter.getType().equals(String.class);
    }

    @Override
    public String resolve(Parameter parameter, CommandHandlerMethod method, CommandContext<CommandListenerWrapper> context) {
        String s = context.getArgument(parameter.getName(), String.class);
        if(s==null) return "";
        return s;
    }
}
