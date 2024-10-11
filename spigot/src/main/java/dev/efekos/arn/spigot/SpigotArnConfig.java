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

package dev.efekos.arn.spigot;

import dev.efekos.arn.common.annotation.modifier.InventorySlot;
import dev.efekos.arn.common.annotation.modifier.Vector;
import dev.efekos.arn.common.data.ExceptionMap;
import dev.efekos.arn.spigot.face.SpArnConfig;
import dev.efekos.arn.spigot.face.SpigotCmdResolver;
import dev.efekos.arn.spigot.face.SpigotHndResolver;
import dev.efekos.arn.spigot.resolver.command.*;
import dev.efekos.arn.spigot.resolver.handler.*;

import java.util.List;

/**
 * Default configuration of Arn.
 *
 * @author efekos
 * @since 0.2
 */
public final class SpigotArnConfig implements SpArnConfig {

    /***
     * Creates a new configurer.
     */
    public SpigotArnConfig() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addHandlerMethodArgumentResolvers(List<SpigotHndResolver> resolvers) {
        resolvers.addAll(List.of(
                new HndIntArg(), new HndStringArg(), new HndPlayerSender(), new HndBooleanArg(), new HndDoubleArg(),
                new HndLocationArg(), new HndLongArg(), new HndTextArg(), new HndEffectTypeArg(), new HndGameModeArg(),
                new HndPlayerArg(), new HndMultiplePlayerArg(), new HndDimensionArg(), new HndEntityArg(),
                new HndMultipleEntityArg(), new HndFloatArg(), new HndEnchantmentArg(), new HndItemArg(), new HndBlockArg(),
                new HndItemStackArg(), new HndBlockDataArg(), new HndSender(), new HndConsoleCommandSender(),
                new HndBlockCommandSender(), new HndAdvancementArg(), new HndAttributeArg(), new HndVectorArg(),
                new HndInventorySlotArg(), new HndSenderEqu(), new HndSenderInv(), new HndSenderId(), new HndSenderStat(),
                new HndSenderStat(), new HndSenderName(), new HndSenderLoc()
        ));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addArgumentResolvers(List<SpigotCmdResolver> resolvers) {
        resolvers.addAll(List.of(
                new CmdBooleanArg(), new CmdDoubleArg(), new CmdLocationArg(), new CmdLocationArg(), new CmdIntArg(),
                new CmdStringArg(), new CmdTextArg(), new CmdEffectTypeArg(), new CmdGameModeArg(), new CmdPlayerArg(),
                new CmdMultiplePlayerArg(), new CmdDimensionArg(), new CmdEntityArg(), new CmdMultipleEntityArg(),
                new CmdFloatArg(), new CmdEnchantmentArg(), new CmdItemArg(), new CmdBlockArg(), new CmdItemStackArg(),
                new CmdAdvancementArg(), new CmdAttributeArg(), new CmdVectorArg(), new CmdInventorySlotArg()
        ));
    }

    @Override
    public void putArgumentResolverExceptions(ExceptionMap<SpigotCmdResolver> map) {
        map.put(CmdLocationArg.class, Vector.class);
        map.put(CmdIntArg.class, InventorySlot.class);
    }

    @Override
    public void putHandlerMethodArgumentResolverExceptions(ExceptionMap<SpigotHndResolver> map) {
        map.put(HndLocationArg.class, Vector.class);
        map.put(HndIntArg.class, InventorySlot.class);
    }

}
