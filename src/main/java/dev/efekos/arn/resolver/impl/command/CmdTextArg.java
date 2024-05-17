package dev.efekos.arn.resolver.impl.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import dev.efekos.arn.annotation.CommandArgument;
import dev.efekos.arn.resolver.CommandArgumentResolver;
import net.md_5.bungee.api.chat.BaseComponent;
import net.minecraft.commands.CommandDispatcher;
import net.minecraft.commands.arguments.ArgumentChatComponent;

import java.lang.reflect.Parameter;

public class CmdTextArg implements CommandArgumentResolver {

    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(CommandArgument.class) && parameter.getType().equals(BaseComponent.class);
    }

    @Override
    public ArgumentBuilder apply(Parameter parameter) {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        return CommandDispatcher.a(s.isEmpty()?parameter.getName():s, ArgumentChatComponent.a());
    }

}
