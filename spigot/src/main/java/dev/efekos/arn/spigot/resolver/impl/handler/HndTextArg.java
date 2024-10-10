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
import dev.efekos.arn.common.data.CommandHandlerMethod;
import dev.efekos.arn.common.resolver.CommandHandlerMethodArgumentResolver;
import dev.efekos.arn.spigot.resolver.SpigotHndResolver;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_21_R1.CraftServer;

import java.lang.reflect.Parameter;

/**
 * An implementation of {@link CommandHandlerMethodArgumentResolver}. Resolves {@link BaseComponent} arguments.
 *
 * @author efekos
 * @since 0.1
 */
public final class HndTextArg implements SpigotHndResolver {

    /**
     * A context that is needed to resolve an argument.
     */
    private static CommandBuildContext context;

    /**
     * Creates a new resolver.
     */
    public HndTextArg() {
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
        return parameter.isAnnotationPresent(CommandArgument.class) && parameter.getType().equals(BaseComponent.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BaseComponent resolve(Parameter parameter, CommandHandlerMethod method, CommandContext<CommandSourceStack> ctx) {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        Component component = ComponentArgument.getComponent(ctx, s.isEmpty() ? parameter.getName() : s);
        if (context == null) initializeContext();
        String json = Component.Serializer.toJson(component, context);
        return ComponentSerializer.deserialize(json);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean requireCommandArgument() {
        return true;
    }
}
