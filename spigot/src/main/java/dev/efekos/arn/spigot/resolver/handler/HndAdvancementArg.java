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
import dev.efekos.arn.common.resolver.BaseHndResolver;
import dev.efekos.arn.spigot.SpigotCommandHandlerMethod;
import dev.efekos.arn.spigot.face.SpigotHndResolver;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import org.bukkit.advancement.Advancement;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_21_R1.advancement.CraftAdvancement;

import java.lang.reflect.Parameter;

;

/**
 * An implementation of {@link BaseHndResolver}. Resolves
 * {@link BlockData} arguments.
 *
 * @author efekos
 * @since 0.1
 */
public final class HndAdvancementArg implements SpigotHndResolver {

    /**
     * Creates a new resolver.
     */
    public HndAdvancementArg() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(CommandArgument.class) && parameter.getType().equals(Advancement.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean requireCommandArgument() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Advancement resolve(Parameter parameter, SpigotCommandHandlerMethod method,
                               CommandContext<CommandSourceStack> context) throws ArnSyntaxException {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        try {
            AdvancementHolder advancement = ResourceLocationArgument.getAdvancement(context,
                    s.isEmpty() ? parameter.getName() : s);
            return new CraftAdvancement(advancement);
        } catch (CommandSyntaxException e) {
            throw new ArnSyntaxException(e.getMessage());
        }
    }

}
