package com.example.hellotalk.service.impl.comment;

import com.example.hellotalk.entity.comment.CommentEntity;
import com.example.hellotalk.entity.moment.MomentEntity;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.exception.CommentNotFoundException;
import com.example.hellotalk.exception.EntityDoesNotBelongToUserException;
import com.example.hellotalk.exception.MomentNotFoundException;
import com.example.hellotalk.model.comment.Comment;
import com.example.hellotalk.repository.user.UserRepository;
import com.example.hellotalk.repository.comment.CommentRepository;
import com.example.hellotalk.repository.moment.MomentRepository;
import com.example.hellotalk.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.hellotalk.exception.AppExceptionHandler.*;
import static com.example.hellotalk.model.comment.Comment.fromEntity;

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

        return fromEntity(commentEntity);
    }

    @Override
    public List<Comment> getAllCommentsForMoment(UUID momentId) {

        List<CommentEntity> commentEntityList = commentRepository.findAllByMomentEntity_IdContains(momentId);

        return commentEntityList.stream()
                .map(Comment::fromEntity)
                .toList();
    }

    @Override
    public Comment createComment(UUID momentId, Comment comment) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserEntity userEntity = userRepository.findByUsername(username);

        Optional<MomentEntity> optionalMomentEntity = momentRepository.findById(momentId);
        if (optionalMomentEntity.isPresent()) {

            MomentEntity momentEntity = optionalMomentEntity.get();
            CommentEntity commentEntity = CommentEntity.fromModel(comment).toBuilder()
                    .userEntity(userEntity)
                    .creationDate(ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC))
                    .momentEntity(momentEntity)
                    .build();
            commentEntity = commentRepository.save(commentEntity);

            return fromEntity(commentEntity);
        } else
            throw new MomentNotFoundException(MOMENT_NOT_FOUND_EXCEPTION);
    }

    @Override
    public Comment updateComment(UUID commentId, Comment comment) {

        ZonedDateTime formattedDate = ZonedDateTime.parse(ZonedDateTime.now().format(formatter));

        CommentEntity commentEntity = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(COMMENT_NOT_FOUND_EXCEPTION));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserEntity userEntity = userRepository.findByUsername(username);

        if (!username.equals(commentEntity.getUserEntity().getUsername())) {
            throw new EntityDoesNotBelongToUserException(ENTITY_DOES_NOT_BELONG_TO_USER_EXCEPTION);
        }

        commentEntity = commentEntity.toBuilder()
                .text(comment.getText())
                .userEntity(userEntity)
                .lastUpdatedDate(formattedDate)
                .build();

        commentEntity = commentRepository.save(commentEntity);

        return fromEntity(commentEntity);
    }

    @Override
    public String deleteComment(UUID commentId) {

        Optional<CommentEntity> optionalCommentEntity = commentRepository.findById(commentId);
        String json = """
                {"message": "Comment Deleted"}
                """;

        if (optionalCommentEntity.isPresent()) {

            CommentEntity commentEntity = optionalCommentEntity.get();

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            if (!username.equals(commentEntity.getUserEntity().getUsername())) {
                throw new EntityDoesNotBelongToUserException(ENTITY_DOES_NOT_BELONG_TO_USER_EXCEPTION);
            } else {
                commentRepository.deleteById(commentId);
                return json;
            }
        } else {
            throw new CommentNotFoundException(COMMENT_NOT_FOUND_EXCEPTION);
        }
    }

}
