/*
 * MIT License
 *
 * Copyright (c) 2025 efekos
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
import dev.efekos.arn.common.base.BaseHndResolver;
import dev.efekos.arn.spigot.SpigotCommandHandlerMethod;
import dev.efekos.arn.spigot.annotation.*;
import dev.efekos.arn.spigot.face.SpigotHndResolver;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Parameter;

;

/**
 * An implementation of {@link BaseHndResolver}. Resolves
 * equipment of {@link Player} senders.
 *
 * @author efekos
 * @since 0.2
 */
public final class HndSenderEqu implements SpigotHndResolver {

    /**
     * Creates a new resolver.
     */
    public HndSenderEqu() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(FromSender.class) && parameter.getType().equals(ItemStack.class);
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
    public ItemStack resolve(Parameter parameter, SpigotCommandHandlerMethod method,
                             CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getBukkitSender();
        if (!(sender instanceof Player player))
            return null;

        if (parameter.isAnnotationPresent(OffHand.class))
            return player.getInventory().getItemInOffHand();
        if (parameter.isAnnotationPresent(Helmet.class))
            return player.getInventory().getHelmet();
        if (parameter.isAnnotationPresent(Chestplate.class))
            return player.getInventory().getChestplate();
        if (parameter.isAnnotationPresent(Leggings.class))
            return player.getInventory().getLeggings();
        if (parameter.isAnnotationPresent(Boots.class))
            return player.getInventory().getBoots();
        return player.getInventory().getItemInMainHand();
    }
}
