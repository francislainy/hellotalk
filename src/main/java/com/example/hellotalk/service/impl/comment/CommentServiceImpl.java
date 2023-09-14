package com.example.hellotalk.service.impl.comment;

import com.example.hellotalk.entity.comment.CommentEntity;
import com.example.hellotalk.entity.moment.MomentEntity;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.exception.CommentNotFoundException;
import com.example.hellotalk.exception.EntityDoesNotBelongToUserException;
import com.example.hellotalk.exception.MomentNotFoundException;
import com.example.hellotalk.mapper.CommentMapper;
import com.example.hellotalk.model.comment.Comment;
import com.example.hellotalk.repository.comment.CommentRepository;
import com.example.hellotalk.repository.moment.MomentRepository;
import com.example.hellotalk.service.comment.CommentService;
import com.example.hellotalk.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static com.example.hellotalk.exception.AppExceptionHandler.*;

@RequiredArgsConstructor
@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final MomentRepository momentRepository;

    private final UserService userService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private final CommentMapper commentMapper;

    @Override
    public Comment getComment(UUID commentId) {
        CommentEntity commentEntity = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(COMMENT_NOT_FOUND_EXCEPTION));

        return commentMapper.toModel(commentEntity);
    }

    @Override
    public List<Comment> getAllCommentsForMoment(UUID momentId) {

        List<CommentEntity> commentEntityList = commentRepository.findAllByMomentEntityId(momentId);

        return commentEntityList.stream()
                .map(commentMapper::toModel)
                .toList();
    }

    @Override
    public Comment createComment(UUID momentId, Comment comment) {

        UserEntity userEntity = userService.getCurrentUser();

        MomentEntity momentEntity = momentRepository.findById(momentId)
                .orElseThrow(() -> new MomentNotFoundException(MOMENT_NOT_FOUND_EXCEPTION));

        CommentEntity commentEntity = commentMapper.toEntity(comment).toBuilder()
                .userEntity(userEntity)
                .creationDate(ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC))
                .momentEntity(momentEntity)
                .parentCommentEntity(null)
                .build();
        commentEntity = commentRepository.save(commentEntity);

        return commentMapper.toModel(commentEntity);
    }

    @Override
    public Comment updateComment(UUID commentId, Comment comment) {

        UserEntity userEntity = userService.getCurrentUser();

        CommentEntity commentEntity = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(COMMENT_NOT_FOUND_EXCEPTION));

        if (!userEntity.getId().equals(commentEntity.getUserEntity().getId())) {
            throw new EntityDoesNotBelongToUserException(ENTITY_DOES_NOT_BELONG_TO_USER_EXCEPTION);
        }

        ZonedDateTime formattedDate = ZonedDateTime.parse(ZonedDateTime.now().format(formatter));
        commentEntity = commentEntity.toBuilder()
                .content(comment.getContent())
                .userEntity(userEntity)
                .lastUpdatedDate(formattedDate)
                .build();

        commentEntity = commentRepository.save(commentEntity);

        return commentMapper.toModel(commentEntity);
    }

    @Override
    public void deleteComment(UUID commentId) {

        UserEntity userEntity = userService.getCurrentUser();

        CommentEntity commentEntity = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(COMMENT_NOT_FOUND_EXCEPTION));

        if (!userEntity.getId().equals(commentEntity.getUserEntity().getId())) {
            throw new EntityDoesNotBelongToUserException(ENTITY_DOES_NOT_BELONG_TO_USER_EXCEPTION);
        }

        commentRepository.deleteById(commentId);
    }

    @Override
    public Comment replyToComment(UUID parentCommentId, Comment replyComment) {

        UserEntity userEntity = userService.getCurrentUser();

        CommentEntity parentCommentEntity = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new CommentNotFoundException(COMMENT_NOT_FOUND_EXCEPTION));

        if (!userEntity.getId().equals(parentCommentEntity.getUserEntity().getId())) {
            throw new EntityDoesNotBelongToUserException(ENTITY_DOES_NOT_BELONG_TO_USER_EXCEPTION);
        }

        replyComment.setParentId(parentCommentId);

        CommentEntity childCommentEntity = commentMapper.toEntity(replyComment).toBuilder()
                .userEntity(userEntity)
                .creationDate(ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC))
                .momentEntity(parentCommentEntity.getMomentEntity())
                .parentCommentEntity(parentCommentEntity)
                .build();
        childCommentEntity = commentRepository.save(childCommentEntity);

        return commentMapper.toModel(childCommentEntity);
    }

    @Override
    public List<Comment> getRepliesForComment(UUID momentId, UUID commentId) {

        UserEntity userEntity = userService.getCurrentUser();

        CommentEntity commentEntity = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(COMMENT_NOT_FOUND_EXCEPTION));

        if (!userEntity.getId().equals(commentEntity.getUserEntity().getId())) {
            throw new EntityDoesNotBelongToUserException(ENTITY_DOES_NOT_BELONG_TO_USER_EXCEPTION);
        }

        return commentRepository.findAllByParentCommentEntityId(commentId)
                .stream()
                .map(commentMapper::toModel)
                .toList();
    }

}
