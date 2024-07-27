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

package dev.efekos.arn.argument;

import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceLocationArgument;

import java.util.function.Supplier;

/**
 * Represents registration type of a {@link CustomArgumentType}. This registration type affects which characters you can
 * use in your argument and when Minecraft will throw an error before the flow gets to your command handling.
 *
 * @author efekos
 * @since 0.3.4
 */
public enum ArgumentRegistration {

    /**
     * Uses resource locations (also known as {@link org.bukkit.NamespacedKey}s) as argument syntax. If your argument
     * suggestions are a list of ids that are made out of a namespace, a {@code :} and a key, this registration type
     * will be useful.
     */
    ID(ResourceLocationArgument::id, (context, name) -> ResourceLocationArgument.getId(context, name).toString()),

    /**
     * Uses word arguments. If your suggestions are a list of words made out of lowercase English characters, this
     * registration type will be useful.
     */
    WORD(StringArgumentType::word, StringArgumentType::getString),

    /**
     * Similar to {@link #WORD}, but allows symbols and spaces using quotation marks.
     */
    STRING(StringArgumentType::string, StringArgumentType::getString),

    /**
     * Uses integer argument type. If your argument is going to parse an integer in a specific way, this registration
     * type will be useful.
     */
    INTEGER(IntegerArgumentType::integer, (context, name) -> IntegerArgumentType.getInteger(context, name) + ""),

    /**
     * Uses double argument type. If your argument is going to parse a double in a specific way, this registration
     * type will be useful.
     */
    DOUBLE(DoubleArgumentType::doubleArg, (context, name) -> DoubleArgumentType.getDouble(context, name) + ""),

    /**
     * Uses boolean argument type. If your argument is going to parse a bool in a specific way, this registration type
     * will be useful.
     */
    BOOLEAN(BoolArgumentType::bool, (context, name) -> BoolArgumentType.getBool(context, name) + "");

    /**
     * A {@link Supplier} for the {@link ArgumentType} needed for this registration.
     */
    private final Supplier<ArgumentType<?>> func;

    /**
     * A function that will return a {@link String} value accordingly to {@link ArgumentType} provided from
     * {@link #func}. Needs a {@link CommandContext} and name of the argument as a {@link String}.
     */
    private final GetFunction getFunction;

    /**
     * Creates a new argument registration.
     *
     * @param func        {@link #func}.
     * @param getFunction {@link #getFunction}.
     */
    ArgumentRegistration(Supplier<ArgumentType<?>> func, GetFunction getFunction) {
        this.func = func;
        this.getFunction = getFunction;
    }

    /**
     * Returns {@link ArgumentType} supplied into this registration type.
     *
     * @return What was supplied to {@link #func}.
     */
    public ArgumentType<?> getFunc() {
        return func.get();
    }

    /**
     * Returns value of the argument as a string using {@link #getFunction}.
     *
     * @param context Context of the command.
     * @param name    Name of the argument.
     * @return A {@link String} that represents or is the value of the argument.
     */
    public String getV(CommandContext<CommandSourceStack> context, String name) {
        return getFunction.get(context, name);
    }

    /**
     * A small interface used to specify getter functions of {@link ArgumentRegistration} types.
     *
     * @author efekos
     * @since 0.3.4
     */
    @FunctionalInterface
    interface GetFunction {

        /**
         * Returns value of the argument as a {@link String}.
         *
         * @param context Context of the command.
         * @param name    Name of the argument.
         * @return A {@link String} that represents or is the value of the argument.
         */
        String get(CommandContext<CommandSourceStack> context, String name);

    }
}
