package dev.efekos.arn.resolver.impl;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.efekos.arn.annotation.CommandArgument;
import dev.efekos.arn.handler.CommandHandlerMethod;
import dev.efekos.arn.resolver.CommandHandlerMethodArgumentResolver;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentEntity;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;

public class HndMultipleEntityArg implements CommandHandlerMethodArgumentResolver {

    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(CommandArgument.class) && parameter.getType().equals(Entity[].class);
    }

    @Override
    public Entity[] resolve(Parameter parameter, CommandHandlerMethod method, CommandContext<CommandListenerWrapper> context) throws CommandSyntaxException {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        Collection<? extends net.minecraft.world.entity.Entity> entities = ArgumentEntity.c(context, s.isEmpty() ? parameter.getName() : s);

        return entities.stream().map(entity -> CraftEntity.getEntity(((CraftServer) Bukkit.getServer()),entity)).toArray(CraftEntity[]::new);
    }

}

