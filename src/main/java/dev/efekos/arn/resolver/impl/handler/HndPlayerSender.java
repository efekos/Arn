package dev.efekos.arn.resolver.impl.handler;

import com.mojang.brigadier.context.CommandContext;
import dev.efekos.arn.annotation.CommandArgument;
import dev.efekos.arn.data.CommandHandlerMethod;
import dev.efekos.arn.resolver.CommandHandlerMethodArgumentResolver;
import net.minecraft.commands.CommandListenerWrapper;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Parameter;

public class HndPlayerSender implements CommandHandlerMethodArgumentResolver {

    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.getType().equals(Player.class) && !parameter.isAnnotationPresent(CommandArgument.class);
    }

    @Override
    public Player resolve(Parameter parameter, CommandHandlerMethod method, CommandContext<CommandListenerWrapper> context) {
        CommandSender sender = context.getSource().getBukkitSender();
        if(sender instanceof Player) return ((Player) sender);
        return null;
    }

    @Override
    public boolean requireCommandArgument() {
        return false;
    }
}
