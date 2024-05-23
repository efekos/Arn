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

package dev.efekos.arn.config;

import dev.efekos.arn.resolver.CommandArgumentResolver;
import dev.efekos.arn.resolver.CommandHandlerMethodArgumentResolver;
import dev.efekos.arn.resolver.impl.command.*;
import dev.efekos.arn.resolver.impl.handler.*;

import java.util.List;

/**
 * Default configuration of Arn.
 * @author efekos
 * @since 0.2
 */
public final class BaseArnConfigurer implements ArnConfigurer {

    /**{@inheritDoc}*/
    @Override
    public void addHandlerMethodArgumentResolvers(List<CommandHandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new HndIntArg());
        resolvers.add(new HndStringArg());
        resolvers.add(new HndPlayerSender());
        resolvers.add(new HndBooleanArg());
        resolvers.add(new HndDoubleArg());
        resolvers.add(new HndLocationArg());
        resolvers.add(new HndLongArg());
        resolvers.add(new HndTextArg());
        resolvers.add(new HndEffectTypeArg());
        resolvers.add(new HndGameModeArg());
        resolvers.add(new HndPlayerArg());
        resolvers.add(new HndMultiplePlayerArg());
        resolvers.add(new HndDimensionArg());
        resolvers.add(new HndEntityArg());
        resolvers.add(new HndMultipleEntityArg());
        resolvers.add(new HndFloatArg());
        resolvers.add(new HndEnchantmentArg());
        resolvers.add(new HndItemArg());
        resolvers.add(new HndBlockArg());
        resolvers.add(new HndItemStackArg());
        resolvers.add(new HndBlockDataArg());
        resolvers.add(new HndSender());
        resolvers.add(new HndConsoleCommandSender());
        resolvers.add(new HndBlockCommandSender());
        resolvers.add(new HndAdvancementArg());
        resolvers.add(new HndAttributeArg());
        resolvers.add(new HndVectorArg());
        resolvers.add(new HndInventorySlotArg());
        resolvers.add(new HndSenderEqu());
        resolvers.add(new HndSenderInv());
        resolvers.add(new HndSenderId());
        resolvers.add(new HndSenderInv());
        resolvers.add(new HndSenderStat());
        resolvers.add(new HndSenderName());
        resolvers.add(new HndSenderLoc());
    }

    /**{@inheritDoc}*/
    @Override
    public void addArgumentResolvers(List<CommandArgumentResolver> resolvers) {
        resolvers.add(new CmdBooleanArg());
        resolvers.add(new CmdDoubleArg());
        resolvers.add(new CmdLocationArg());
        resolvers.add(new CmdLongArg());
        resolvers.add(new CmdIntArg());
        resolvers.add(new CmdStringArg());
        resolvers.add(new CmdTextArg());
        resolvers.add(new CmdEffectTypeArg());
        resolvers.add(new CmdGameModeArg());
        resolvers.add(new CmdPlayerArg());
        resolvers.add(new CmdMultiplePlayerArg());
        resolvers.add(new CmdDimensionArg());
        resolvers.add(new CmdEntityArg());
        resolvers.add(new CmdMultipleEntityArg());
        resolvers.add(new CmdFloatArg());
        resolvers.add(new CmdEnchantmentArg());
        resolvers.add(new CmdItemArg());
        resolvers.add(new CmdBlockArg());
        resolvers.add(new CmdItemStackArg());
        resolvers.add(new CmdBlockDataArg());
        resolvers.add(new CmdAdvancementArg());
        resolvers.add(new CmdAttributeArg());
        resolvers.add(new CmdVectorArg());
        resolvers.add(new CmdInventorySlotArg());
    }
}
