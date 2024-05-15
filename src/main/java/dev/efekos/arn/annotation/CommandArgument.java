package dev.efekos.arn.annotation;

public @interface CommandArgument {
    String value() default "";
    boolean required() default true;
}