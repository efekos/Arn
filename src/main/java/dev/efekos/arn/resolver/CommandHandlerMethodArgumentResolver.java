package dev.efekos.arn.resolver;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.efekos.arn.data.CommandHandlerMethod;
import net.minecraft.commands.CommandListenerWrapper;

import java.lang.reflect.Parameter;

public interface CommandHandlerMethodArgumentResolver {

    boolean isApplicable(Parameter parameter);
    boolean requireCommandArgument();
    Object resolve(Parameter parameter, CommandHandlerMethod method, CommandContext<CommandListenerWrapper> context) throws CommandSyntaxException;

}