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

package dev.efekos.arn.annotation;


import dev.efekos.arn.annotation.modifier.Block;
import dev.efekos.arn.annotation.modifier.Item;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies that applied parameter is a command argument. When a method parameter is a CommandArgument, {@link dev.efekos.arn.Arn}
 * will try to find an applicable {@link dev.efekos.arn.resolver.CommandHandlerMethodArgumentResolver} to provide a value
 * to the parameter when calling a command handler method. If there is a {@link dev.efekos.arn.resolver.CommandArgumentResolver}
 * associated with the type of the parameter, an argument node that represents the parameter will be added to command
 * structure. As of Arn 0.1, only the following types are supported as a CommandArgument.
 * <ul>
 *     <li>{@link Boolean}</li>
 *     <li>{@link Float}</li>
 *     <li>{@link Integer}</li>
 *     <li>{@link Long}</li>
 *     <li>{@link String}</li>
 *     <li>{@link org.bukkit.block.BlockState}</li>
 *     <li>{@link org.bukkit.enchantments.Enchantment}</li>
 *     <li>{@link org.bukkit.entity.Entity}</li>
 *     <li>{@link org.bukkit.entity.Entity}[]</li>
 *     <li>{@link org.bukkit.GameMode}</li>
 *     <li>{@link org.bukkit.Location} (block position only)</li>
 *     <li>{@link org.bukkit.Material} (separated into blocks and items, see {@link Item} and {@link Block}.)</li>
 *     <li>{@link org.bukkit.World}</li>
 *     <li>{@link org.bukkit.entity.Player}</li>
 *     <li>{@link org.bukkit.entity.Player}[]</li>
 *     <li>{@link org.bukkit.potion.PotionEffectType}</li>
 *     <li>{@link net.md_5.bungee.api.chat.BaseComponent}</li>
 *     <li>{@link org.bukkit.advancement.Advancement}</li>
 *     <li>{@link org.bukkit.attribute.Attribute}</li>
 *     <li>{@link Container} {@link Enum}s that are annotated with {@link CustomArgument}.</li>
 * </ul>
 * @since 0.1
 * @author efekos
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface CommandArgument {
    /**
     * Name of the command argument that will be shown to user. Defaults to name of the parameter. If the plugin isn't
     * compiled with {@code -parameters}, then the parameter name will be arg<i>N</i>, where <i>N</i> is the index of
     * the parameter in the descriptor of the method which declares the parameter.
     * @return Name of this argument.
     */
    String value() default "";
}