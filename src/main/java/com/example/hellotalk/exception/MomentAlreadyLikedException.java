package com.example.hellotalk.exception;

public class MomentAlreadyLikedException extends RuntimeException {

    public MomentAlreadyLikedException(String message) {
        super(message);
    }
}
