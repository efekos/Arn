package dev.efekos.arn.resolver.impl.handler;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.efekos.arn.annotation.CommandArgument;
import dev.efekos.arn.data.CommandHandlerMethod;
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
import java.util.Collection;

/**
 * An implementation of {@link CommandHandlerMethodArgumentResolver}. Resolves {@link Player}[] arguments.
 * @since 0.1
 * @author efekos
 */
public final class HndMultiplePlayerArg implements CommandHandlerMethodArgumentResolver {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(CommandArgument.class) && parameter.getType().equals(Player[].class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Player[] resolve(Parameter parameter, CommandHandlerMethod method, CommandContext<CommandListenerWrapper> context) throws CommandSyntaxException {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        Collection<EntityPlayer> player = ArgumentEntity.d(context, s.isEmpty() ? parameter.getName() : s);
        if(player==null) return null;
        Server server = Bukkit.getServer();
        return player.stream().map(entityPlayer -> new CraftPlayer(((CraftServer) server),entityPlayer)).toArray(CraftPlayer[]::new);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean requireCommandArgument() {
        return true;
    }
}
