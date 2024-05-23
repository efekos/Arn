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

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.efekos.arn.annotation.FromSender;
import dev.efekos.arn.annotation.modifier.sender.*;
import dev.efekos.arn.data.CommandHandlerMethod;
import dev.efekos.arn.resolver.CommandHandlerMethodArgumentResolver;
import net.minecraft.commands.CommandListenerWrapper;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Parameter;


/**
 * An implementation of {@link CommandHandlerMethodArgumentResolver}. Resolves different kinds of number values
 * from a {@link Player} sender.
 *
 * @author efekos
 * @since 0.2
 */
public final class HndSenderStat implements CommandHandlerMethodArgumentResolver {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(FromSender.class) && ((parameter.getType().equals(Integer.class) || parameter.getType().equals(int.class)) || (parameter.getType().equals(Float.class) || parameter.getType().equals(float.class)) || (parameter.getType().equals(Double.class) || parameter.getType().equals(double.class)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean requireCommandArgument() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object resolve(Parameter parameter, CommandHandlerMethod method, CommandContext<CommandListenerWrapper> context) throws CommandSyntaxException {
        CommandSender sender = context.getSource().getBukkitSender();
        if (!(sender instanceof Player)) return null;
        Player player = (Player) sender;

        if (parameter.isAnnotationPresent(Experience.class)) return caster(parameter.getType(), player.getExp());
        if (parameter.isAnnotationPresent(ExpLevel.class)) return caster(parameter.getType(), player.getLevel());
        if (parameter.isAnnotationPresent(FoodLevel.class)) return caster(parameter.getType(), player.getFoodLevel());
        if (parameter.isAnnotationPresent(Health.class)) return caster(parameter.getType(), player.getHealth());
        if (parameter.isAnnotationPresent(MaxHealth.class))
            return caster(parameter.getType(), player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        return player.getHealth();
    }

    /**
     * Casts given number to the correct type depending on what the parameter type is.
     *
     * @param paramType Type of the parameter.
     * @param value     Value to cast.
     * @return Same value that might be cast to another class.
     */
    private Object caster(Class<?> paramType, Object value) {
        if (paramType.equals(int.class) || paramType.equals(Integer.class)) return (Integer) value;
        if (paramType.equals(float.class) || paramType.equals(Float.class)) return (Float) value;
        if (paramType.equals(double.class) || paramType.equals(Double.class)) return (Double) value;
        return value;
    }
}
