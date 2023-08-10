package com.example.hellotalk.exception;

public class FollowshipDoesNotExistException extends RuntimeException {

    public FollowshipDoesNotExistException(String message) {
        super(message);
    }
}
