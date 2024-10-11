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
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.JukeboxSong;
import org.bukkit.MusicInstrument;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockType;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.generator.structure.Structure;
import org.bukkit.generator.structure.StructureType;
import org.bukkit.inventory.ItemType;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class PaperArnConfigurer implements PaperArnConfig {

    @Override
    public void addHandlerMethodArgumentResolvers(List<PaperHndResolver> resolvers) {
        resolvers.addAll(List.of(
                new HndBoolArg(), new HndDoubleArg(), new HndEntitiesArg(), new HndEntityArg(), new HndFloatArg(),
                new HndIntArg(), new HndBlockPosArg(), new HndLongArg(), new HndPlayerArg(), new HndPlayersArg(),
                new HndStringArg(), new HndVectorArg(),

                new HndResourceArg(Attribute.class),new HndResourceArg(Biome.class),new HndResourceArg(ItemType.class),
                new HndResourceArg(EntityType.class),new HndResourceArg(BlockType.class),new HndResourceArg(DamageType.class),
                new HndResourceArg(Sound.class),new HndResourceArg(JukeboxSong.class),new HndResourceArg(Enchantment.class),
                new HndResourceArg(PotionEffectType.class),new HndResourceArg(Structure.class),new HndResourceArg(StructureType.class),
                new HndResourceArg(MusicInstrument.class)
        ));
    }

    @Override
    public void addArgumentResolvers(List<PaperCmdResolver> resolvers) {
        resolvers.addAll(List.of(
                new CmdResourceArg(Attribute.class, RegistryKey.ATTRIBUTE), new CmdResourceArg(Biome.class,RegistryKey.BIOME),
                new CmdResourceArg(ItemType.class,RegistryKey.ITEM), new CmdResourceArg(EntityType.class,RegistryKey.ENTITY_TYPE),
                new CmdResourceArg(BlockType.class,RegistryKey.BLOCK),new CmdResourceArg(DamageType.class,RegistryKey.DAMAGE_TYPE),
                new CmdResourceArg(Sound.class,RegistryKey.SOUND_EVENT),new CmdResourceArg(JukeboxSong.class,RegistryKey.JUKEBOX_SONG),
                new CmdResourceArg(Enchantment.class, RegistryKey.ENCHANTMENT), new CmdResourceArg(PotionEffectType.class,RegistryKey.MOB_EFFECT),
                new CmdResourceArg(Structure.class,RegistryKey.STRUCTURE),new CmdResourceArg(StructureType.class,RegistryKey.STRUCTURE_TYPE),
                new CmdResourceArg(MusicInstrument.class,RegistryKey.INSTRUMENT),

                new CmdBlockPosArg(), new CmdBlockStateArg(),
                new CmdBoolArg(), new CmdComponentArg(), new CmdDoubleArg(), new CmdEntitiesArg(), new CmdEntityArg(), new CmdFloatArg(), new CmdGameModeArg(),
                new CmdIntArg(), new CmdItemPredicateArg(), new CmdItemStackArg(), new CmdLongArg(), new CmdPlayerArg(),
                new CmdPlayersArg(), new CmdStringArg(), new CmdUUIDArg(), new CmdVectorArg()
        ));
    }

    @Override
    public void putArgumentResolverExceptions(ExceptionMap<PaperCmdResolver> map) {

    }

    @Override
    public void putHandlerMethodArgumentResolverExceptions(ExceptionMap<PaperHndResolver> map) {

    }
}
