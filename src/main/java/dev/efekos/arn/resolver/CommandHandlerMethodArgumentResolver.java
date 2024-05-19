package dev.efekos.arn.resolver;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.efekos.arn.data.CommandHandlerMethod;
import net.minecraft.commands.CommandListenerWrapper;

import java.lang.reflect.Parameter;

/**
 * Represents a resolver that can take a {@link Parameter} from a {@link CommandHandlerMethod}, and figure out what
 * should be passed in to it while invoking the method.
 * @since 0.1
 * @author efekos
 */
public interface CommandHandlerMethodArgumentResolver {


    /**
     * Returns whether this {@link CommandHandlerMethodArgumentResolver} can resolve {@code parameter}. Keep in mind,
     * that there shouldn't be more than one {@link CommandHandlerMethodArgumentResolver} that can resolver the same
     * parameter.
     * @param parameter A parameter of a {@link dev.efekos.arn.data.CommandHandlerMethod}.
     * @return {@code true} if this {@link Parameter} should be resolved using this
     *         {@link CommandHandlerMethodArgumentResolver}, {@code false} otherwise.
     */
    boolean isApplicable(Parameter parameter);

    /**
     * Should return true if there is a {@link CommandArgumentResolver} assigned to this resolver.
     * @return Whether {@link dev.efekos.arn.Arn} should search for a {@link CommandArgumentResolver} when a
     * {@link Parameter} is resolvable by this resolver.
     */
    boolean requireCommandArgument();

    /**
     *
     * @param parameter The {@link Parameter} that was associated with this resolver in the first place.
     * @param method Main {@link CommandHandlerMethod} in case something from there is needed.
     * @param context Command context to get arguments from the executed command.
     * @return An object to be passed in to {@code parameter}.
     * @throws CommandSyntaxException if needed.
     */
    Object resolve(Parameter parameter, CommandHandlerMethod method, CommandContext<CommandListenerWrapper> context) throws CommandSyntaxException;

}