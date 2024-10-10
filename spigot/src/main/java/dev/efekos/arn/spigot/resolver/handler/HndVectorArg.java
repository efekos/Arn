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

package dev.efekos.arn.spigot.resolver.handler;

import com.mojang.brigadier.context.CommandContext;
import dev.efekos.arn.common.annotation.CommandArgument;
import dev.efekos.arn.common.annotation.modifier.Vector;
import dev.efekos.arn.common.resolver.CommandHandlerMethodArgumentResolver;
import dev.efekos.arn.spigot.data.SpigotCommandHandlerMethod;
import dev.efekos.arn.spigot.resolver.SpigotHndResolver;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Parameter;

;

/**
 * An implementation of {@link CommandHandlerMethodArgumentResolver}. Resolves
 * {@link Location} arguments using block
 * position.
 *
 * @author efekos
 * @since 0.1
 */
public final class HndVectorArg implements SpigotHndResolver {

    /**
     * Creates a new resolver.
     */
    public HndVectorArg() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(CommandArgument.class) && parameter.getType().equals(Location.class)
                && parameter.isAnnotationPresent(Vector.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object resolve(Parameter parameter, SpigotCommandHandlerMethod method,
                          CommandContext<CommandSourceStack> context) {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        Vec3 position = Vec3Argument.getVec3(context, s.isEmpty() ? parameter.getName() : s);
        double x = position.x();
        double y = position.y();
        double z = position.z();

        CommandSender sender = context.getSource().getBukkitSender();
        if (sender instanceof Player p)
            return new Location(p.getWorld(), x, y, z);
        else if (sender instanceof BlockCommandSender bc)
            return new Location(bc.getBlock().getWorld(), x, y, z);
        else
            return new Location(Bukkit.getWorld("overworld"), x, y, z);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean requireCommandArgument() {
        return true;
    }
}
