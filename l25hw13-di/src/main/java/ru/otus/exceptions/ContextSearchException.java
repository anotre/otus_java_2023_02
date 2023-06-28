package ru.otus.exceptions;

public class ContextSearchException extends RuntimeException {
    public ContextSearchException(Exception ex) {
        super(ex);
    }

    public ContextSearchException(String message) {
        super(message);
    }

    public ContextSearchException() {
        super();
    }
}
