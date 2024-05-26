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
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.efekos.arn.annotation.FromSender;
import dev.efekos.arn.annotation.modifier.sender.*;
import dev.efekos.arn.data.CommandHandlerMethod;
import dev.efekos.arn.resolver.CommandHandlerMethodArgumentResolver;
import net.minecraft.commands.CommandListenerWrapper;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Parameter;


/**
 * An implementation of {@link CommandHandlerMethodArgumentResolver}. Resolves equipment of {@link Player} senders.
 *
 * @author efekos
 * @since 0.2
 */
public final class HndSenderEqu implements CommandHandlerMethodArgumentResolver {

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
    public ItemStack resolve(Parameter parameter, CommandHandlerMethod method, CommandContext<CommandListenerWrapper> context) throws CommandSyntaxException {
        CommandSender sender = context.getSource().getBukkitSender();
        if (!(sender instanceof Player)) return null;
        Player player = (Player) sender;

        if (parameter.isAnnotationPresent(OffHand.class)) return player.getInventory().getItemInOffHand();
        if (parameter.isAnnotationPresent(Helmet.class)) return player.getInventory().getHelmet();
        if (parameter.isAnnotationPresent(Chestplate.class)) return player.getInventory().getChestplate();
        if (parameter.isAnnotationPresent(Leggings.class)) return player.getInventory().getLeggings();
        if (parameter.isAnnotationPresent(Boots.class)) return player.getInventory().getBoots();
        return player.getInventory().getItemInMainHand();
    }
}
