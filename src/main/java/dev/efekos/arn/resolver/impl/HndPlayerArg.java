package dev.efekos.arn.resolver.impl;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.efekos.arn.annotation.CommandArgument;
import dev.efekos.arn.handler.CommandHandlerMethod;
import dev.efekos.arn.resolver.CommandHandlerMethodArgumentResolver;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Parameter;

public class HndPlayerArg implements CommandHandlerMethodArgumentResolver {
    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(CommandArgument.class) && parameter.getType().equals(Player.class);
    }

    @Override
    public Object resolve(Parameter parameter, CommandHandlerMethod method, CommandContext<CommandListenerWrapper> context) throws CommandSyntaxException {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        EntityPlayer player = ArgumentEntity.e(context, s.isEmpty() ? parameter.getName() : s);
        if(player==null) return null;
        Server server = Bukkit.getServer();
        return new CraftPlayer(((CraftServer) server),player);
    }
}
