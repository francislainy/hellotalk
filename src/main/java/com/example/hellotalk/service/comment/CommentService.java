package com.example.hellotalk.service.comment;

import com.example.hellotalk.model.comment.Comment;

import java.util.List;
import java.util.UUID;

public interface CommentService {

    Comment getComment(UUID commentId);

    List<Comment> getAllCommentsForMoment(UUID momentId);

    Comment createComment(UUID momentId, Comment comment);

    Comment updateComment(UUID commentId, Comment comment);

    void deleteComment(UUID commentId);

    Comment replyToComment(UUID parentCommentId, Comment replyComment);
}
