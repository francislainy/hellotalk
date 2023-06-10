package com.example.hellotalk.controller;

import com.example.hellotalk.model.comment.Comment;
import com.example.hellotalk.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/ht/moments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping({"/{momentId}/comments/{commentId}", "/{momentId}/comments/{commentId}/"})
    public ResponseEntity<Object> getComment(@PathVariable UUID commentId) {
        return new ResponseEntity<>(commentService.getComment(commentId), HttpStatus.OK);
    }

    @GetMapping({"/{momentId}/comments", "/{momentId}/comments/"})
    public ResponseEntity<Object> getAllCommentsForMoment(@PathVariable("momentId") UUID momentId) {
        return new ResponseEntity<>(commentService.getAllCommentsForMoment(momentId), HttpStatus.OK);
    }

    @PostMapping({"/{momentId}/comments", "/{momentId}/comments/"})
    public ResponseEntity<Object> createComment(@PathVariable UUID momentId, @RequestBody Comment comment) {
        return new ResponseEntity<>(commentService.createComment(momentId, comment), HttpStatus.CREATED);
    }

    @PutMapping({"/{momentId}/comments/{commentId}", "/{momentId}/comments/{commentId}/"})
    public ResponseEntity<Object> updateMoment(@PathVariable UUID commentId, @RequestBody Comment comment) {
        return new ResponseEntity<>(commentService.updateComment(commentId, comment), HttpStatus.OK);
    }

    @DeleteMapping({"/{momentId}/comments/{commentId}", "/{momentId}/comments/{commentId}/"})
    public ResponseEntity<Object> deleteMoment(@PathVariable UUID commentId) {
        return new ResponseEntity<>(commentService.deleteComment(commentId), HttpStatus.PARTIAL_CONTENT);
    }
}
