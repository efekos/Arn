package dev.efekos.arn.resolver.impl.handler;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.efekos.arn.annotation.CommandArgument;
import dev.efekos.arn.annotation.material.Item;
import dev.efekos.arn.data.CommandHandlerMethod;
import dev.efekos.arn.resolver.CommandHandlerMethodArgumentResolver;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.MinecraftKey;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import java.lang.reflect.Parameter;
import java.util.Arrays;

/**
 * An implementation of {@link CommandHandlerMethodArgumentResolver}. Resolves {@link Material} arguments that is an
 * {@link Item}.
 * @since 0.1
 * @author efekos
 */
public final class HndItemArg implements CommandHandlerMethodArgumentResolver {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(CommandArgument.class) && parameter.getType().equals(Material.class) && parameter.isAnnotationPresent(Item.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Material resolve(Parameter parameter, CommandHandlerMethod method, CommandContext<CommandListenerWrapper> context) throws CommandSyntaxException {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        Holder.c<net.minecraft.world.item.Item> itemc = ResourceArgument.a(context, s.isEmpty() ? parameter.getName() : s, Registries.F);
        net.minecraft.world.item.Item item = itemc.a();
        MinecraftKey key = BuiltInRegistries.h.b(item);
        return Arrays.stream(Material.values()).filter(material -> material.getKey().equals(new NamespacedKey(key.b(),key.a()))).findFirst().orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean requireCommandArgument() {
        return true;
    }
}
