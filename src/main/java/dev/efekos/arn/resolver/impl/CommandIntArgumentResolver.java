package dev.efekos.arn.resolver.impl;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import dev.efekos.arn.annotation.CommandArgument;
import dev.efekos.arn.resolver.CommandArgumentResolver;
import net.minecraft.commands.CommandDispatcher;

import java.lang.reflect.Parameter;

public class CommandIntArgumentResolver implements CommandArgumentResolver {

    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(CommandArgument.class) && (parameter.getType().equals(int.class)||parameter.getType().equals(Integer.class));
    }

    @Override
    public void apply(ArgumentBuilder dispatcher, Parameter parameter) {
        dispatcher = dispatcher.then(CommandDispatcher.a(parameter.getName(), IntegerArgumentType.integer()));
    }
}
