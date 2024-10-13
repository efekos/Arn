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

package dev.efekos.arn.common;

import dev.efekos.arn.common.annotation.Container;
import dev.efekos.arn.common.data.ExceptionMap;

import java.util.List;

/**
 * An interface that represents a configurer of Arn. When ran, Arn scans for {@link Container}s that is a configurer
 * and applies such configuration from found configuration classes. Implementations must have an empty constructor in
 * order to work.
 *
 * @author efekos
 * @since 0.1
 */
public interface BaseArnConfigurer<Cmd, Hnd> {

    /**
     * Adds extra {@link Hnd}s to the given list.
     *
     * @param resolvers A list.
     */
    void addHandlerMethodArgumentResolvers(List<Hnd> resolvers);

    /**
     * Adds extra {@link Cmd}s to the given list.
     *
     * @param resolvers A list.
     */
    void addArgumentResolvers(List<Cmd> resolvers);

    /**
     * Adds extra annotation exceptions to the given map.
     *
     * @param map An {@link ExceptionMap}.
     */
    void putArgumentResolverExceptions(ExceptionMap<Cmd> map);


    /**
     * Adds extra annotation exceptions to the given map.
     *
     * @param map An {@link ExceptionMap}.
     */
    void putHandlerMethodArgumentResolverExceptions(ExceptionMap<Hnd> map);
}