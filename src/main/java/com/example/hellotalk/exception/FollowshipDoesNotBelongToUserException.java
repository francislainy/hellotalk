package com.example.hellotalk.exception;

public class FollowshipDoesNotBelongToUserException extends RuntimeException {

    public FollowshipDoesNotBelongToUserException(String message) {
        super(message);
    }
}
