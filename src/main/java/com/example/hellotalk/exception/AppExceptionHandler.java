package com.example.hellotalk.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

    public static String USER_NOT_FOUND_EXCEPTION = "NO USER FOUND WITH THIS ID";

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex, WebRequest webRequest) {
        return new ResponseEntity<>(new ApiError(ex.getMessage(), NOT_FOUND, LocalDateTime.now()), NOT_FOUND);
    }

    @ExceptionHandler(FollowerNotFoundException.class)
    public ResponseEntity<Object> handleFollowerNotFoundException(FollowerNotFoundException ex, WebRequest webRequest) {
        return new ResponseEntity<>(new ApiError(ex.getMessage(), NOT_FOUND, LocalDateTime.now()), NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        return new ResponseEntity<>(new ApiError(ex.getMessage(), HttpStatus.NOT_FOUND, LocalDateTime.now()), HttpStatus.NOT_FOUND);
    }
}
