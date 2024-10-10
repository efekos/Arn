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

package dev.efekos.arn.spigot.resolver.impl.handler;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.efekos.arn.common.annotation.CommandArgument;
import dev.efekos.arn.common.exception.ArnSyntaxException;
import dev.efekos.arn.common.resolver.CommandHandlerMethodArgumentResolver;
import dev.efekos.arn.spigot.data.SpigotCommandHandlerMethod;
import dev.efekos.arn.spigot.resolver.SpigotHndResolver;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_21_R1.CraftServer;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.reflect.Parameter;
import java.util.Collection;

;

/**
 * An implementation of {@link CommandHandlerMethodArgumentResolver}. Resolves
 * {@link Entity}[] arguments.
 *
 * @author efekos
 * @since 0.1
 */
public final class HndMultipleEntityArg implements SpigotHndResolver {

    /**
     * Creates a new resolver.
     */
    public HndMultipleEntityArg() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(CommandArgument.class) && parameter.getType().equals(Entity[].class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Entity[] resolve(Parameter parameter, SpigotCommandHandlerMethod method,
                            CommandContext<CommandSourceStack> context) throws ArnSyntaxException {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        Collection<? extends net.minecraft.world.entity.Entity> entities = null;
        try {
            entities = EntityArgument.getEntities(context, s.isEmpty() ? parameter.getName() : s);
        } catch (CommandSyntaxException e) {
            throw new ArnSyntaxException(e);
        }
        CommandSender sender = context.getSource().getBukkitSender();
        World w;
        if (sender instanceof Player)
            w = ((Player) sender).getWorld();
        else if (sender instanceof BlockCommandSender)
            w = ((BlockCommandSender) sender).getBlock().getWorld();
        else
            w = Bukkit.getWorld("overworld");
        return entities.stream().map(entity -> CraftEntity.getEntity(((CraftServer) Bukkit.getServer()), entity))
                .filter(craftEntity -> craftEntity.getWorld().equals(w)).toArray(CraftEntity[]::new);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean requireCommandArgument() {
        return true;
    }

}
