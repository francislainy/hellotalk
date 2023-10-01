package com.example.hellotalk.exception;

public class FollowshipAlreadyExistsException extends RuntimeException {

    public FollowshipAlreadyExistsException(String message) {
        super(message);
    }
}
