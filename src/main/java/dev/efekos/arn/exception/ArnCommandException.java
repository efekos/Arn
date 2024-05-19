package dev.efekos.arn.exception;

import dev.efekos.arn.data.CommandHandlerMethod;

/**
 * An exception type thrown by {@link dev.efekos.arn.Arn} when there is something wrong with a
 * {@link CommandHandlerMethod}. It might be with the command itself, or something happening while executing the
 * {@link CommandHandlerMethod#getMethod()} method.
 * @author efekos
 * @since 0.1
 */
public class ArnCommandException extends Exception{

    /**
     * Creates a new exception.
     */
    public ArnCommandException() {
    }

    /**
     * Creates a new exception.
     * @param message Exception message.
     */
    public ArnCommandException(String message) {
        super(message);
    }

    /**
     * Creates a new exception.
     * @param message Exception message.
     * @param cause Exception cause.
     */
    public ArnCommandException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new exception.
     * @param cause Exception cause.
     */
    public ArnCommandException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new exception.
     * @param message Exception message.
     * @param cause Exception cause.
     * @param enableSuppression Whether suppression should be enabled.
     * @param writableStackTrace Whether stack trace is writeable
     */
    public ArnCommandException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
