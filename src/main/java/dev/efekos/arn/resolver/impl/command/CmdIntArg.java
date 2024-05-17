package dev.efekos.arn.resolver.impl.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import dev.efekos.arn.annotation.CommandArgument;
import dev.efekos.arn.annotation.NumberLimitations;
import dev.efekos.arn.resolver.CommandArgumentResolver;
import net.minecraft.commands.CommandDispatcher;

import java.lang.reflect.Parameter;

public class CmdIntArg implements CommandArgumentResolver {

    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.isAnnotationPresent(CommandArgument.class) && (parameter.getType().equals(int.class)||parameter.getType().equals(Integer.class));
    }

    @Override
    public ArgumentBuilder apply(Parameter parameter) {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        NumberLimitations limitations = parameter.getAnnotation(NumberLimitations.class);
        boolean b = limitations != null;
        return CommandDispatcher.a(s.isEmpty() ?parameter.getName():s, b? IntegerArgumentType.integer((int)limitations.min(),(int)limitations.max()):IntegerArgumentType.integer());
    }
}
