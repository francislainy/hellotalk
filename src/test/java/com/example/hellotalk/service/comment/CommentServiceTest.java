package com.example.hellotalk.service.comment;

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
import com.example.hellotalk.service.impl.comment.CommentServiceImpl;
import com.example.hellotalk.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.hellotalk.exception.AppExceptionHandler.*;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    CommentServiceImpl commentService;

    @Mock
    CommentRepository commentRepository;

    @Mock
    MomentRepository momentRepository;

    @Spy
    CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Mock
    UserService userService;

    ZonedDateTime now = ZonedDateTime.parse(ZonedDateTime.now().format(ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")));
    ZonedDateTime creationDate = now;
    ZonedDateTime lastUpdatedDate = now;

    @Test
    void testGetComment_ValidCommentId_ReturnsComment() {

        UUID commentId = randomUUID();
        UUID userId = randomUUID();
        String username = "anyUsername";
        String name = "anyName";
        UserEntity userEntity = UserEntity.builder().id(userId).username(username).name(name).build();

        UUID momentId = randomUUID();
        MomentEntity momentEntity = MomentEntity.builder().id(momentId).build();
        CommentEntity commentEntity = getCommentEntity(commentId);
        commentEntity.setUserEntity(userEntity);
        commentEntity.setMomentEntity(momentEntity);

        when(commentRepository.findById(any())).thenReturn(Optional.of(commentEntity));

        Comment comment = commentService.getComment(commentId);
        assertAll(
                () -> assertEquals(commentId, comment.getId()),
                () -> assertEquals("anyText", comment.getText()),
                () -> assertEquals(momentId.toString(), comment.getMomentId().toString()),
                () -> assertEquals(userId.toString(), comment.getUser().getId().toString()),
                () -> assertEquals(username, comment.getUser().getUsername()),
                () -> assertEquals(name, comment.getUser().getName()),
                () -> assertEquals(String.valueOf(creationDate), String.valueOf(comment.getCreationDate())));

        verify(commentRepository, times(1)).findById(commentId);
    }

    @Test
    void testGetComment_InvalidCommentId_ThrowsCommentNotFoundException() {

        UUID commentId = randomUUID();
        when(commentRepository.findById(any())).thenReturn(Optional.empty());

        CommentNotFoundException exception =
                assertThrows(CommentNotFoundException.class, () -> commentService.getComment(commentId));

        assertEquals(COMMENT_NOT_FOUND_EXCEPTION, exception.getMessage());

        verify(commentRepository, times(1)).findById(commentId);
    }

    @Test
    void testGetAllCommentsForMoment_ValidMomentId_ReturnsListOfComments() {

        UUID commentId = randomUUID();
        UUID userId = randomUUID();
        String username = "anyUsername";
        String name = "anyName";
        UserEntity userEntity = UserEntity.builder().id(userId).username(username).name(name).build();
        UUID momentId = randomUUID();
        MomentEntity momentEntity = MomentEntity.builder().id(momentId).build();
        CommentEntity commentEntity = getCommentEntity(commentId);
        commentEntity.setUserEntity(userEntity);
        commentEntity.setMomentEntity(momentEntity);

        Comment commentToReturn = commentMapper.toModel(commentEntity);

        when(commentRepository.findAllByMomentEntity_IdContains(any())).thenReturn(List.of(commentEntity));
        when(commentMapper.toModel(any())).thenReturn(commentToReturn);

        List<Comment> allComments = commentService.getAllCommentsForMoment(momentId);
        assertEquals(1, allComments.size());

        Comment comment = allComments.get(0);
        assertAll(
                () -> assertEquals(commentId, comment.getId()),
                () -> assertEquals("anyText", comment.getText()),
                () -> assertEquals(momentId.toString(), comment.getMomentId().toString()),
                () -> assertEquals(userId.toString(), comment.getUser().getId().toString()),
                () -> assertEquals(username, comment.getUser().getUsername()),
                () -> assertEquals(name, comment.getUser().getName()),
                () -> assertEquals(String.valueOf(creationDate), String.valueOf(comment.getCreationDate())),
                () -> assertEquals(String.valueOf(lastUpdatedDate), String.valueOf(comment.getLastUpdatedDate())));

        verify(commentRepository, times(1)).findAllByMomentEntity_IdContains(momentId);
    }

    @Test
    void testGetAllCommentsForMoment_MomentWithNoMoments_ReturnsEmptyList() {

        List<Comment> comments = commentService.getAllCommentsForMoment(randomUUID());
        assertTrue(comments.isEmpty());
    }

    @Test
    void testCreateComment_ValidMomentIdAndComment_ReturnsCreatedComment() {

        UUID commentId = randomUUID();
        UUID momentId = randomUUID();
        UUID userId = randomUUID();
        UserEntity userEntity = UserEntity.builder().id(userId).build();
        CommentEntity commentEntity = getCommentEntity(commentId);
        commentEntity.setUserEntity(userEntity);
        MomentEntity momentEntity = MomentEntity.builder().id(momentId).userEntity(userEntity).build();

        when(userService.getCurrentUser()).thenReturn(userEntity);
        when(commentRepository.save(any())).thenReturn(commentEntity);
        when(momentRepository.findById(any())).thenReturn(Optional.ofNullable(momentEntity));

        Comment comment = commentService.createComment(momentId, commentMapper.toModel(commentEntity));
        assertAll(
                () -> assertEquals(commentId, comment.getId()),
                () -> assertEquals("anyText", comment.getText()),
                () -> assertEquals(userId.toString(), comment.getUser().getId().toString()),
                () -> assertEquals(String.valueOf(creationDate), String.valueOf(comment.getCreationDate())),
                () -> assertEquals(String.valueOf(lastUpdatedDate), String.valueOf(comment.getLastUpdatedDate())));

        verify(userService, times(1)).getCurrentUser();
        verify(momentRepository, times(1)).findById(momentId);
        verify(commentRepository, times(1)).save(any(CommentEntity.class));
    }

    @Test
    void testCreateComment_MomentNotFound_ThrowsMomentNotFoundException() {

        UUID momentId = randomUUID();
        UserEntity userEntity = UserEntity.builder().id(randomUUID()).build();
        CommentEntity commentEntity = getCommentEntity(randomUUID());
        commentEntity.setUserEntity(userEntity);

        when(momentRepository.findById(any())).thenReturn(Optional.empty());

        Comment commentModel = commentMapper.toModel(commentEntity);
        MomentNotFoundException exception =
                assertThrows(MomentNotFoundException.class, () -> commentService.createComment(momentId, commentModel));

        assertEquals(MOMENT_NOT_FOUND_EXCEPTION, exception.getMessage());

        verify(userService, never()).getCurrentUser();
        verify(momentRepository, times(1)).findById(momentId);
        verify(commentRepository, never()).save(any(CommentEntity.class));
    }

    @Test
    void testUpdateComment_ValidCommentIdAndCommentBody_ReturnsUpdatedComment() {

        UUID userId = randomUUID();
        UserEntity userEntity = UserEntity.builder().id(userId).build();
        when(userService.getCurrentUser()).thenReturn(userEntity);

        UUID commentId = randomUUID();
        CommentEntity commentEntity = getCommentEntity(commentId);
        commentEntity.setUserEntity(userEntity);

        CommentEntity commentEntityUpdated = CommentEntity.builder()
                .id(commentId)
                .text("anyUpdatedText")
                .userEntity(userEntity)
                .creationDate(creationDate)
                .lastUpdatedDate(lastUpdatedDate)
                .build();

        when(commentRepository.findById(any())).thenReturn(Optional.of(commentEntity));
        when(commentRepository.save(any())).thenReturn(commentEntityUpdated);

        Comment comment = commentMapper.toModel(commentEntity);
        comment = commentService.updateComment(commentId, comment);

        Comment finalComment = comment;
        assertAll(
                () -> assertEquals(commentId, finalComment.getId()),
                () -> assertEquals("anyUpdatedText", finalComment.getText()),
                () -> assertEquals(userId.toString(), finalComment.getUser().getId().toString()),
                () -> assertEquals(String.valueOf(creationDate), String.valueOf(finalComment.getCreationDate())),
                () -> assertEquals(String.valueOf(lastUpdatedDate), String.valueOf(finalComment.getLastUpdatedDate())));

        verify(commentRepository, times(1)).findById(commentId);
        verify(userService, times(1)).getCurrentUser();
        verify(commentRepository, times(1)).save(any(CommentEntity.class));
    }

    @Test
    void testUpdateComment_CommentNotFound_ThrowsCommentNotFoundException() {

        UUID commentId = randomUUID();
        Comment comment = commentMapper.toModel(getCommentEntity(commentId));
        CommentNotFoundException exception =
                assertThrows(CommentNotFoundException.class, () -> commentService.updateComment(commentId, comment));

        assertEquals(COMMENT_NOT_FOUND_EXCEPTION, exception.getMessage());

        verify(commentRepository, times(1)).findById(commentId);
        verify(userService, never()).getCurrentUser();
        verify(commentRepository, never()).save(any(CommentEntity.class));
    }

    @Test
    void testUpdateCommentDetails_UnauthorizedUser_ThrowsEntityDoesNotBelongToUserException() {

        UUID userId = randomUUID();
        UserEntity userEntity = UserEntity.builder().id(userId).build();
        UserEntity unauthorizedUserEntity = UserEntity.builder().id(randomUUID()).build();
        when(userService.getCurrentUser()).thenReturn(unauthorizedUserEntity);

        UUID commentId = randomUUID();
        CommentEntity commentEntity = getCommentEntity(commentId);
        commentEntity.setUserEntity(userEntity);
        Comment comment = commentMapper.toModel(commentEntity);

        when(commentRepository.findById(any())).thenReturn(Optional.of(commentEntity));
        EntityDoesNotBelongToUserException exception =
                assertThrows(EntityDoesNotBelongToUserException.class, () -> commentService.updateComment(commentId, comment));

        assertEquals(ENTITY_DOES_NOT_BELONG_TO_USER_EXCEPTION, exception.getMessage());

        verify(commentRepository, times(1)).findById(commentId);
        verify(userService, times(1)).getCurrentUser();
        verify(commentRepository, never()).save(any(CommentEntity.class));
    }

    @Test
    void testDeleteComment_ValidCommentId_DeletesTheCommentAndReturnsSuccessMessage() {

        UUID userId = randomUUID();
        UserEntity userEntity = UserEntity.builder().id(userId).build();
        when(userService.getCurrentUser()).thenReturn(userEntity);

        UUID commentId = randomUUID();
        CommentEntity commentEntity = getCommentEntity(commentId);
        commentEntity.setUserEntity(userEntity);

        String json = """
                {"message": "Comment Deleted"}
                """;

        when(commentRepository.findById(any())).thenReturn(Optional.of(commentEntity));
        assertEquals(json, assertDoesNotThrow(() -> commentService.deleteComment(commentId)));
        verify(commentRepository, times(1)).deleteById(commentId);
    }

    @Test
    void testDeleteComment_InvalidCommentId_ThrowsCommentNotFoundException() {

        UUID commentId = randomUUID();
        CommentNotFoundException exception =
                assertThrows(CommentNotFoundException.class, () -> commentService.deleteComment(commentId));

        assertEquals(COMMENT_NOT_FOUND_EXCEPTION, exception.getMessage());
        verify(commentRepository, never()).deleteById(commentId);
    }

    @Test
    void testDeleteComment_CommentDoesNotBelongToUser_ThrowsEntityDoesNotBelongToUserException() {

        UUID userId = UUID.randomUUID();
        UserEntity userEntity = UserEntity.builder().id(userId).build();
        UserEntity unauthorizedUserEntity = UserEntity.builder().id(randomUUID()).build();

        UUID commentId = randomUUID();
        CommentEntity commentEntity = getCommentEntity(commentId);
        commentEntity.setUserEntity(userEntity);

        when(userService.getCurrentUser()).thenReturn(unauthorizedUserEntity);
        when(commentRepository.findById(any())).thenReturn(Optional.of(commentEntity));

        EntityDoesNotBelongToUserException exception =
                assertThrows(EntityDoesNotBelongToUserException.class, () -> commentService.deleteComment(commentId));

        assertEquals(ENTITY_DOES_NOT_BELONG_TO_USER_EXCEPTION, exception.getMessage());
        verify(commentRepository, never()).deleteById(commentId);
    }

    // Helpers
    private CommentEntity getCommentEntity(UUID commentId) {

        ZonedDateTime now = ZonedDateTime.now();
        creationDate = ZonedDateTime.parse(now.format(ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")));

        return CommentEntity.builder()
                .id(commentId)
                .text("anyText")
                .creationDate(creationDate)
                .lastUpdatedDate(lastUpdatedDate)
                .build();
    }

}
