package dev.efekos.arn.annotation;

public @interface Command {
    String name() default "";
    String description() default "";
    String permission() default "";
    String[] aliases() default {};
    boolean allowConsole() default false;
}
