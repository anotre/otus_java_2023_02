package ru.otus.exceptions;

public class NoParameterNameException  extends RuntimeException {
    public NoParameterNameException(Exception ex) {
        super(ex);
    }

    public NoParameterNameException(String message) {
        super(message);
    }

    public NoParameterNameException() {
        super();
    }
}
