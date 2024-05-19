package dev.efekos.arn.resolver.impl.command;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import dev.efekos.arn.annotation.CommandArgument;
import dev.efekos.arn.annotation.NumberLimitations;
import dev.efekos.arn.resolver.CommandArgumentResolver;
import net.minecraft.commands.CommandDispatcher;

import java.lang.reflect.Parameter;

/**
 * An implementation of {@link CommandArgumentResolver}. Resolves {@link Float} and {@code float} arguments.
 * @since 0.1
 * @author efekos
 */
public final class CmdFloatArg implements CommandArgumentResolver {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(CommandArgument.class) && (parameter.getType().equals(float.class)||parameter.getType().equals(Float.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArgumentBuilder<?,?> apply(Parameter parameter) {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        NumberLimitations limitations = parameter.getAnnotation(NumberLimitations.class);
        boolean b = limitations != null;
        return CommandDispatcher.a(s.isEmpty() ?parameter.getName():s, b?FloatArgumentType.floatArg(limitations.min(),limitations.max()):FloatArgumentType.floatArg());
    }
}
