package dev.efekos.arn.resolver.impl.handler;

import com.mojang.brigadier.context.CommandContext;
import dev.efekos.arn.annotation.CommandArgument;
import dev.efekos.arn.data.CommandHandlerMethod;
import dev.efekos.arn.resolver.CommandHandlerMethodArgumentResolver;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.coordinates.ArgumentPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;

import java.lang.reflect.Parameter;

public class HndLocationArg implements CommandHandlerMethodArgumentResolver {

    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(CommandArgument.class) && parameter.getType().equals(Location.class);
    }

    @Override
    public Object resolve(Parameter parameter, CommandHandlerMethod method, CommandContext<CommandListenerWrapper> context) {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        BlockPosition position = ArgumentPosition.b(context, s.isEmpty() ? parameter.getName() : s);
        int x = position.u();
        int y = position.v();
        int z = position.w();
        World world;

        EntityPlayer player = context.getSource().i();
        if(player==null) {
            world = Bukkit.getWorld("overworld");
            return new Location(world, x, y, z);
        }
        Server server = Bukkit.getServer();
        CraftPlayer p = new CraftPlayer(((CraftServer) server), player);
        world = p.getWorld();
        return new Location(world, x, y, z);
    }

    @Override
    public boolean requireCommandArgument() {
        return true;
    }
}
