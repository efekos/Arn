package dev.efekos.arn.config;

import dev.efekos.arn.resolver.CommandHandlerMethodArgumentResolver;

import java.util.List;

public interface ArnConfigurer {

    void addArgumentResolvers(List<CommandHandlerMethodArgumentResolver> resolvers);

}