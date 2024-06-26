/*
 * MIT License
 *
 * Copyright (c) 2024 efekos
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.efekos.arn.config;

import dev.efekos.arn.data.ExceptionMap;
import dev.efekos.arn.resolver.CommandArgumentResolver;
import dev.efekos.arn.resolver.CommandHandlerMethodArgumentResolver;

import java.util.List;

/**
 * An interface that represents a configurer of {@link dev.efekos.arn.Arn}. When {@link dev.efekos.arn.Arn#run(Class)}
 * is called, Arn scans for {@link dev.efekos.arn.annotation.Container}s that is a configurer, and applies such
 * configuration from found configuration classes. Implementations must have an empty constructor in order to work.
 *
 * @author efekos
 * @since 0.1
 */
public interface ArnConfigurer {

    /**
     * Adds extra {@link CommandHandlerMethodArgumentResolver}s to the given list.
     *
     * @param resolvers A list.
     */
    void addHandlerMethodArgumentResolvers(List<CommandHandlerMethodArgumentResolver> resolvers);

    /**
     * Adds extra {@link CommandArgumentResolver}s to the given list.
     *
     * @param resolvers A list.
     */
    void addArgumentResolvers(List<CommandArgumentResolver> resolvers);

    /**
     * Adds extra annotation exceptions to the given map.
     *
     * @param map An {@link ExceptionMap}.
     */
    void putArgumentResolverExceptions(ExceptionMap<CommandArgumentResolver> map);


    /**
     * Adds extra annotation exceptions to the given map.
     *
     * @param map An {@link ExceptionMap}.
     */
    void putHandlerMethodArgumentResolverExceptions(ExceptionMap<CommandHandlerMethodArgumentResolver> map);
}