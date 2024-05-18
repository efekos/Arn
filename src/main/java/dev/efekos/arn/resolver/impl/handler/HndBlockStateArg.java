package dev.efekos.arn.resolver.impl.handler;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.efekos.arn.annotation.CommandArgument;
import dev.efekos.arn.data.CommandHandlerMethod;
import dev.efekos.arn.resolver.CommandHandlerMethodArgumentResolver;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.blocks.ArgumentTile;
import net.minecraft.commands.arguments.blocks.ArgumentTileLocation;
import net.minecraft.world.level.block.state.IBlockData;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_20_R3.block.data.CraftBlockData;

import java.lang.reflect.Parameter;

public class HndBlockStateArg implements CommandHandlerMethodArgumentResolver {

    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(CommandArgument.class) && parameter.getType().equals(BlockData.class);
    }

    @Override
    public boolean requireCommandArgument() {
        return true;
    }

    @Override
    public BlockData resolve(Parameter parameter, CommandHandlerMethod method, CommandContext<CommandListenerWrapper> context) throws CommandSyntaxException {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        ArgumentTileLocation tile = ArgumentTile.a(context, s.isEmpty() ? parameter.getName() : s);
        IBlockData data = tile.a();

        return CraftBlockData.fromData(data);
    }

}
