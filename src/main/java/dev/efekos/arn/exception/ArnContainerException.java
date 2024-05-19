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

    /**
     * Creates a new exception.
     */
    public ArnContainerException() {
    }

    /**
     * Creates a new exception.
     * @param message Exception message.
     */
    public ArnContainerException(String message) {
        super(message);
    }

    /**
     * Creates a new exception.
     * @param message Exception message.
     * @param cause Exception cause.
     */
    public ArnContainerException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new exception.
     * @param cause Exception cause.
     */
    public ArnContainerException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new exception.
     * @param message Exception message.
     * @param cause Exception cause.
     * @param enableSuppression Whether suppression should be enabled.
     * @param writableStackTrace Whether stack trace is writeable
     */
    public ArnContainerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
