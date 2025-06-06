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

package dev.efekos.arn.common;

import dev.efekos.arn.common.base.ArnInstance;

import javax.annotation.Nullable;
import javax.naming.OperationNotSupportedException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Main class of Arn, a Minecraft command library used to easily register commands.
 */
public class Arn {

    private Arn() {

    }

    /**
     * Checks if Arn is currently available.
     * @return Whether Arn is currently available or not.
     */
    public static boolean isAvailable() {
        try {
            Class.forName("dev.efekos.arn.StaticArnBinder");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Uses dev.efekos.arn.StaticArnBinder class to try to get an Arn instance. This class does not exist in {@code arn-common}
     * artifact and must be created by another class to return an {@link ArnInstance}.
     * @return Created {@link ArnInstance} if Arn is currently available, {@code null} otherwise.
     */
    @Nullable
    public static ArnInstance getInstance() {
        if (!isAvailable()) return null;
        try {
            Class<?> clazz = Class.forName("dev.efekos.arn.StaticArnBinder");
            Method createArnInstance = clazz.getDeclaredMethod("createArnInstance");
            Object o = createArnInstance.invoke(null);
            if (!(o instanceof ArnInstance i))
                throw new RuntimeException("dev.efekos.arn.common.StaticArnBinder#createArnInstance does not return a dev.efekos.arn.common.i.ArnInstance. Please report this to github: https://github.com/efekos/Arn");
            return i;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Could not find StaticArnBinder even though it is guaranteed to exist. Please report this to github: https://github.com/efekos/Arn", e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Could not find the method #createArnInstance at StaticArnBinder. Please report this to github: https://github.com/efekos/Arn", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Could not access #createArnInstance method of StaticArnBinder. Please report this to github: https://github.com/efekos/Arn", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}
