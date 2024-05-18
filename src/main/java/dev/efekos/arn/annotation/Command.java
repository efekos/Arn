package dev.efekos.arn.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies a method to be a command handler method. When this annotation is present, the associated method will be
 * scanned by {@link dev.efekos.arn.Arn}, and be treated as a command method. {@link dev.efekos.arn.Arn} will call this
 * method on command execution.
 * @since 0.1
 * @author efekos
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command {
    /**
     * Literals of this command. There are a few rules about this value:
     * <ul>
     *     <li>{@link dev.efekos.arn.data.CommandAnnotationLiteral#SEPARATOR_CHAR} can be used to separate multiple
     *     literals.</li>
     *     <li>{@code [ba]:\d:[a-z]} syntax can be used to offset literals. b stands for 'before', a stands for 'after'. Then a digit
     *     must be present to determine which argument the literal will be offset to, followed by the actual literal.</li>
     * </ul>
     * These rules can be used to create different kinds of literal placements. For example: {@code "foo.bar.a:0:faz"}.
     * This will place first two literals, {@code foo} and {@code bar} to the start of the command. Then, {@code faz}
     * will be placed <strong>a</strong>fter 0th argument. The final structure of a command with this literal placements
     * will be {@code /foo bar <arg0> faz}. Other examples are: {@code "health.a:0:set" -> /health <arg0> set},
     * {@code "item.a:0:drop.a:0:all" -> /item <arg0> drop all}, {@code "exp.a:0:add.a:1:levels" -> /exp <arg0> add <arg1> level }.
     * @return Literals of this command as a {@link String}.
     */
    String value();

    /**
     * A short description about this command, that can be used for generate help commands later. If a description isn't
     * present, {@link dev.efekos.arn.Arn} will default it to "No description provided.".
     * @return Description of this command.
     */
    String description() default "";

    /**
     * Permission needed by players to execute this command. If not present, a permission won't be required for this
     * command.
     * @return Permission required to use this command.
     */
    String permission() default "";
}
