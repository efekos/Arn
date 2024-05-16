package dev.efekos.arn.resolver.impl;

import com.mojang.brigadier.context.CommandContext;
import dev.efekos.arn.annotation.CommandArgument;
import dev.efekos.arn.handler.CommandHandlerMethod;
import dev.efekos.arn.resolver.CommandHandlerMethodArgumentResolver;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentChatComponent;
import net.minecraft.network.chat.IChatBaseComponent;

import java.lang.reflect.Parameter;

public class HndTextArg implements CommandHandlerMethodArgumentResolver {

    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(CommandArgument.class) && parameter.getType().equals(BaseComponent.class);
    }

    @Override
    public BaseComponent resolve(Parameter parameter, CommandHandlerMethod method, CommandContext<CommandListenerWrapper> context) {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        IChatBaseComponent component = ArgumentChatComponent.a(context, s.isEmpty() ? parameter.getName() : s);
        String json = IChatBaseComponent.ChatSerializer.a(component);
        return ComponentSerializer.deserialize(json);
    }
}
