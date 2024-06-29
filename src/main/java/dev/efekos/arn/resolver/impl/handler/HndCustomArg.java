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

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.efekos.arn.Arn;
import dev.efekos.arn.annotation.CommandArgument;
import dev.efekos.arn.argument.CustomArgumentType;
import dev.efekos.arn.data.CommandHandlerMethod;
import dev.efekos.arn.exception.ArnSyntaxException;
import dev.efekos.arn.resolver.CommandHandlerMethodArgumentResolver;
import net.minecraft.commands.CommandSourceStack;

import java.lang.reflect.Parameter;


/**
 * An implementation of {@link CommandHandlerMethodArgumentResolver}. Resolves {@link CustomArgumentType}s.
 *
 * @author efekos
 * @since 0.3.1
 */
public final class HndCustomArg implements CommandHandlerMethodArgumentResolver {

    /**
     * An instance of the {@link CustomArgumentType} this resolver resolves.
     */
    private final CustomArgumentType<?> customArgumentType;

    /**
     * Creates a new resolver.
     *
     * @param customArgumentType An instance of the {@link CustomArgumentType} this resolver resolves.
     */
    public HndCustomArg(CustomArgumentType<?> customArgumentType) {
        this.customArgumentType = customArgumentType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(CommandArgument.class) && parameter.getType().equals(customArgumentType.getType());
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
    public Object resolve(Parameter parameter, CommandHandlerMethod method, CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        String string = StringArgumentType.getString(context, s.isEmpty() ? parameter.getName() : s);
        try {
            return customArgumentType.parse(context.getSource().getBukkitSender(), string);
        } catch (ArnSyntaxException e) {
            throw Arn.GENERIC.create(e.getMessage());
        }
    }
}
