package dev.efekos.arn.exception;

public class ArnArgumentException extends Exception{

    public ArnArgumentException() {
    }

    public ArnArgumentException(String message) {
        super(message);
    }

    public ArnArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArnArgumentException(Throwable cause) {
        super(cause);
    }

    public ArnArgumentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
