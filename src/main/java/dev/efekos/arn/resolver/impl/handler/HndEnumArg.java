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
import dev.efekos.arn.annotation.CommandArgument;
import dev.efekos.arn.annotation.CustomArgument;
import dev.efekos.arn.data.CommandHandlerMethod;
import dev.efekos.arn.exception.ArnCommandException;
import dev.efekos.arn.resolver.CommandHandlerMethodArgumentResolver;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ResourceArgument;

import java.lang.reflect.Parameter;
import java.util.Locale;

/**
 * An implementation of {@link CommandHandlerMethodArgumentResolver}. Resolves {@link Enum} arguments annotated with
 * {@link dev.efekos.arn.annotation.CustomArgument}.
 * @since 0.1
 * @author efekos
 */
public class HndEnumArg implements CommandHandlerMethodArgumentResolver {
    /**
     * Main {@link Enum} class this resolver will handle.
     */
    private final Class<? extends Enum<?>> enumClass;

    /**
     * Creates a new instance of this resolver.
     * @param enumClass Main {@link Enum} class this resolver will handle.
     */
    public HndEnumArg(Class<? extends Enum<?>> enumClass) {
        this.enumClass = enumClass;
    }

    /**{@inheritDoc}*/
    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.getType().equals(enumClass)&&parameter.isAnnotationPresent(CommandArgument.class);
    }

    /**{@inheritDoc}*/
    @Override
    public boolean requireCommandArgument() {
        return true;
    }

    /**{@inheritDoc}*/
    @Override
    public Enum<?> resolve(Parameter parameter, CommandHandlerMethod method, CommandContext<CommandListenerWrapper> context) throws CommandSyntaxException {

        String s = parameter.getAnnotation(CommandArgument.class).value();
        String string = StringArgumentType.getString(context, s.isEmpty() ? parameter.getName() : s);
        try {
            return Enum.valueOf(enumClass.getEnumConstants()[0].getClass(),string.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException e) {
            throw ResourceArgument.a.create(string,enumClass.getAnnotation(CustomArgument.class).value());
        } catch (NullPointerException e) {
            System.out.println("ARN-ERROR");
            System.out.println(enumClass);
            System.out.println(s);
            System.out.println(string);
            throw new RuntimeException(new ArnCommandException("There is something wrong with HndEnumArg. Please report this issue to github: https://github.com/efekos/Arn",e));
        }
    }
}
