package dev.efekos.arn.annotation.modifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If this annotation is applied to a {@link org.bukkit.Location} argument, the argument will be a Vec3 argument instead
 * of block pos, allowing players to enter doubles.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Vector {
}
