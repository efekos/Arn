package dev.efekos.arn.resolver.impl.handler;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.efekos.arn.annotation.CommandArgument;
import dev.efekos.arn.data.CommandHandlerMethod;
import dev.efekos.arn.resolver.CommandHandlerMethodArgumentResolver;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.GameModeArgument;
import net.minecraft.world.level.EnumGamemode;
import org.bukkit.GameMode;

import java.lang.reflect.Parameter;
import java.util.Locale;

/**
 * An implementation of {@link CommandHandlerMethodArgumentResolver}. Resolves {@link GameMode} arguments.
 * @since 0.1
 * @author efekos
 */
public final class HndGameModeArg implements CommandHandlerMethodArgumentResolver {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(CommandArgument.class) && parameter.getType().equals(GameMode.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GameMode resolve(Parameter parameter, CommandHandlerMethod method, CommandContext<CommandListenerWrapper> context) throws CommandSyntaxException {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        EnumGamemode gamemode = GameModeArgument.a(context, s.isEmpty() ? parameter.getName() : s);
        return GameMode.valueOf(gamemode.c().toUpperCase(Locale.ENGLISH));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean requireCommandArgument() {
        return true;
    }
}
