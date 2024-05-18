package dev.efekos.arn.config;

import dev.efekos.arn.resolver.CommandArgumentResolver;
import dev.efekos.arn.resolver.CommandHandlerMethodArgumentResolver;

import java.util.List;

/**
 * An interface that represents a configurer of {@link dev.efekos.arn.Arn}. When {@link dev.efekos.arn.Arn#run(Class)}
 * is called, Arn scans for {@link dev.efekos.arn.annotation.Container}s that is a configurer, and applies such
 * configuration from found configuration classes. Implementations must have an empty constructor in order to work.
 * @author efekos
 * @since 0.1
 */
public interface ArnConfigurer {

    void addHandlerMethodArgumentResolvers(List<CommandHandlerMethodArgumentResolver> resolvers);

    void addArgumentResolvers(List<CommandArgumentResolver> resolvers);

}