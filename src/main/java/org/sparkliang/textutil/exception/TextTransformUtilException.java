package org.sparkliang.textutil.exception;

/**
 * The common exception in text-util project.
 *
 * @author spark
 * @date 2020-04-07
 * @since 1.0
 */
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
