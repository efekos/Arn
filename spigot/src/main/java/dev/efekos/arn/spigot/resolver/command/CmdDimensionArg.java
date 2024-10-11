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
import dev.efekos.arn.common.annotation.CommandArgument;
import dev.efekos.arn.common.resolver.CommandArgumentResolver;
import dev.efekos.arn.spigot.resolver.SpigotCmdResolver;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import org.bukkit.World;

import java.lang.reflect.Parameter;

/**
 * An implementation of {@link CommandArgumentResolver}. Resolves {@link World} arguments using {@link DimensionArgument}.
 * <strong>Since this implementation uses {@link DimensionArgument}, which was only made for dimensions of one world,
 * there isn't any conclusion that this resolver will work with different worlds, such as ones made by Multiverse plugin.
 * </strong>
 *
 * @author efekos
 * @since 0.1
 */
public final class CmdDimensionArg implements SpigotCmdResolver {

    /**
     * Creates a new resolver.
     */
    public CmdDimensionArg() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(CommandArgument.class) && parameter.getType().equals(World.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArgumentBuilder<CommandSourceStack, ?> apply(Parameter parameter) {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        return Commands.argument(s.isEmpty() ? parameter.getName() : s, DimensionArgument.dimension());
    }
}