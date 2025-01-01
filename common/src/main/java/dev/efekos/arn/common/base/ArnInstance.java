/*
 * MIT License
 *
 * Copyright (c) 2025 efekos
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

package dev.efekos.arn.common.base;

import dev.efekos.arn.common.Arn;
import dev.efekos.arn.common.ArnFeature;
import dev.efekos.arn.common.exception.ArnException;

import java.util.List;

/**
 * Represents an instance off Arn that can be used to run Arn on the project. {@link Arn#getInstance()} should be used.
 */
public interface ArnInstance {

    /**
     * Scans all classes under the package that {@code mainClass} is.
     * @param mainClass Main class of the project.
     * @param instance An instance of the main class as it might be needed for some Arn implementations.
     * @param <T> Type of the main class.
     * @throws ArnException if any error occurs while running Arn.
     */
    <T> void run(Class<T> mainClass, T instance) throws ArnException;

    /**
     * Adds the given class to an exclusion list. When a class is excluded from Arn, it will not be scanned even if it
     * has {@link dev.efekos.arn.common.annotation.Container} annotation. Can be used to disable specific parts of the
     * project depending on configuration.
     * @param clazz Class to exclude.
     * @return Same instance of Arn.
     */
    ArnInstance excludeClass(Class<?> clazz);

    /**
     * Returns a list of features supported by this Arn implementation. Can be used to enable other systems when a part
     * of the project needs a feature to be supported, but it isn't.
     * @return A list of supported {@link ArnFeature}s.
     */
    List<ArnFeature> getSupportedFeatures();

    /**
     * Checks if feature list of this {@link ArnInstance} contains the given {@link ArnFeature}. If
     * {@link ArnFeature#ALL} is in the feature list, this method will always return {@code true}.
     * @param feature A feature of Arn.
     * @return Whether the given feature is supported or not.
     */
    default boolean doesSupport(ArnFeature feature) {
        return getSupportedFeatures().contains(ArnFeature.ALL) || getSupportedFeatures().contains(feature);
    }

    /**
     * Tries to exclude a class using its full name. If the class is loaded in the current runtime, it will be excluded
     * from Arn.
     * @param className Name of the class.
     * @return Same instance of Arn.
     */
    default ArnInstance excludeClass(String className) {
        try {
            return excludeClass(Class.forName(className));
        } catch (Exception e) {
            return this;
        }
    }

}