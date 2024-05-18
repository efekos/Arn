package dev.efekos.arn.resolver.impl.handler;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.efekos.arn.handler.CommandHandlerMethod;
import dev.efekos.arn.resolver.CommandHandlerMethodArgumentResolver;
import net.minecraft.commands.CommandListenerWrapper;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.lang.reflect.Parameter;

public class HndConsoleCommandSender implements CommandHandlerMethodArgumentResolver {

    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.getType().equals(ConsoleCommandSender.class);
    }

    @Override
    public boolean requireCommandArgument() {
        return false;
    }

    @Override
    public ConsoleCommandSender resolve(Parameter parameter, CommandHandlerMethod method, CommandContext<CommandListenerWrapper> context) throws CommandSyntaxException {
        CommandSender sender = context.getSource().getBukkitSender();
        if(sender instanceof ConsoleCommandSender) return ((ConsoleCommandSender) sender);
        return null;
    }
}
