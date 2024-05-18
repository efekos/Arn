package dev.efekos.arn.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Makes applied type able to be scanned by {@link dev.efekos.arn.Arn} while scanning for command handler methods,
 * configurers etc. Every class annotated with this annotation must have an empty constructor.
 * @since 0.1
 * @author efekos
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Container {
}
