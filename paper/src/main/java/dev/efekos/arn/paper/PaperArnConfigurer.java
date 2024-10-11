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

import dev.efekos.arn.common.data.ExceptionMap;
import dev.efekos.arn.paper.command.*;
import dev.efekos.arn.paper.face.PaperArnConfig;
import dev.efekos.arn.paper.face.PaperCmdResolver;
import dev.efekos.arn.paper.face.PaperHndResolver;
import dev.efekos.arn.paper.handler.*;

import java.util.List;

public class PaperArnConfigurer implements PaperArnConfig {

    @Override
    public void addHandlerMethodArgumentResolvers(List<PaperHndResolver> resolvers) {
        resolvers.addAll(List.of(
                new HndBoolArg(),new HndDoubleArg(),new HndEntitiesArg(),new HndEntityArg(), new HndFloatArg(),
                new HndIntArg(),new HndLocationArg(),new HndLongArg(),new HndPlayerArg(),new HndPlayersArg(),
                new HndStringArg(),new HndVectorArg()
        ));
    }

    @Override
    public void addArgumentResolvers(List<PaperCmdResolver> resolvers) {
        resolvers.addAll(List.of(
                new CmdAttributeArg(),new CmdBiomeArg(),new CmdBlockArg(),new CmdBlockPosArg(),new CmdBlockStateArg(),
                new CmdBoolArg(),new CmdComponentArg(),new CmdDamageTypeArg(),new CmdDoubleArg(),new CmdEffectArg(),
                new CmdEnchantmentArg(), new CmdEntitiesArg(),new CmdEntityArg(),new CmdFloatArg(),new CmdGameModeArg(),
                new CmdIntArg(),new CmdItemPredicateArg(),new CmdItemStackArg(),new CmdLongArg(),new CmdPlayerArg(),
                new CmdPlayersArg(),new CmdSoundArg(),new CmdStringArg(),new CmdUUIDArg(),new CmdVectorArg()
        ));
    }

    @Override
    public void putArgumentResolverExceptions(ExceptionMap<PaperCmdResolver> map) {

    }

    @Override
    public void putHandlerMethodArgumentResolverExceptions(ExceptionMap<PaperHndResolver> map) {

    }
}
