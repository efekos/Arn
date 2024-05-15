package dev.efekos.arn.exception;

public class ArnCommandException extends Exception{
    public ArnCommandException() {
    }

    public ArnCommandException(String message) {
        super(message);
    }

    public ArnCommandException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArnCommandException(Throwable cause) {
        super(cause);
    }

    public ArnCommandException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
