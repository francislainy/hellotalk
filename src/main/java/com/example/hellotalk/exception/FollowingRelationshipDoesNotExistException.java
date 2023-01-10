package com.example.hellotalk.exception;

public class FollowingRelationshipDoesNotExistException extends RuntimeException {

    public FollowingRelationshipDoesNotExistException(String message) {
        super(message);
    }
}
