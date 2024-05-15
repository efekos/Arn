package dev.efekos.arn.resolver;

import com.mojang.brigadier.builder.ArgumentBuilder;

import java.lang.reflect.Parameter;

public interface CommandArgumentResolver {

    boolean isApplicable(Parameter parameter);

    void apply(ArgumentBuilder dispatcher, Parameter parameter);

}
