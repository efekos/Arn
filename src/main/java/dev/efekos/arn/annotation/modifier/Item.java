package dev.efekos.arn.annotation.modifier;


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

}
