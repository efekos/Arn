package dev.efekos.arn.exception;

/**
 * An exception type thrown by {@link dev.efekos.arn.Arn} if there is something wrong with
 * {@link dev.efekos.arn.annotation.Container}s annotated with {@link dev.efekos.arn.annotation.CustomArgument}.
 * @since 0.1
 * @author efekos
 */
public class ArnArgumentException extends Exception {

    /**
     * Creates a new exception.
     */
    public ArnArgumentException() {
    }

    /**
     * Creates a new exception.
     *
     * @param message Exception message.
     */
    public ArnArgumentException(String message) {
        super(message);
    }

    /**
     * Creates a new exception.
     *
     * @param message Exception message.
     * @param cause   Exception cause.
     */
    public ArnArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new exception.
     *
     * @param cause Exception cause.
     */
    public ArnArgumentException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new exception.
     *
     * @param message            Exception message.
     * @param cause              Exception cause.
     * @param enableSuppression  Whether suppression should be enabled.
     * @param writableStackTrace Whether stack trace is writeable
     */
    public ArnArgumentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
