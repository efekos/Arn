/*
 * MIT License
 *
 * Copyright (c) 2025 efekos
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
import dev.efekos.arn.paper.face.PaperCmdResolver;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.registry.RegistryKey;

import java.lang.reflect.Parameter;

public final class CmdResourceArg implements PaperCmdResolver {

    private final Class<?> clazz;
    private final RegistryKey<?> registryKey;

    public <T> CmdResourceArg(Class<T> clazz, RegistryKey<T> registryKey) {
        this.clazz = clazz;
        this.registryKey = registryKey;
    }

    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(CommandArgument.class) && parameter.getType().equals(clazz);
    }

    @Override
    public ArgumentBuilder<CommandSourceStack, ?> apply(Parameter parameter) {
        return Commands.argument(getName(parameter), ArgumentTypes.resource(registryKey));
    }
}
