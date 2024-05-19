package dev.efekos.arn.exception;

import dev.efekos.arn.annotation.Container;

/**
 * An exception thrown when a {@link Container} can't be instantiated by {@link dev.efekos.arn.Arn}. This happens when
 * the type annotated with {@link Container} doesn't have a constructor with no parameters. There is no need to construct
 * a {@link Container} yourself, so neither is there a proper reason to make a constructor, especially with arguments.
 * @author efekos
 * @since 0.1
 */
public class ArnContainerException extends Exception{
    public ArnContainerException() {
    }

    public ArnContainerException(String message) {
        super(message);
    }

    public ArnContainerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArnContainerException(Throwable cause) {
        super(cause);
    }

    public ArnContainerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
