package dev.efekos.arn.resolver.impl;

import com.mojang.brigadier.builder.ArgumentBuilder;
import dev.efekos.arn.annotation.CommandArgument;
import dev.efekos.arn.resolver.CommandArgumentResolver;
import net.minecraft.commands.CommandDispatcher;
import net.minecraft.commands.arguments.GameModeArgument;
import org.bukkit.GameMode;

import java.lang.reflect.Parameter;

public class CmdGameModeArg implements CommandArgumentResolver {

    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(CommandArgument.class) && parameter.getType().equals(GameMode.class);
    }

    @Override
    public ArgumentBuilder apply(Parameter parameter) {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        return CommandDispatcher.a(s.isEmpty()?parameter.getName():s, GameModeArgument.a());
    }
}