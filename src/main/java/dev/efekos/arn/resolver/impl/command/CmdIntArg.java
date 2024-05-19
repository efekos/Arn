package dev.efekos.arn.resolver.impl.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import dev.efekos.arn.annotation.CommandArgument;
import dev.efekos.arn.annotation.modifier.InventorySlot;
import dev.efekos.arn.annotation.modifier.Item;
import dev.efekos.arn.resolver.CommandArgumentResolver;
import net.minecraft.commands.CommandDispatcher;

import java.lang.reflect.Parameter;

/**
 * An implementation of {@link CommandArgumentResolver}. Resolves {@link Integer} and {@code int} arguments.
 * @since 0.1
 * @author efekos
 */
public final class CmdIntArg implements CommandArgumentResolver {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(CommandArgument.class) && (parameter.getType().equals(int.class)||parameter.getType().equals(Integer.class)) && !parameter.isAnnotationPresent(InventorySlot.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArgumentBuilder<?,?> apply(Parameter parameter) {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        Item.NumberLimitations limitations = parameter.getAnnotation(Item.NumberLimitations.class);
        boolean b = limitations != null;
        return CommandDispatcher.a(s.isEmpty() ?parameter.getName():s, b? IntegerArgumentType.integer((int)limitations.min(),(int)limitations.max()):IntegerArgumentType.integer());
    }
}
