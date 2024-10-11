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
import io.papermc.paper.command.brigadier.argument.predicate.ItemStackPredicate;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.JukeboxSong;
import org.bukkit.MusicInstrument;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockState;
import org.bukkit.block.BlockType;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.generator.structure.Structure;
import org.bukkit.generator.structure.StructureType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.UUID;

public class PaperArnConfigurer implements PaperArnConfig {

    @Override
    public void addHandlerMethodArgumentResolvers(List<PaperHndResolver> resolvers) {
        resolvers.addAll(List.of(
                new HndEntitiesArg(), new HndEntityArg(),  new HndBlockPosArg(), new HndPlayerArg(), new HndPlayersArg(),
                new HndVectorArg(),

                new HndNonResolverArg(Integer.class),new HndNonResolverArg(int.class),new HndNonResolverArg(Double.class),
                new HndNonResolverArg(double.class),new HndNonResolverArg(Float.class),new HndNonResolverArg(float.class),
                new HndNonResolverArg(String.class),new HndNonResolverArg(Boolean.class),new HndNonResolverArg(boolean.class),
                new HndNonResolverArg(Long.class),new HndNonResolverArg(long.class), new HndNonResolverArg(Component.class),
                new HndNonResolverArg(Attribute.class),new HndNonResolverArg(Biome.class),new HndNonResolverArg(ItemType.class),
                new HndNonResolverArg(EntityType.class),new HndNonResolverArg(BlockType.class),new HndNonResolverArg(DamageType.class),
                new HndNonResolverArg(Sound.class),new HndNonResolverArg(JukeboxSong.class),new HndNonResolverArg(Enchantment.class),
                new HndNonResolverArg(PotionEffectType.class),new HndNonResolverArg(Structure.class),new HndNonResolverArg(StructureType.class),
                new HndNonResolverArg(MusicInstrument.class), new HndNonResolverArg(ItemStack.class), new HndNonResolverArg(UUID.class),
                new HndNonResolverArg(ItemStackPredicate.class), new HndNonResolverArg(GameMode.class), new HndNonResolverArg(BlockState.class)
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
