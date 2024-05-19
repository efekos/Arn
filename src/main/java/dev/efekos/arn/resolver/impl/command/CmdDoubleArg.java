package dev.efekos.arn.resolver.impl.command;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import dev.efekos.arn.annotation.CommandArgument;
import dev.efekos.arn.annotation.modifier.Item;
import dev.efekos.arn.resolver.CommandArgumentResolver;
import net.minecraft.commands.CommandDispatcher;

import java.lang.reflect.Parameter;

/**
 * An implementation of {@link CommandArgumentResolver}. Resolves {@link Double} and {@code double} arguments.
 * @since 0.1
 * @author efekos
 */
public final class CmdDoubleArg implements CommandArgumentResolver {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(CommandArgument.class) && (parameter.getType().equals(double.class)||parameter.getType().equals(Double.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArgumentBuilder<?,?> apply(Parameter parameter) {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        Item.NumberLimitations limitations = parameter.getAnnotation(Item.NumberLimitations.class);
        boolean b = limitations != null;
        return CommandDispatcher.a(s.isEmpty() ?parameter.getName():s, b? DoubleArgumentType.doubleArg(limitations.min(),limitations.max()):DoubleArgumentType.doubleArg());
    }
}
