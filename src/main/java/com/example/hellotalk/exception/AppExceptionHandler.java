package com.example.hellotalk.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

    public static final String USER_NOT_FOUND_EXCEPTION = "NO USER FOUND WITH THIS ID";
    public static final String MOMENT_NOT_FOUND_EXCEPTION = "NO MOMENT FOUND WITH THIS ID";
    public static final String FOLLOWING_RELATIONSHIP_ALREADY_EXISTS_EXCEPTION = "FOLLOWING RELATIONSHIP ALREADY EXISTS";
    public static final String FOLLOWING_RELATIONSHIP_DOES_NOT_EXIST_EXCEPTION = "FOLLOWING RELATIONSHIP DOES NOT EXIST";

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex, WebRequest webRequest) {
        return new ResponseEntity<>(new ApiError(ex.getMessage(), NOT_FOUND, LocalDateTime.now()), NOT_FOUND);
    }

    @ExceptionHandler(FollowingRelationshipNotCreatedException.class)
    @ResponseStatus(NOT_FOUND)
    public ResponseEntity<Object> handleFollowerNotFoundException(FollowingRelationshipNotCreatedException ex, WebRequest webRequest) {
        return new ResponseEntity<>(new ApiError(ex.getMessage(), NOT_FOUND, LocalDateTime.now()), NOT_FOUND);
    }

    @ExceptionHandler(MomentNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ResponseEntity<Object> handleMomentNotFoundException(MomentNotFoundException ex, WebRequest webRequest) {
        return new ResponseEntity<>(new ApiError(ex.getMessage(), NOT_FOUND, LocalDateTime.now()), NOT_FOUND);
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        return new ResponseEntity<>(new ApiError(ex.getMessage(), HttpStatus.NOT_FOUND, LocalDateTime.now()), HttpStatus.NOT_FOUND);
    }
}
