package dev.efekos.arn.resolver.impl;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.efekos.arn.annotation.CommandArgument;
import dev.efekos.arn.handler.CommandHandlerMethod;
import dev.efekos.arn.resolver.CommandHandlerMethodArgumentResolver;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.GameModeArgument;
import net.minecraft.world.level.EnumGamemode;
import org.bukkit.GameMode;

import java.lang.reflect.Parameter;
import java.util.Locale;

public class HndGameModeArg implements CommandHandlerMethodArgumentResolver {

    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(CommandArgument.class) && parameter.getType().equals(GameMode.class);
    }

    @Override
    public GameMode resolve(Parameter parameter, CommandHandlerMethod method, CommandContext<CommandListenerWrapper> context) throws CommandSyntaxException {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        EnumGamemode gamemode = GameModeArgument.a(context, s.isEmpty() ? parameter.getName() : s);
        return GameMode.valueOf(gamemode.c().toUpperCase(Locale.ENGLISH));
    }
}
