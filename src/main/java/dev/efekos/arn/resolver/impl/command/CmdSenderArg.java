package dev.efekos.arn.resolver.impl.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import dev.efekos.arn.annotation.CommandArgument;
import dev.efekos.arn.annotation.FromSender;
import dev.efekos.arn.resolver.CommandArgumentResolver;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Parameter;

public class CmdSenderArg implements CommandArgumentResolver {
    @Override
    public boolean isApplicable(Parameter parameter) {
        return (parameter.getType().equals(Player.class)||parameter.getType().equals(CommandSender.class)||parameter.getType().equals(ConsoleCommandSender.class)||parameter.getType().equals(BlockCommandSender.class)) && !parameter.isAnnotationPresent(CommandArgument.class) && !parameter.isAnnotationPresent(FromSender.class);
    }

    @Override
    public ArgumentBuilder<?,?> apply(Parameter parameter) {
        return null;
    }
}
