package dev.efekos.arn.resolver.impl.handler;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.efekos.arn.annotation.CommandArgument;
import dev.efekos.arn.annotation.material.Block;
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
 * An implementation of {@link CommandHandlerMethodArgumentResolver}. Resolves {@link Material} arguments that is a {@link Block}.
 * @since 0.1
 * @author efekos
 */
public class HndBlockArg implements CommandHandlerMethodArgumentResolver {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(CommandArgument.class) && parameter.getType().equals(Material.class) && parameter.isAnnotationPresent(Block.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object resolve(Parameter parameter, CommandHandlerMethod method, CommandContext<CommandListenerWrapper> context) throws CommandSyntaxException {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        Holder.c<net.minecraft.world.level.block.Block> blockc = ResourceArgument.a(context, s.isEmpty() ? parameter.getName() : s, Registries.f);
        MinecraftKey key = BuiltInRegistries.e.b(blockc.a());
        NamespacedKey blockKey = new NamespacedKey(key.b(), key.a());
        return Arrays.stream(Material.values()).filter(material -> material.getKey().equals(blockKey)).findFirst().orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean requireCommandArgument() {
        return true;
    }
}
