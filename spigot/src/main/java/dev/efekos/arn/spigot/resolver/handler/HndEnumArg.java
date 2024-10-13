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

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.efekos.arn.common.BaseHndResolver;
import dev.efekos.arn.common.annotation.CommandArgument;
import dev.efekos.arn.common.annotation.CustomArgument;
import dev.efekos.arn.common.exception.ArnCommandException;
import dev.efekos.arn.common.exception.ArnSyntaxException;
import dev.efekos.arn.spigot.SpigotCommandHandlerMethod;
import dev.efekos.arn.spigot.face.SpigotHndResolver;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceArgument;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.lang.reflect.Parameter;
import java.util.Locale;

;

/**
 * An implementation of {@link BaseHndResolver}. Resolves
 * {@link Enum} arguments annotated with
 * {@link CustomArgument}.
 *
 * @author efekos
 * @since 0.1
 */
public final class HndEnumArg implements SpigotHndResolver {

    /**
     * Main {@link Enum} class this resolver will handle.
     */
    private final Class<? extends Enum<?>> enumClass;

    /**
     * Creates a new instance of this resolver.
     *
     * @param enumClass Main {@link Enum} class this resolver will handle.
     */
    public HndEnumArg(Class<? extends Enum<?>> enumClass) {
        this.enumClass = enumClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.getType().equals(enumClass) && parameter.isAnnotationPresent(CommandArgument.class);
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
    public Enum<?> resolve(Parameter parameter, SpigotCommandHandlerMethod method,
                           CommandContext<CommandSourceStack> context) throws ArnSyntaxException {

        String s = parameter.getAnnotation(CommandArgument.class).value();
        String string = StringArgumentType.getString(context, s.isEmpty() ? parameter.getName() : s);
        try {
            return Enum.valueOf(enumClass.getEnumConstants()[0].getClass(), string.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException e) {
            throw new ArnSyntaxException(ResourceArgument.ERROR_UNKNOWN_RESOURCE.create(string,
                    enumClass.getAnnotation(CustomArgument.class).value()).getMessage());
        } catch (NullPointerException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "ARN-ERROR");
            Bukkit.getConsoleSender().sendMessage(enumClass.toString());
            Bukkit.getConsoleSender().sendMessage(s);
            Bukkit.getConsoleSender().sendMessage(string);
            throw new RuntimeException(new ArnCommandException(
                    "There is something wrong with HndEnumArg. Please report this issue to github: https://github.com/efekos/Arn",
                    e));
        }
    }
}
