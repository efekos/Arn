package dev.efekos.arn.resolver.impl.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import dev.efekos.arn.annotation.CommandArgument;
import dev.efekos.arn.resolver.CommandArgumentResolver;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandDispatcher;
import net.minecraft.commands.arguments.item.ArgumentItemStack;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Parameter;

/**
 * An implementation of {@link CommandArgumentResolver}. Resolves {@link ItemStack} arguments.
 * @since 0.1
 * @author efekos
 */
public final class CmdItemStackArg implements CommandArgumentResolver {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(CommandArgument.class) && parameter.getType().equals(ItemStack.class);
    }


    /***/
    private static CommandBuildContext context;

    /***/
    private static void initializeContext(){
        FeatureFlagSet flagSet = FeatureFlagSet.a(FeatureFlags.a);
        IRegistryCustom.Dimension holderlookup = ((CraftServer) Bukkit.getServer()).getHandle().c().aZ();
        context = CommandBuildContext.a(holderlookup,flagSet);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArgumentBuilder<?,?> apply(Parameter parameter) {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        if(context==null) initializeContext();
        return CommandDispatcher.a(s.isEmpty()?parameter.getName():s, ArgumentItemStack.a(context));
    }
}