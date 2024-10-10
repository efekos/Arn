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
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.efekos.arn.common.annotation.CommandArgument;
import dev.efekos.arn.common.exception.ArnSyntaxException;
import dev.efekos.arn.common.resolver.CommandHandlerMethodArgumentResolver;
import dev.efekos.arn.spigot.data.SpigotCommandHandlerMethod;
import dev.efekos.arn.spigot.resolver.SpigotHndResolver;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import org.bukkit.NamespacedKey;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Parameter;

;

/**
 * An implementation of {@link CommandHandlerMethodArgumentResolver}. Resolves
 * {@link PotionEffectType} arguments.
 *
 * @author efekos
 * @since 0.1
 */
public final class HndEffectTypeArg implements SpigotHndResolver {

    /**
     * Creates a new resolver.
     */
    public HndEffectTypeArg() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(CommandArgument.class)
                && parameter.getType().equals(PotionEffectType.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object resolve(Parameter parameter, SpigotCommandHandlerMethod method,
                          CommandContext<CommandSourceStack> context) throws ArnSyntaxException {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        Holder.Reference<MobEffect> f;
        try {
            f = ResourceArgument.getMobEffect(context, s.isEmpty() ? parameter.getName() : s);
        } catch (CommandSyntaxException e) {
            throw new ArnSyntaxException(e);
        }
        MobEffect list = f.value();
        ResourceLocation key = BuiltInRegistries.MOB_EFFECT.getKey(list);
        NamespacedKey effectKey = new NamespacedKey(key.getNamespace(), key.getPath());
        return PotionEffectType.getByKey(effectKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean requireCommandArgument() {
        return true;
    }
}
