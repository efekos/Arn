package dev.efekos.arn.resolver.impl.command;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import dev.efekos.arn.annotation.CommandArgument;
import dev.efekos.arn.annotation.NumberLimitations;
import dev.efekos.arn.resolver.CommandArgumentResolver;
import net.minecraft.commands.CommandDispatcher;

import java.lang.reflect.Parameter;

public class CmdLongArg implements CommandArgumentResolver {
    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(CommandArgument.class) && (parameter.getType().equals(long.class)||parameter.getType().equals(Long.class));
    }

    @Override
    public ArgumentBuilder apply(Parameter parameter) {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        NumberLimitations limitations = parameter.getAnnotation(NumberLimitations.class);
        boolean b = limitations != null;
        return CommandDispatcher.a(s.isEmpty() ?parameter.getName():s, b? LongArgumentType.longArg(limitations.min(),limitations.max()):LongArgumentType.longArg());
    }
}
