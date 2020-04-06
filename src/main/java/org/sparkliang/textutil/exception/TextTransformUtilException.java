package org.sparkliang.textutil.exception;

public class TextTransformUtilException extends RuntimeException {
    public TextTransformUtilException() {
        super();
    }


    public TextTransformUtilException(String message) {
        super(message);
    }


    public TextTransformUtilException(String message, Throwable cause) {
        super(message, cause);
    }


    public TextTransformUtilException(Throwable cause) {
        super(cause);
    }


    protected TextTransformUtilException(String message, Throwable cause,
                                         boolean enableSuppression,
                                         boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
