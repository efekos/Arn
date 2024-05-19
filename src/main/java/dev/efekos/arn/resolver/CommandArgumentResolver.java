package dev.efekos.arn.resolver;

import com.mojang.brigadier.builder.ArgumentBuilder;

import java.lang.reflect.Parameter;


/**
 * Represents a resolver that can create an {@link ArgumentBuilder} from a {@link Parameter} of a
 * {@link dev.efekos.arn.data.CommandHandlerMethod}. Unlike {@link CommandHandlerMethodArgumentResolver}s, there can be
 * {@link Parameter}s that doesn't have a CommandArgumentResolver. If
 * {@link CommandHandlerMethodArgumentResolver#requireCommandArgument()} returns {@code false} for a parameter,
 * {@link dev.efekos.arn.Arn} won't search for a CommandArgumentResolver for that parameter.
 * @author efekos
 * @since 0.1
 */
public interface CommandArgumentResolver {

    /**
     * Returns whether this {@link CommandArgumentResolver} can resolve {@code parameter}. Keep in mind that there
     * shouldn't be more than one {@link CommandArgumentResolver} that can resolver the same parameter.
     * @param parameter A parameter of a {@link dev.efekos.arn.data.CommandHandlerMethod}.
     * @return {@code true} if this {@link Parameter} should be resolved using this {@link CommandArgumentResolver},
     *         {@code false} otherwise.
     */
    boolean isApplicable(Parameter parameter);

    /**
     * Creates a {@link ArgumentBuilder} that will represent {@code parameter} in the command structure.
     * @param parameter A parameter of a {@link dev.efekos.arn.data.CommandHandlerMethod}.
     * @return An {@link ArgumentBuilder} that represents {@code parameter}.
     */
    ArgumentBuilder apply(Parameter parameter);

}
