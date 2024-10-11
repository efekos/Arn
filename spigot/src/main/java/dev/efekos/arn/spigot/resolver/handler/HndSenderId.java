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
import dev.efekos.arn.common.annotation.FromSender;
import dev.efekos.arn.common.resolver.BaseHndResolver;
import dev.efekos.arn.spigot.SpigotCommandHandlerMethod;
import dev.efekos.arn.spigot.face.SpigotHndResolver;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Parameter;
import java.util.UUID;

;

/**
 * An implementation of {@link BaseHndResolver}. Resolves
 * {@link java.util.UUID}s of {@link CommandSender}s.
 *
 * @author efekos
 * @since 0.2
 */
public final class HndSenderId implements SpigotHndResolver {

    /**
     * Creates a new resolver.
     */
    public HndSenderId() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(FromSender.class) && parameter.getType().equals(UUID.class);
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
    public Object resolve(Parameter parameter, SpigotCommandHandlerMethod method,
                          CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getBukkitSender();
        if (sender instanceof Player)
            return ((Player) sender).getUniqueId();
        return null;
    }

}
