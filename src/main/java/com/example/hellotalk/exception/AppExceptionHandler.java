package com.example.hellotalk.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

    public static final String USER_NOT_FOUND_EXCEPTION = "NO USER FOUND WITH THIS ID";
    public static final String ENTITY_DOES_NOT_BELONG_TO_USER_EXCEPTION = "ENTITY DOES NOT BELONG TO USER EXCEPTION";
    public static final String MOMENT_NOT_FOUND_EXCEPTION = "NO MOMENT FOUND WITH THIS ID";
    public static final String MOMENT_ALREADY_LIKED_EXCEPTION = "MOMENT ALREADY LIKED";
    public static final String MOMENT_NOT_YET_LIKED_EXCEPTION = "MOMENT NOT YET LIKED";
    public static final String COMMENT_NOT_FOUND_EXCEPTION = "NO COMMENT FOUND WITH THIS ID";
    public static final String MESSAGE_NOT_FOUND_EXCEPTION = "NO MESSAGE FOUND WITH THIS ID";
    public static final String FOLLOWSHIP_ALREADY_EXISTS_EXCEPTION = "FOLLOWSHIP ALREADY EXISTS";
    public static final String FOLLOWSHIP_NOT_CREATED_USER_CANT_FOLLOW_THEMSELF = "USER TRYING TO FOLLOW THEMSELVES. SENDER AND RECEIVER USERS CAN'T BE THE SAME";
    public static final String FOLLOWSHIP_DOES_NOT_EXIST_EXCEPTION = "FOLLOWSHIP DOES NOT EXIST";
    public static final String FOLLOWSHIP_DOES_NOT_BELONG_TO_USER_EXCEPTION = "FOLLOWSHIP DOES NOT BELONG TO USER EXCEPTION";

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex) {
        return new ResponseEntity<>(new ApiError(ex.getMessage(), NOT_FOUND, LocalDateTime.now()), NOT_FOUND);
    }

    @ExceptionHandler(MomentNotFoundException.class)
    public ResponseEntity<Object> handleMomentNotFoundException(MomentNotFoundException ex) {
        return new ResponseEntity<>(new ApiError(ex.getMessage(), NOT_FOUND, LocalDateTime.now()), NOT_FOUND);
    }

    @ExceptionHandler(MomentAlreadyLikedException.class)
    public ResponseEntity<Object> handleMomentAlreadyLikedException(MomentAlreadyLikedException ex) {
        return new ResponseEntity<>(new ApiError(ex.getMessage(), CONFLICT, LocalDateTime.now()), CONFLICT);
    }

    @ExceptionHandler(MomentNotYetLikedException.class)
    public ResponseEntity<Object> handleMomentNotYetLikedException(MomentNotYetLikedException ex) {
        return new ResponseEntity<>(new ApiError(ex.getMessage(), BAD_REQUEST, LocalDateTime.now()), BAD_REQUEST);
    }

    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<Object> handleCommentNotFoundException(CommentNotFoundException ex) {
        return new ResponseEntity<>(new ApiError(ex.getMessage(), NOT_FOUND, LocalDateTime.now()), NOT_FOUND);
    }

    @ExceptionHandler(EntityDoesNotBelongToUserException.class)
    public ResponseEntity<Object> handleEntityDoesNotBelongToUserException(EntityDoesNotBelongToUserException ex) {
        return new ResponseEntity<>(new ApiError(ex.getMessage(), FORBIDDEN, LocalDateTime.now()), FORBIDDEN);
    }

    @ExceptionHandler(EntityBelongsToUserException.class)
    public ResponseEntity<Object> handleEntityBelongsToUserException(EntityBelongsToUserException ex) {
        return new ResponseEntity<>(new ApiError(ex.getMessage(), FORBIDDEN, LocalDateTime.now()), FORBIDDEN);
    }

    @ExceptionHandler(FollowshipAlreadyExistsException.class)
    public ResponseEntity<Object> handleFollowshipDeletedException(FollowshipAlreadyExistsException ex) {
        return new ResponseEntity<>(new ApiError(ex.getMessage(), CONFLICT, LocalDateTime.now()), CONFLICT);
    }

    @ExceptionHandler(FollowshipNotCreatedUserCantFollowThemselfException.class)
    public ResponseEntity<Object> handleFollowshipNotCreatedUserCantFollowThemselfException(FollowshipNotCreatedUserCantFollowThemselfException ex) {
        return new ResponseEntity<>(new ApiError(ex.getMessage(), BAD_REQUEST, LocalDateTime.now()), BAD_REQUEST);
    }

    @ExceptionHandler(FollowshipDoesNotBelongToUserException.class)
    public ResponseEntity<Object> handleFollowshipDoesNotBelongToUserException(FollowshipDoesNotBelongToUserException ex) {
        return new ResponseEntity<>(new ApiError(ex.getMessage(), FORBIDDEN, LocalDateTime.now()), FORBIDDEN);
    }

    @ExceptionHandler(MessageNotFoundException.class)
    public ResponseEntity<Object> handleMessageNotFoundException(MessageNotFoundException ex) {
        return new ResponseEntity<>(new ApiError(ex.getMessage(), NOT_FOUND, LocalDateTime.now()), NOT_FOUND);
    }

}
