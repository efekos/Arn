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

package dev.efekos.arn.spigot.resolver.impl.command;


import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.efekos.arn.common.annotation.CommandArgument;
import dev.efekos.arn.common.resolver.CommandArgumentResolver;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import org.bukkit.advancement.Advancement;

import java.lang.reflect.Parameter;
import java.util.Collection;

/**
 * An implementation of {@link CommandArgumentResolver}. Resolves {@link org.bukkit.advancement.Advancement} arguments.
 *
 * @author efekos
 * @since 0.1
 */
public final class CmdAdvancementArg implements CommandArgumentResolver {

    /**
     * A suggestion provider that provides all advancements loaded in the game.
     */
    private static final SuggestionProvider<CommandSourceStack> c = (var0, var1) -> {
        Collection<AdvancementHolder> var2 = var0.getSource().getServer().getAdvancements().getAllAdvancements();
        return SharedSuggestionProvider.suggestResource(var2.stream().map(AdvancementHolder::id), var1);
    };

    /**
     * Creates a new resolver.
     */
    public CmdAdvancementArg() {
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
    public ArgumentBuilder apply(Parameter parameter) {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        return Commands.argument(s.isEmpty() ? parameter.getName() : s, ResourceLocationArgument.id()).suggests(c);
    }

}
