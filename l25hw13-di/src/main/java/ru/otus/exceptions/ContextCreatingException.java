package ru.otus.exceptions;

public class ContextCreatingException extends RuntimeException {
    public ContextCreatingException(Exception ex) {
        super(ex);
    }

    public ContextCreatingException(String message) {
        super(message);
    }

    public ContextCreatingException() {
        super();
    }
}
