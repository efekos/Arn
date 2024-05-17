package dev.efekos.arn.resolver.impl.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import dev.efekos.arn.annotation.CommandArgument;
import dev.efekos.arn.annotation.material.Item;
import dev.efekos.arn.resolver.CommandArgumentResolver;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandDispatcher;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.item.ArgumentItemStack;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Parameter;

public class CmdItemArg implements CommandArgumentResolver {
    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(CommandArgument.class) && parameter.getType().equals(Material.class) && parameter.isAnnotationPresent(Item.class);
    }


    private static CommandBuildContext context;

    private static void initializeContext(){
        FeatureFlagSet flagSet = FeatureFlagSet.a(FeatureFlags.a);
        IRegistryCustom.Dimension holderlookup = ((CraftServer) Bukkit.getServer()).getHandle().c().aZ();
        context = CommandBuildContext.a(holderlookup,flagSet);
    }

    @Override
    public ArgumentBuilder<?,?> apply(Parameter parameter) {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        if(context==null) initializeContext();
        return CommandDispatcher.a(s.isEmpty()?parameter.getName():s, ResourceArgument.a(context, Registries.F));
    }
}
