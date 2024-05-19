package dev.efekos.arn.resolver.impl.handler;


import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.efekos.arn.annotation.CommandArgument;
import dev.efekos.arn.data.CommandHandlerMethod;
import dev.efekos.arn.resolver.CommandHandlerMethodArgumentResolver;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentMinecraftKeyRegistered;
import org.bukkit.advancement.Advancement;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_20_R3.advancement.CraftAdvancement;

import java.lang.reflect.Parameter;

/**
 * An implementation of {@link CommandHandlerMethodArgumentResolver}. Resolves {@link BlockData} arguments.
 * @since 0.1
 * @author efekos
 */
public final class HndAdvancementArg implements CommandHandlerMethodArgumentResolver {

    /** {@inheritDoc} */
    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(CommandArgument.class) && parameter.getType().equals(Advancement.class);
    }

    /** {@inheritDoc} */
    @Override
    public boolean requireCommandArgument() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public Advancement resolve(Parameter parameter, CommandHandlerMethod method, CommandContext<CommandListenerWrapper> context) throws CommandSyntaxException {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        AdvancementHolder advancement = ArgumentMinecraftKeyRegistered.a(context, s.isEmpty() ? parameter.getName() : s);
        return new CraftAdvancement(advancement);
    }

}
