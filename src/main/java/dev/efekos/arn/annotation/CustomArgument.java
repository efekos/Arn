package dev.efekos.arn.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Any class that is an {@link Enum} cen be annotated with this annotation, then be used as a command argument.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CustomArgument {

    /**
     * Returns name of this "registry". Must be a valid {@link org.bukkit.NamespacedKey}.
     * @return Value of this annotation.
     */
    String value();

}
