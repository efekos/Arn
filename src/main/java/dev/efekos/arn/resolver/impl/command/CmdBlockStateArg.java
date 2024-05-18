package dev.efekos.arn.resolver.impl.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import dev.efekos.arn.annotation.CommandArgument;
import dev.efekos.arn.resolver.CommandArgumentResolver;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandDispatcher;
import net.minecraft.commands.arguments.blocks.ArgumentTile;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import org.bukkit.Bukkit;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;

import java.lang.reflect.Parameter;

public class CmdBlockStateArg implements CommandArgumentResolver {

    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(CommandArgument.class) && parameter.getType().equals(BlockData.class);
    }
    private static CommandBuildContext context;

    private static void initializeContext(){
        FeatureFlagSet flagSet = FeatureFlagSet.a(FeatureFlags.a);
        IRegistryCustom.Dimension holderlookup = ((CraftServer) Bukkit.getServer()).getHandle().c().aZ();
        context = CommandBuildContext.a(holderlookup,flagSet);
    }


    @Override
    public ArgumentBuilder apply(Parameter parameter) {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        if(context==null) initializeContext();
        return CommandDispatcher.a(s.isEmpty()?parameter.getName():s, ArgumentTile.a(context));
    }

}
