package com.example.hellotalk.exception;

public class FollowerNotFoundException extends RuntimeException {

    public FollowerNotFoundException(String message) {
        super(message);
    }
}
