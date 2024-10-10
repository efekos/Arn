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

package dev.efekos.arn.resolver.impl.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import dev.efekos.arn.common.annotation.CommandArgument;
import dev.efekos.arn.common.annotation.modifier.Vector;
import dev.efekos.arn.common.resolver.CommandArgumentResolver;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import org.bukkit.Location;

import java.lang.reflect.Parameter;

/**
 * An implementation of {@link CommandArgumentResolver}. Resolves {@link Location} arguments using vec3 position.
 *
 * @author efekos
 * @since 0.1
 */
public final class CmdVectorArg implements CommandArgumentResolver {

    /**
     * Creates a new resolver.
     */
    public CmdVectorArg() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(CommandArgument.class) && parameter.getType().equals(Location.class) && parameter.isAnnotationPresent(Vector.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArgumentBuilder<?, ?> apply(Parameter parameter) {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        return Commands.argument(s.isEmpty() ? parameter.getName() : s, Vec3Argument.vec3());
    }
}
