package dev.efekos.arn.exception;

public class ArnConfigurerException extends Exception{

    public ArnConfigurerException() {
    }

    public ArnConfigurerException(String message) {
        super(message);
    }

    public ArnConfigurerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArnConfigurerException(Throwable cause) {
        super(cause);
    }

    public ArnConfigurerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}