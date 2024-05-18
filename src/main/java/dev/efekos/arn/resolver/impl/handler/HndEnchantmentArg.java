package dev.efekos.arn.resolver.impl.handler;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.efekos.arn.annotation.CommandArgument;
import dev.efekos.arn.data.CommandHandlerMethod;
import dev.efekos.arn.resolver.CommandHandlerMethodArgumentResolver;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

import java.lang.reflect.Parameter;

public class HndEnchantmentArg implements CommandHandlerMethodArgumentResolver {

    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(CommandArgument.class)  && parameter.getType().equals(Enchantment.class);
    }

    @Override
    public Object resolve(Parameter parameter, CommandHandlerMethod method, CommandContext<CommandListenerWrapper> context) throws CommandSyntaxException {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        Holder.c<net.minecraft.world.item.enchantment.Enchantment> enchantmentc = ResourceArgument.g(context, s.isEmpty() ? parameter.getName() : s);

        MinecraftKey key = BuiltInRegistries.f.b(enchantmentc.a());
        NamespacedKey effectKey = new NamespacedKey(key.b(), key.a());
        return Enchantment.getByKey(effectKey);
    }


    @Override
    public boolean requireCommandArgument() {
        return true;
    }
}
