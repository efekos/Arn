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

package dev.efekos.arn.spigot.resolver.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import dev.efekos.arn.common.BaseCmdResolver;
import dev.efekos.arn.common.annotation.CommandArgument;
import dev.efekos.arn.spigot.face.SpigotCmdResolver;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_21_R1.CraftServer;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Parameter;

/**
 * An implementation of {@link BaseCmdResolver}. Resolves {@link PotionEffectType} arguments.
 *
 * @author efekos
 * @since 0.1
 */
public final class CmdEffectTypeArg implements SpigotCmdResolver {

    /**
     * A context that is needed to resolve an argument.
     */
    private static CommandBuildContext context;

    /**
     * Creates a new resolver.
     */
    public CmdEffectTypeArg() {
    }

    /**
     * Initializes {@link #context}.
     */
    private static void initializeContext() {
        FeatureFlagSet flagSet = FeatureFlagSet.of(FeatureFlags.VANILLA);
        HolderLookup.Provider holderlookup = ((CraftServer) Bukkit.getServer()).getHandle().getServer().registryAccess();
        context = CommandBuildContext.simple(holderlookup, flagSet);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(CommandArgument.class) && parameter.getType().equals(PotionEffectType.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArgumentBuilder<CommandSourceStack, ?> apply(Parameter parameter) {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        if (context == null) initializeContext();
        return Commands.argument(s.isEmpty() ? parameter.getName() : s, ResourceArgument.resource(context, Registries.MOB_EFFECT));
    }

}
