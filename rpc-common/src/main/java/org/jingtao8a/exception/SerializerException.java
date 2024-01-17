package org.jingtao8a.exception;

public class SerializerException extends RuntimeException {
    public SerializerException(String message) {
        super(message);
    }
    public SerializerException(String message, String detail) {
        super(message + ":" + detail);
    }
}
