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
import dev.efekos.arn.common.annotation.modifier.Item;
import dev.efekos.arn.common.data.CommandHandlerMethod;
import dev.efekos.arn.common.exception.ArnSyntaxException;
import dev.efekos.arn.common.resolver.CommandHandlerMethodArgumentResolver;
import dev.efekos.arn.spigot.resolver.SpigotHndResolver;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import java.lang.reflect.Parameter;
import java.util.Arrays;

/**
 * An implementation of {@link CommandHandlerMethodArgumentResolver}. Resolves {@link Material} arguments that is an
 * {@link Item}.
 *
 * @author efekos
 * @since 0.1
 */
public final class HndItemArg implements SpigotHndResolver {

    /**
     * Creates a new resolver.
     */
    public HndItemArg() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(CommandArgument.class) && parameter.getType().equals(Material.class) && parameter.isAnnotationPresent(Item.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Material resolve(Parameter parameter, CommandHandlerMethod method, CommandContext<CommandSourceStack> context) throws ArnSyntaxException {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        Holder.Reference<net.minecraft.world.item.Item> itemc = null;
        try {
            itemc = ResourceArgument.getResource(context, s.isEmpty() ? parameter.getName() : s, Registries.ITEM);
        } catch (CommandSyntaxException e) {
            throw new ArnSyntaxException(e);
        }
        net.minecraft.world.item.Item item = itemc.value();
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(item);
        return Arrays.stream(Material.values()).filter(material -> material.getKey().equals(new NamespacedKey(key.getNamespace(), key.getPath()))).findFirst().orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean requireCommandArgument() {
        return true;
    }
}
