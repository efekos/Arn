package dev.efekos.arn.annotation.modifier;

import dev.efekos.arn.annotation.CommandArgument;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Applies limitations to number arguments annotated with {@link CommandArgument}. When this annotation is present on any
 * number argument ({@code long},{@code int},{@code float} and {@code double}), given minimum and maximum limits will be
 * applied to the command argument node which will be on final command structure.
 * @since 0.1
 * @author efekos
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface NumberLimitations {

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
