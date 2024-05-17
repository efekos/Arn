package dev.efekos.arn.resolver.impl;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.efekos.arn.annotation.CommandArgument;
import dev.efekos.arn.handler.CommandHandlerMethod;
import dev.efekos.arn.resolver.CommandHandlerMethodArgumentResolver;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentDimension;
import net.minecraft.server.level.WorldServer;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;

import java.lang.reflect.Parameter;

public class HndDimensionArg implements CommandHandlerMethodArgumentResolver {

    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(CommandArgument.class) && (parameter.getType().equals(World.class));
    }

    @Override
    public World resolve(Parameter parameter, CommandHandlerMethod method, CommandContext<CommandListenerWrapper> context) throws CommandSyntaxException {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        WorldServer world = ArgumentDimension.a(context, s.isEmpty() ? parameter.getName() : s);
        return world.getWorld();
    }
}