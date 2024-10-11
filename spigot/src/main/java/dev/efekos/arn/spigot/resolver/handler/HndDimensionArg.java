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
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.World;

import java.lang.reflect.Parameter;

;

/**
 * An implementation of {@link BaseHndResolver}. Resolves
 * {@link DimensionArgument} arguments.
 * <strong>Since this implementation uses {@link DimensionArgument}, which was
 * only made for to dimensions of one world,
 * there isn't any conclusion that this resolver will work with different
 * worlds, such as ones made by Multiverse plugin.
 * </strong>
 *
 * @author efekos
 * @since 0.1
 */
public final class HndDimensionArg implements SpigotHndResolver {

    /**
     * Creates a new resolver.
     */
    public HndDimensionArg() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(CommandArgument.class) && (parameter.getType().equals(World.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public World resolve(Parameter parameter, SpigotCommandHandlerMethod method,
                         CommandContext<CommandSourceStack> context) throws ArnSyntaxException {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        ServerLevel world;
        try {
            world = DimensionArgument.getDimension(context, s.isEmpty() ? parameter.getName() : s);
        } catch (CommandSyntaxException e) {
            throw new ArnSyntaxException(e.getMessage());
        }
        return world.getWorld();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean requireCommandArgument() {
        return true;
    }
}
