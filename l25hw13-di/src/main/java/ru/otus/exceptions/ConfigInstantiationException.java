package ru.otus.exceptions;

public class ConfigInstantiationException extends RuntimeException {
    public ConfigInstantiationException(Exception ex) {
        super(ex);
    }

    public ConfigInstantiationException(String message) {
        super(message);
    }

    public ConfigInstantiationException() {
        super();
    }
}
