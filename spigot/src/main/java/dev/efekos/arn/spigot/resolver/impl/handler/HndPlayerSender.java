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
import dev.efekos.arn.common.annotation.CommandArgument;
import dev.efekos.arn.common.resolver.CommandHandlerMethodArgumentResolver;
import dev.efekos.arn.spigot.data.SpigotCommandHandlerMethod;
import dev.efekos.arn.spigot.resolver.SpigotHndResolver;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Parameter;

;

/**
 * An implementation of {@link CommandHandlerMethodArgumentResolver}. Resolves
 * {@link Player} senders.
 *
 * @author efekos
 * @since 0.1
 */
public final class HndPlayerSender implements SpigotHndResolver {

    /**
     * Creates a new resolver.
     */
    public HndPlayerSender() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.getType().equals(Player.class) && !parameter.isAnnotationPresent(CommandArgument.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Player resolve(Parameter parameter, SpigotCommandHandlerMethod method,
                          CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getBukkitSender();
        if (sender instanceof Player)
            return ((Player) sender);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean requireCommandArgument() {
        return false;
    }
}
