package ru.interview.datavalidator.exception;

public class FieldNotFoundException extends FileParsingException {
    public FieldNotFoundException(String message) {
        super(message);
    }
}
