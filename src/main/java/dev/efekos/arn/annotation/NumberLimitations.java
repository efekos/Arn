package dev.efekos.arn.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface NumberLimitations {
    long min() default Integer.MIN_VALUE;
    long max() default Integer.MAX_VALUE;
}
