package com.example.hellotalk.exception;

public class EntityDoesNotBelongToUserException extends RuntimeException {

    public EntityDoesNotBelongToUserException(String message) {
        super(message);
    }
}
