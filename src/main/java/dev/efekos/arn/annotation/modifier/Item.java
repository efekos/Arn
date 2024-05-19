package dev.efekos.arn.annotation.modifier;


import dev.efekos.arn.annotation.CommandArgument;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Used together with {@link dev.efekos.arn.annotation.CommandArgument} {@link org.bukkit.Material} arguments. Specifies
 * that this {@link org.bukkit.Material} argument must be a {@link org.bukkit.Material} that is an item. Most blocks are
 * also an item, so this won't change mush.
 * @since 0.1
 * @author efekos
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Item {
    /**
     * Applies limitations to number arguments annotated with {@link CommandArgument}. When this annotation is present on any
     * number argument ({@code long},{@code int},{@code float} and {@code double}), given minimum and maximum limits will be
     * applied to the command argument node which will be on final command structure.
     * @since 0.1
     * @author efekos
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @interface NumberLimitations {

        /**
         * Minimum value of the number argument.
         * @return Minimum value.
         */
        long min() default Integer.MIN_VALUE;

        /**
         * Maximum value of the number argument.
         * @return Maximum value.
         */
        long max() default Integer.MAX_VALUE;
    }
}
