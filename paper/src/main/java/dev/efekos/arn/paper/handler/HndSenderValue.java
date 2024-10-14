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

package dev.efekos.arn.paper.handler;

import com.mojang.brigadier.context.CommandContext;
import dev.efekos.arn.common.annotation.FromSender;
import dev.efekos.arn.common.exception.ArnSyntaxException;
import dev.efekos.arn.paper.PaperCommandMethod;
import dev.efekos.arn.paper.face.PaperHndResolver;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.function.Function;

public final class HndSenderValue<T> implements PaperHndResolver {

    private final Class<T> clazz;
    private final Class<? extends Annotation> annotation;
    private final Function<Player, T> provider;

    public HndSenderValue(Class<T> clazz, Class<? extends Annotation> annotation, Function<Player, T> provider) {
        this.clazz = clazz;
        this.annotation = annotation;
        this.provider = provider;
    }

    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(FromSender.class) && parameter.isAnnotationPresent(annotation) && parameter.getType().equals(clazz);
    }

    @Override
    public boolean requireCommandArgument() {
        return false;
    }

    @Override
    public Object resolve(Parameter parameter, PaperCommandMethod method, CommandContext<CommandSourceStack> context) throws ArnSyntaxException {
        CommandSender sender = context.getSource().getSender();
        if (!(sender instanceof Player p)) return null;
        return clazz.cast(provider.apply(p));
    }

}
