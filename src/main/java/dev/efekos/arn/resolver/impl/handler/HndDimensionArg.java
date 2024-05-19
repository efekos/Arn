package dev.efekos.arn.resolver.impl.handler;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.efekos.arn.annotation.CommandArgument;
import dev.efekos.arn.data.CommandHandlerMethod;
import dev.efekos.arn.resolver.CommandHandlerMethodArgumentResolver;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentDimension;
import net.minecraft.server.level.WorldServer;
import org.bukkit.World;

import java.lang.reflect.Parameter;

/**
 * An implementation of {@link CommandHandlerMethodArgumentResolver}. Resolves {@link } arguments.
 * <strong>Since this implementation uses {@link ArgumentDimension}, which was only made for to dimensions of one world,
 * there isn't any conclusion that this resolver will work with different worlds, such as ones made by Multiverse plugin.
 * </strong>
 * @since 0.1
 * @author efekos
 */
public class HndDimensionArg implements CommandHandlerMethodArgumentResolver {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(CommandArgument.class) && (parameter.getType().equals(World.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public World resolve(Parameter parameter, CommandHandlerMethod method, CommandContext<CommandListenerWrapper> context) throws CommandSyntaxException {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        WorldServer world = ArgumentDimension.a(context, s.isEmpty() ? parameter.getName() : s);
        return world.getWorld();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean requireCommandArgument() {
        return true;
    }
}
