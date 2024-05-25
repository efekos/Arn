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

package dev.efekos.arn.data;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a {@code Map<Class<? extends T>, List<Class<? extends Annotation>>>}, but provides only two necessary
 * methods to manage the map.
 * @param <T> Key type of the map.
 * @author efekos
 * @since 0.3
 */
public class ExceptionMap<T> {

    /**
     * Actual map that is represented by this {@link ExceptionMap}.
     */
    private final Map<Class<? extends T>, List<Class<? extends Annotation>>> actualMap = new HashMap<>();

    /**
     * Adds an annotation to the exception list of the class given.
     * @param clazz A class.
     * @param annotation An annotation class.
     */
    public void put(Class<? extends T> clazz, Class<? extends Annotation> annotation) {
        List<Class<? extends Annotation>> list = actualMap.getOrDefault(clazz, new ArrayList<>());
        if(list.contains(annotation))return;
        list.add(annotation);
        actualMap.put(clazz, list);
    }

    /**
     * Returns the exception list of the class given.
     * @param clazz A class.
     * @return A list of annotation classes.
     */
    public List<Class<? extends Annotation>> get(Class<? extends T> clazz) {
        return actualMap.getOrDefault(clazz,new ArrayList<>());
    }

}
