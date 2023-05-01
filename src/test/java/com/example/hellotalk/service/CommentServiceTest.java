package com.example.hellotalk.service;

import com.example.hellotalk.entity.comment.CommentEntity;
import com.example.hellotalk.entity.moment.MomentEntity;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.exception.CommentNotFoundException;
import com.example.hellotalk.exception.MomentNotFoundException;
import com.example.hellotalk.model.comment.Comment;
import com.example.hellotalk.repository.UserRepository;
import com.example.hellotalk.repository.comment.CommentRepository;
import com.example.hellotalk.repository.moment.MomentRepository;
import com.example.hellotalk.service.impl.comment.CommentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.hellotalk.Constants.USER_ID;
import static com.example.hellotalk.exception.AppExceptionHandler.COMMENT_NOT_FOUND_EXCEPTION;
import static com.example.hellotalk.exception.AppExceptionHandler.MOMENT_NOT_FOUND_EXCEPTION;
import static com.example.hellotalk.model.comment.Comment.buildCommentFromEntity;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    CommentServiceImpl commentService;

    @Mock
    CommentRepository commentRepository;

    @Mock
    MomentRepository momentRepository;

    @Mock
    UserRepository userRepository;

    ZonedDateTime now = ZonedDateTime.parse(ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")));
    ZonedDateTime creationDate = now;
    ZonedDateTime lastUpdatedDate = now;
    UUID userCreatorId = USER_ID;

    @Test
    void testGetComment() {

        UUID commentId = randomUUID();
        CommentEntity commentEntity = getCommentEntity(commentId);

        when(commentRepository.findById(any())).thenReturn(Optional.of(commentEntity));

        Comment comment = commentService.getComment(commentId);

        assertAll(
                () -> assertEquals(commentId, comment.getId()),
                () -> assertEquals("anyText", comment.getText()),
                () -> assertEquals(userCreatorId, comment.getUserCreatorId()),
                () -> assertEquals(String.valueOf(creationDate), String.valueOf(comment.getCreationDate())));
    }

    @Test
    void testGetComment_ThrowsExceptionCommentDoesNotExist() {

        UUID commentId = randomUUID();
        when(commentRepository.findById(any())).thenReturn(Optional.empty());

        CommentNotFoundException exception =
                assertThrows(CommentNotFoundException.class, () -> commentService.getComment(commentId));

        assertEquals(COMMENT_NOT_FOUND_EXCEPTION, exception.getMessage());
    }

    @Test
    void testGetAllCommentsForMoment() {

        UUID commentId = randomUUID();
        UUID momentId = randomUUID();
        CommentEntity commentEntity = getCommentEntity(commentId);

        when(commentRepository.findAllByMomentEntity_IdContains(any())).thenReturn(List.of(commentEntity));

        List<Comment> allComments = commentService.getAllCommentsForMoment(momentId);
        assertEquals(1, allComments.size());

        Comment comment = allComments.get(0);

        assertAll(
                () -> assertEquals(userCreatorId, comment.getUserCreatorId()),
                () -> assertEquals(commentId, comment.getId()),
                () -> assertEquals("anyText", comment.getText()),
                () -> assertEquals(userCreatorId, comment.getUserCreatorId()),
                () -> assertEquals(String.valueOf(creationDate), String.valueOf(comment.getCreationDate())),
                () -> assertEquals(String.valueOf(lastUpdatedDate), String.valueOf(comment.getLastUpdatedDate())));
    }

    @Test
    void testGetAllCommentsForMoment_ReturnsEmptyListForMomentWithNoMoments() {

        List<Comment> comments = commentService.getAllCommentsForMoment(randomUUID());
        assertTrue(comments.isEmpty());
    }

    @Test
    void testCreateComment() {

        UUID commentId = randomUUID();
        UUID momentId = randomUUID();
        CommentEntity commentEntity = getCommentEntity(commentId);
        MomentEntity momentEntity = MomentEntity.builder().id(momentId).build();
        UserEntity userEntity = UserEntity.builder().id(userCreatorId).build();

        when(commentRepository.save(any())).thenReturn(commentEntity);
        when(momentRepository.findById(any())).thenReturn(Optional.ofNullable(momentEntity));
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(userEntity));

        Comment comment = commentService.createComment(momentId, buildCommentFromEntity(commentEntity), userCreatorId.toString());
        assertAll(
                () -> assertEquals(commentId, comment.getId()),
                () -> assertEquals("anyText", comment.getText()),
                () -> assertEquals(userCreatorId, comment.getUserCreatorId()),
                () -> assertEquals(String.valueOf(creationDate), String.valueOf(comment.getCreationDate())),
                () -> assertEquals(String.valueOf(lastUpdatedDate), String.valueOf(comment.getLastUpdatedDate())));
    }

    @Test
    void testCreateComment_ThrowsExceptionMomentDoesNotExist() {

        UUID commentId = randomUUID();
        UUID momentId = randomUUID();
        CommentEntity commentEntity = getCommentEntity(commentId);

        Optional<UserEntity> optionalUserEntity = Optional.ofNullable(UserEntity.builder().id(commentEntity.getUserEntity().getId()).build());
        when(userRepository.findById(any())).thenReturn(optionalUserEntity);
        when(momentRepository.findById(any())).thenReturn(Optional.empty());

        MomentNotFoundException exception =
                assertThrows(MomentNotFoundException.class, () -> commentService.createComment(momentId, buildCommentFromEntity(commentEntity), userCreatorId.toString()));

        assertEquals(MOMENT_NOT_FOUND_EXCEPTION, exception.getMessage());
    }

    @Test
    void testUpdateCommentDetails() {

        UUID commentId = randomUUID();
        CommentEntity commentEntity = getCommentEntity(commentId);

        CommentEntity commentEntityUpdated = CommentEntity.builder()
                .id(commentId)
                .text("anyUpdatedText")
                .userEntity(UserEntity.builder().id(userCreatorId).build())
                .creationDate(creationDate)
                .lastUpdatedDate(lastUpdatedDate)
                .build();

        when(commentRepository.findById(any())).thenReturn(Optional.of(commentEntity));
        when(commentRepository.save(any())).thenReturn(commentEntityUpdated);

        Comment comment = buildCommentFromEntity(commentEntity);
        comment = commentService.updateComment(commentId, comment);

        Comment finalComment = comment;
        assertAll(
                () -> assertEquals(commentId, finalComment.getId()),
                () -> assertEquals("anyUpdatedText", finalComment.getText()),
                () -> assertEquals(userCreatorId, finalComment.getUserCreatorId()),
                () -> assertEquals(String.valueOf(creationDate), String.valueOf(finalComment.getCreationDate())),
                () -> assertEquals(String.valueOf(lastUpdatedDate), String.valueOf(finalComment.getLastUpdatedDate())));
    }

    @Test
    void testUpdateCommentDetails_ThrowsExceptionWhenCommentIsNotFound() {

        UUID commentId = randomUUID();
        Comment comment = buildCommentFromEntity(getCommentEntity(commentId));
        CommentNotFoundException exception =
                assertThrows(CommentNotFoundException.class, () -> commentService.updateComment(commentId, comment));

        assertEquals(COMMENT_NOT_FOUND_EXCEPTION, exception.getMessage());
    }

    @Test
    void testDeleteComment() {

        String json = """
                {"message": "Comment Deleted"}
                """;

        UUID commentId = randomUUID();
        when(commentRepository.findById(any())).thenReturn(Optional.of(getCommentEntity(commentId)));
        assertEquals(json, assertDoesNotThrow(() -> commentService.deleteComment(commentId)));
    }

    @Test
    void testDeleteComment_ThrowsExceptionWhenCommentIsNotFound() {

        UUID commentId = randomUUID();
        CommentNotFoundException exception =
                assertThrows(CommentNotFoundException.class, () -> commentService.deleteComment(commentId));

        assertEquals(COMMENT_NOT_FOUND_EXCEPTION, exception.getMessage());
    }

    private CommentEntity getCommentEntity(UUID momentId) {

        ZonedDateTime now = ZonedDateTime.now();
        creationDate = ZonedDateTime.parse(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")));

        return CommentEntity.builder()
                .id(momentId)
                .text("anyText")
                .userEntity(UserEntity.builder().id(userCreatorId).build())
                .creationDate(creationDate)
                .lastUpdatedDate(lastUpdatedDate)
                .build();
    }
}
