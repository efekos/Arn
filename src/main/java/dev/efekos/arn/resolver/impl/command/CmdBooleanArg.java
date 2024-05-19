package dev.efekos.arn.resolver.impl.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import dev.efekos.arn.annotation.CommandArgument;
import dev.efekos.arn.resolver.CommandArgumentResolver;
import net.minecraft.commands.CommandDispatcher;

import java.lang.reflect.Parameter;

/**
 * An implementation of {@link CommandArgumentResolver}. Resolves {@link Boolean} and {@code boolean} arguments.
 * @since 0.1
 * @author efekos
 */
public final class CmdBooleanArg implements CommandArgumentResolver {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(CommandArgument.class) && (parameter.getType().equals(boolean.class)||parameter.getType().equals(Boolean.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArgumentBuilder<?,?> apply(Parameter parameter) {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        return CommandDispatcher.a(s.isEmpty() ?parameter.getName():s, BoolArgumentType.bool());
    }
}
