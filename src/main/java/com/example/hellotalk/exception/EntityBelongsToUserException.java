package com.example.hellotalk.exception;

public class EntityBelongsToUserException extends RuntimeException {

    public EntityBelongsToUserException(String message) {
        super(message);
    }
}
