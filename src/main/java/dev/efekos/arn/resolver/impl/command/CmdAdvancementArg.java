package dev.efekos.arn.resolver.impl.command;


import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.efekos.arn.annotation.CommandArgument;
import dev.efekos.arn.resolver.CommandArgumentResolver;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.commands.CommandDispatcher;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.commands.arguments.ArgumentMinecraftKeyRegistered;
import org.bukkit.advancement.Advancement;

import java.lang.reflect.Parameter;
import java.util.Collection;

/**
 * An implementation of {@link CommandArgumentResolver}. Resolves {@link org.bukkit.advancement.Advancement} arguments.
 * @since 0.1
 * @author efekos
 */
public final class CmdAdvancementArg implements CommandArgumentResolver {

    /** {@inheritDoc} */
    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(CommandArgument.class) && parameter.getType().equals(Advancement.class);
    }

    /***/
    private static final SuggestionProvider<CommandListenerWrapper> c = (var0, var1) -> {
        Collection<AdvancementHolder> var2 = var0.getSource().l().aB().b();
        return ICompletionProvider.a(var2.stream().map(AdvancementHolder::a), var1);
    };

    /** {@inheritDoc} */
    @Override
    public ArgumentBuilder apply(Parameter parameter) {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        return CommandDispatcher.a(s.isEmpty()?parameter.getName():s, ArgumentMinecraftKeyRegistered.a()).suggests(c);
    }

}
