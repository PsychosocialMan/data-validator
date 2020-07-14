package ru.interview.datavalidator.exception;

public class UnknownFieldException extends FileParsingException {
    public UnknownFieldException(String message) {
        super(message);
    }
}
