package com.example.hellotalk.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

    public static final String USER_NOT_FOUND_EXCEPTION = "NO USER FOUND WITH THIS ID";
    public static final String ENTITY_DOES_NOT_BELONG_TO_USER_EXCEPTION = "ENTITY DOES NOT BELONG TO USER EXCEPTION";
    public static final String ENTITY_BELONGS_TO_USER_EXCEPTION = "ENTITY BELONGS TO USER EXCEPTION";
    public static final String MOMENT_NOT_FOUND_EXCEPTION = "NO MOMENT FOUND WITH THIS ID";
    public static final String COMMENT_NOT_FOUND_EXCEPTION = "NO COMMENT FOUND WITH THIS ID";
    public static final String MOMENT_ALREADY_LIKED_EXCEPTION = "MOMENT ALREADY LIKED";
    public static final String FOLLOWING_RELATIONSHIP_ALREADY_EXISTS_EXCEPTION = "FOLLOWING RELATIONSHIP DELETED AS IT ALREADY EXISTED";
    public static final String USER_TO_AND_FROM_CANT_BE_THE_SAME = "USER TRYING TO FOLLOW THEMSELF. USER TO AND FROM CAN'T BE THE SAME";
    public static final String FOLLOWING_RELATIONSHIP_DOES_NOT_EXIST_EXCEPTION = "FOLLOWING RELATIONSHIP DOES NOT EXIST";

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex, WebRequest webRequest) {
        return new ResponseEntity<>(new ApiError(ex.getMessage(), NOT_FOUND, LocalDateTime.now()), NOT_FOUND);
    }

    @ExceptionHandler(MomentNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ResponseEntity<Object> handleMomentNotFoundException(MomentNotFoundException ex, WebRequest webRequest) {
        return new ResponseEntity<>(new ApiError(ex.getMessage(), NOT_FOUND, LocalDateTime.now()), NOT_FOUND);
    }

    @ExceptionHandler(MomentAlreadyLikedException.class)
    @ResponseStatus(CONFLICT)
    public ResponseEntity<Object> handleMomentAlreadyLikedException(MomentAlreadyLikedException ex, WebRequest webRequest) {
        return new ResponseEntity<>(new ApiError(ex.getMessage(), CONFLICT, LocalDateTime.now()), CONFLICT);
    }

    @ExceptionHandler(CommentNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ResponseEntity<Object> handleCommentNotFoundException(CommentNotFoundException ex, WebRequest webRequest) {
        return new ResponseEntity<>(new ApiError(ex.getMessage(), NOT_FOUND, LocalDateTime.now()), NOT_FOUND);
    }

    @ExceptionHandler(EntityDoesNotBelongToUserException.class)
    @ResponseStatus(FORBIDDEN)
    public ResponseEntity<Object> handleEntityDoesNotBelongToUserException(EntityDoesNotBelongToUserException ex, WebRequest webRequest) {
        return new ResponseEntity<>(new ApiError(ex.getMessage(), FORBIDDEN, LocalDateTime.now()), FORBIDDEN);
    }

    @ExceptionHandler(EntityBelongsToUserException.class)
    @ResponseStatus(FORBIDDEN)
    public ResponseEntity<Object> handleEntityBelongsToUserException(EntityBelongsToUserException ex, WebRequest webRequest) {
        return new ResponseEntity<>(new ApiError(ex.getMessage(), FORBIDDEN, LocalDateTime.now()), FORBIDDEN);
    }

    @ExceptionHandler(FollowingRelationshipDeletedException.class)
    @ResponseStatus(PARTIAL_CONTENT)
    public ResponseEntity<Object> handleFollowingRelationshipDeletedException(FollowingRelationshipDeletedException ex, WebRequest webRequest) {
        return new ResponseEntity<>(new ApiError(ex.getMessage(), PARTIAL_CONTENT, LocalDateTime.now()), PARTIAL_CONTENT);
    }

    @ExceptionHandler(FollowingRelationshipNotCreatedException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<Object> handleFollowingRelationshipNotCreatedException(FollowingRelationshipNotCreatedException ex, WebRequest webRequest) {
        return new ResponseEntity<>(new ApiError(ex.getMessage(), BAD_REQUEST, LocalDateTime.now()), BAD_REQUEST);
    }

}
