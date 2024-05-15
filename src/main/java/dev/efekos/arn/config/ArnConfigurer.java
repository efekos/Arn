package dev.efekos.arn.config;

import dev.efekos.arn.resolver.CommandArgumentResolver;

import java.util.List;

public interface ArnConfigurer {

    void addArgumentResolvers(List<CommandArgumentResolver> resolvers);

}