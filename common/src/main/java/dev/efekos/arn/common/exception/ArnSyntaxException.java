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

package dev.efekos.arn.common.exception;

import dev.efekos.arn.common.annotation.Command;
import dev.efekos.arn.common.base.BaseCustomArgumentType;

/**
 * An {@link ArnException} that is used to replace Brigadier's
 * {@code com.mojang.brigadier.exceptions.CommandSyntaxException} so you don't have to include NMS in your plugin to
 * use Arn. Methods annotated with {@link Command} and {@link BaseCustomArgumentType} can
 * throw this exception with a message that will pop up to the player with red color by default.
 *
 * @author efekos
 * @since 0.3
 */
public class ArnSyntaxException extends ArnException {

    /**
     * Creates a new exception.
     *
     * @param message Exception message.
     */
    public ArnSyntaxException(String message) {
        super(message);
    }

}