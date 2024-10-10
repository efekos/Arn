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
import dev.efekos.arn.common.annotation.FromSender;
import dev.efekos.arn.common.annotation.modifier.sender.Name;
import dev.efekos.arn.spigot.data.SpigotCommandHandlerMethod;;
import dev.efekos.arn.common.resolver.CommandHandlerMethodArgumentResolver;
import dev.efekos.arn.spigot.resolver.SpigotHndResolver;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.lang.reflect.Parameter;
import java.util.Locale;

/**
 * An implementation of {@link CommandHandlerMethodArgumentResolver}. Resolves
 * names of {@link CommandSender}s.
 *
 * @author efekos
 * @since 0.2
 */
public final class HndSenderName implements SpigotHndResolver {

    /**
     * Creates a new resolver.
     */
    public HndSenderName() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(FromSender.class) && parameter.getType().equals(String.class)
                && (parameter.isAnnotationPresent(Name.class)
                        || parameter.getName().toLowerCase(Locale.ENGLISH).endsWith("name"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean requireCommandArgument() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String resolve(Parameter parameter, SpigotCommandHandlerMethod method,
            CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getBukkitSender();
        if (sender instanceof ConsoleCommandSender)
            return "CONSOLE";
        if (sender instanceof BlockCommandSender) {
            Block b = ((BlockCommandSender) sender).getBlock();
            Location location = b.getLocation();
            return "[@" + b.getType().getKey().getKey() + ":" + location.getBlockX() + ":" + location.getBlockY() + ":"
                    + location.getBlockZ() + "]";
        }
        return sender.getName();
    }

}
