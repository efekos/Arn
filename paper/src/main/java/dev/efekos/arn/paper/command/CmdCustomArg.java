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

package dev.efekos.arn.paper.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import dev.efekos.arn.common.annotation.CommandArgument;
import dev.efekos.arn.common.resolver.BaseCmdResolver;
import dev.efekos.arn.paper.face.CustomArnArgumentType;
import dev.efekos.arn.paper.face.PaperCmdResolver;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

import java.lang.reflect.Parameter;

/**
 * An implementation of {@link BaseCmdResolver}. Resolves {@link CustomArnArgumentType}s.
 *
 * @author efekos
 * @since 0.3.1
 */
public final class CmdCustomArg implements PaperCmdResolver {

    /**
     * An instance of the {@link CustomArnArgumentType} this resolver resolves.
     */
    private final CustomArnArgumentType<?> customArgumentType;

    /**
     * Creates a new resolver.
     *
     * @param customArgumentType An instance of the {@link CustomArnArgumentType} this resolver resolves.
     */
    public CmdCustomArg(CustomArnArgumentType<?> customArgumentType) {
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
    public ArgumentBuilder<CommandSourceStack, ?> apply(Parameter parameter) {
        return Commands.argument(getName(parameter), customArgumentType.getRegistration().getFunc()).suggests((context, builder) -> {
                    for (String s : customArgumentType.suggest(context.getSource().getSender())) {
                        if(s.startsWith(builder.getRemainingLowerCase())) builder.suggest(s);
                    }
                    return builder.buildFuture();
                }
        );
    }
}
