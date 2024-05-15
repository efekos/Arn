package dev.efekos.arn.annotation;

public @interface RestCommand {
    String value() default "";
    String description() default "";
    String permission() default "";
    String[] aliases() default {};
    boolean allowConsole() default false;
}
