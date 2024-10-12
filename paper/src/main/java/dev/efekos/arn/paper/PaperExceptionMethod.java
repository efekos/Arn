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

package dev.efekos.arn.paper;

import com.mojang.brigadier.context.CommandContext;
import dev.efekos.arn.common.data.BaseExceptionHandlerMethod;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public final class PaperExceptionMethod extends BaseExceptionHandlerMethod<CommandContext<CommandSourceStack>> {

    public PaperExceptionMethod(Method method, Class<? extends Exception> exceptionClass) {
        super(method, exceptionClass);
    }

    @Override
    public List<Object> fillParams(Throwable ex, CommandContext<CommandSourceStack> commandContext) {
        List<Object> objects = new ArrayList<>();

        for (Parameter parameter : method.getParameters()) {
            if (Player.class.isAssignableFrom(parameter.getType()))
                objects.add(commandContext.getSource().getSender());
            else if (CommandSender.class.isAssignableFrom(parameter.getType()))
                objects.add(commandContext.getSource().getSender());
            else if (parameter.getType().isAssignableFrom(exceptionClass) || exceptionClass.equals(parameter.getType()))
                objects.add(ex);
            else objects.add(null);
        }

        return objects;
    }

}
