package com.example.hellotalk.service.impl.comment;

import com.example.hellotalk.entity.comment.CommentEntity;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.exception.CommentNotFoundException;
import com.example.hellotalk.exception.MomentNotFoundException;
import com.example.hellotalk.exception.UserNotFoundException;
import com.example.hellotalk.model.comment.Comment;
import com.example.hellotalk.repository.UserRepository;
import com.example.hellotalk.repository.comment.CommentRepository;
import com.example.hellotalk.repository.moment.MomentRepository;
import com.example.hellotalk.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.hellotalk.exception.AppExceptionHandler.*;
import static com.example.hellotalk.model.comment.Comment.buildCommentFromEntity;
import static com.example.hellotalk.util.Utils.parseUUID;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final MomentRepository momentRepository;
    private final UserRepository userRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    @Override
    public Comment getComment(UUID commentId) {
        CommentEntity commentEntity = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(COMMENT_NOT_FOUND_EXCEPTION));

        return buildCommentFromEntity(commentEntity);
    }

    @Override
    public List<Comment> getAllCommentsForMoment(UUID momentId) {

        List<CommentEntity> commentEntityList = commentRepository.findAllByMomentEntity_IdContains(momentId);

        return commentEntityList.stream()
                .map(Comment::buildCommentFromEntity)
                .toList();
    }

    @Override
    public Comment createComment(UUID momentId, Comment comment, String authorization) {

        UserEntity userEntity = userRepository.findById(parseUUID(authorization))
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_EXCEPTION));

        if (momentRepository.findById(momentId).isPresent()) {

            CommentEntity commentEntity = CommentEntity.buildCommentEntityFromModel(comment).toBuilder()
                    .userEntity(userEntity)
                    .creationDate(ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC))
                    .build();
            commentEntity = commentRepository.save(commentEntity);

            return buildCommentFromEntity(commentEntity);
        } else
            throw new MomentNotFoundException(MOMENT_NOT_FOUND_EXCEPTION);
    }

    @Override
    public Comment updateComment(UUID commentId, Comment comment) {

        CommentEntity commentEntity = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(COMMENT_NOT_FOUND_EXCEPTION));

        ZonedDateTime formattedDate = ZonedDateTime.parse(ZonedDateTime.now().format(formatter));
        UserEntity userEntity = UserEntity.builder().id(comment.getUserCreatorId()).build();

        commentEntity = commentEntity.toBuilder()
                .text(comment.getText())
                .userEntity(userEntity)
                .lastUpdatedDate(formattedDate)
                .build();

        commentEntity = commentRepository.save(commentEntity);

        return buildCommentFromEntity(commentEntity);
    }

    @Override
    public String deleteComment(UUID commentId) {

        Optional<CommentEntity> optionalCommentEntity = commentRepository.findById(commentId);
        String json = """
                {"message": "Comment Deleted"}
                """;

        if (optionalCommentEntity.isPresent()) {
            commentRepository.deleteById(commentId);
            return json;
        } else {
            throw new CommentNotFoundException(COMMENT_NOT_FOUND_EXCEPTION);
        }
    }

}
