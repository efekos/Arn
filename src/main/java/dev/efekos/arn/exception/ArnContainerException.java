package dev.efekos.arn.exception;

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
