package dev.efekos.arn.config;

import dev.efekos.arn.resolver.CommandArgumentResolver;
import dev.efekos.arn.resolver.CommandHandlerMethodArgumentResolver;

import java.util.List;

public interface ArnConfigurer {

    void addHandlerMethodArgumentResolvers(List<CommandHandlerMethodArgumentResolver> resolvers);

    void addArgumentResolvers(List<CommandArgumentResolver> resolvers);

}