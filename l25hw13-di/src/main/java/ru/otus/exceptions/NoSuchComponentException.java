package ru.otus.exceptions;

public class NoSuchComponentException extends RuntimeException {
    public NoSuchComponentException(Exception ex) {
        super(ex);
    }

    public NoSuchComponentException(String message) {
        super(message);
    }

    public NoSuchComponentException() {
        super();
    }
}
