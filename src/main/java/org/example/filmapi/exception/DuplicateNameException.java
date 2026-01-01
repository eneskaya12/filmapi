package org.example.filmapi.exception;

public class DuplicateNameException extends RuntimeException{
    public DuplicateNameException(String message) {
        super(message);
    }
}
