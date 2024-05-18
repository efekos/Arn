package dev.efekos.arn.annotation.material;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used together with {@link dev.efekos.arn.annotation.CommandArgument} {@link org.bukkit.Material} arguments. Specifies
 * that this {@link org.bukkit.Material} argument must be a {@link org.bukkit.Material} that is a block.
 * @since 0.1
 * @author efekos
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Block {
}
