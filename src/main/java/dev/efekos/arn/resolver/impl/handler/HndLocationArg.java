/*
 * MIT License
 *
 * Copyright (c) 2024 efekos
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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

/**
 * An implementation of {@link CommandHandlerMethodArgumentResolver}. Resolves {@link Location} arguments using block
 * position.
 *
 * @author efekos
 * @since 0.1
 */
public final class HndLocationArg implements CommandHandlerMethodArgumentResolver {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(CommandArgument.class) && parameter.getType().equals(Location.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object resolve(Parameter parameter, CommandHandlerMethod method, CommandContext<CommandListenerWrapper> context) {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        BlockPosition position = ArgumentPosition.b(context, s.isEmpty() ? parameter.getName() : s);
        int x = position.u();
        int y = position.v();
        int z = position.w();
        World world;

        EntityPlayer player = context.getSource().i();
        if (player == null) {
            world = Bukkit.getWorld("overworld");
            return new Location(world, x, y, z);
        }
        Server server = Bukkit.getServer();
        CraftPlayer p = new CraftPlayer(((CraftServer) server), player);
        world = p.getWorld();
        return new Location(world, x, y, z);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean requireCommandArgument() {
        return true;
    }
}
