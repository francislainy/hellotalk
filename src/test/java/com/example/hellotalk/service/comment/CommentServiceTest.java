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
import java.util.*;
import static java.util.Collections.emptyList;

import static com.example.hellotalk.exception.AppExceptionHandler.*;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentServiceImpl commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private MomentRepository momentRepository;

    @Spy
    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Mock
    private UserService userService;

    private final ZonedDateTime now = ZonedDateTime.parse(ZonedDateTime.now().format(ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")));
    private final ZonedDateTime creationDate = now;
    private final ZonedDateTime lastUpdatedDate = now;

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
                () -> assertEquals("anyText", comment.getContent()),
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

        when(commentRepository.findAllByMomentEntityId(any())).thenReturn(List.of(commentEntity));
        when(commentMapper.toModel(any())).thenReturn(commentToReturn);

        List<Comment> allComments = commentService.getAllCommentsForMoment(momentId);
        assertEquals(1, allComments.size());

        Comment comment = allComments.get(0);
        assertAll(
                () -> assertEquals(commentId, comment.getId()),
                () -> assertEquals("anyText", comment.getContent()),
                () -> assertEquals(momentId.toString(), comment.getMomentId().toString()),
                () -> assertEquals(userId.toString(), comment.getUser().getId().toString()),
                () -> assertEquals(username, comment.getUser().getUsername()),
                () -> assertEquals(name, comment.getUser().getName()),
                () -> assertEquals(String.valueOf(creationDate), String.valueOf(comment.getCreationDate())),
                () -> assertEquals(String.valueOf(lastUpdatedDate), String.valueOf(comment.getLastUpdatedDate())));

        verify(commentRepository, times(1)).findAllByMomentEntityId(momentId);
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
                () -> assertEquals("anyText", comment.getContent()),
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

        when(userService.getCurrentUser()).thenReturn(userEntity);
        when(momentRepository.findById(any())).thenReturn(Optional.empty());

        Comment commentModel = commentMapper.toModel(commentEntity);
        MomentNotFoundException exception =
                assertThrows(MomentNotFoundException.class, () -> commentService.createComment(momentId, commentModel));

        assertEquals(MOMENT_NOT_FOUND_EXCEPTION, exception.getMessage());

        verify(userService, times(1)).getCurrentUser();
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
                .content("anyUpdatedText")
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
                () -> assertEquals("anyUpdatedText", finalComment.getContent()),
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
        verify(userService, times(1)).getCurrentUser();
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

        when(commentRepository.findById(any())).thenReturn(Optional.of(commentEntity));
        assertDoesNotThrow(() -> commentService.deleteComment(commentId));
        verify(commentRepository, times(1)).deleteById(commentId);
    }

    @Test
    void testDeleteComment_InvalidCommentId_ThrowsCommentNotFoundException() {

        UUID userId = randomUUID();
        UserEntity userEntity = UserEntity.builder().id(userId).build();
        when(userService.getCurrentUser()).thenReturn(userEntity);

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

    @Test
    void testCreateReply_ValidCommentIdAndComment_ReturnsCreatedReply() {

        UUID childCommentId = randomUUID();
        UUID parentCommentId = randomUUID();
        UUID userId = randomUUID();
        UserEntity userEntity = UserEntity.builder().id(userId).build();
        CommentEntity parentCommentEntity = getCommentEntity(parentCommentId);
        parentCommentEntity.setUserEntity(userEntity);

        CommentEntity childReplyCommentEntity = getCommentEntity(childCommentId);
        childReplyCommentEntity.setUserEntity(userEntity);
        childReplyCommentEntity.setParentCommentEntity(parentCommentEntity);

        when(userService.getCurrentUser()).thenReturn(userEntity);
        when(commentRepository.findById(parentCommentId)).thenReturn(Optional.of(parentCommentEntity));
        when(commentRepository.save(any())).thenReturn(childReplyCommentEntity);

        Comment comment = commentService.replyToComment(parentCommentId, commentMapper.toModel(childReplyCommentEntity));
        assertAll(
                () -> assertEquals(childCommentId, comment.getId()),
                () -> assertEquals("anyText", comment.getContent()),
                () -> assertEquals(userId.toString(), comment.getUser().getId().toString()),
                () -> assertEquals(String.valueOf(creationDate), String.valueOf(comment.getCreationDate())),
                () -> assertEquals(String.valueOf(lastUpdatedDate), String.valueOf(comment.getLastUpdatedDate())));

        verify(userService, times(1)).getCurrentUser();
        verify(commentRepository, times(1)).save(any(CommentEntity.class));
    }

    @Test
    void testCreateReply_ParentCommentNotFound_ThrowsException() {

        UUID childCommentId = randomUUID();
        UUID parentCommentId = randomUUID();
        UUID userId = randomUUID();

        UserEntity userEntity = UserEntity.builder().id(userId).build();
        CommentEntity parentCommentEntity = getCommentEntity(parentCommentId);
        parentCommentEntity.setUserEntity(userEntity);

        CommentEntity childReplyCommentEntity = getCommentEntity(childCommentId);
        childReplyCommentEntity.setUserEntity(userEntity);
        childReplyCommentEntity.setParentCommentEntity(parentCommentEntity);

        when(userService.getCurrentUser()).thenReturn(userEntity);
        when(commentRepository.findById(parentCommentId)).thenThrow(new CommentNotFoundException(COMMENT_NOT_FOUND_EXCEPTION));

        Comment childReplyComment = commentMapper.toModel(childReplyCommentEntity);

        CommentNotFoundException exception =
                assertThrows(CommentNotFoundException.class, () -> commentService.replyToComment(parentCommentId, childReplyComment));

        assertEquals(COMMENT_NOT_FOUND_EXCEPTION, exception.getMessage());
        verify(userService, times(1)).getCurrentUser();
        verify(commentRepository, never()).save(any(CommentEntity.class));
    }

    @Test
    void testGetReply_ParentCommentExist_ReturnsValidReply() {

        UUID childCommentId = randomUUID();
        UUID parentCommentId = randomUUID();
        UUID userId = randomUUID();

        UserEntity userEntity = UserEntity.builder().id(userId).build();
        UserEntity userEntityParent = UserEntity.builder().id(randomUUID()).build();
        CommentEntity parentCommentEntity = getCommentEntity(parentCommentId);
        parentCommentEntity.setUserEntity(userEntityParent);

        CommentEntity childReplyCommentEntity = getCommentEntity(childCommentId);
        childReplyCommentEntity.setUserEntity(userEntity);
        childReplyCommentEntity.setParentCommentEntity(parentCommentEntity);

        when(userService.getCurrentUser()).thenReturn(userEntity);
        when(commentRepository.findById(parentCommentId)).thenReturn(Optional.of(parentCommentEntity));

        Comment childReplyComment = commentMapper.toModel(childReplyCommentEntity);

        EntityDoesNotBelongToUserException exception =
                assertThrows(EntityDoesNotBelongToUserException.class, () -> commentService.replyToComment(parentCommentId, childReplyComment));

        assertEquals(ENTITY_DOES_NOT_BELONG_TO_USER_EXCEPTION, exception.getMessage());
        verify(userService, times(1)).getCurrentUser();
        verify(commentRepository, never()).save(any(CommentEntity.class));
    }

    @Test
    void testCreateReply_ParentCommentDoesNotBelongToUser_ThrowsException() {

        UUID childCommentId = randomUUID();
        UUID parentCommentId = randomUUID();
        UUID userId = randomUUID();

        UserEntity userEntity = UserEntity.builder().id(userId).build();
        UserEntity userEntityParent = UserEntity.builder().id(randomUUID()).build();
        CommentEntity parentCommentEntity = getCommentEntity(parentCommentId);
        parentCommentEntity.setUserEntity(userEntityParent);

        CommentEntity childReplyCommentEntity = getCommentEntity(childCommentId);
        childReplyCommentEntity.setUserEntity(userEntity);
        childReplyCommentEntity.setParentCommentEntity(parentCommentEntity);

        when(userService.getCurrentUser()).thenReturn(userEntity);
        when(commentRepository.findById(parentCommentId)).thenReturn(Optional.of(parentCommentEntity));

        Comment childReplyComment = commentMapper.toModel(childReplyCommentEntity);

        EntityDoesNotBelongToUserException exception =
                assertThrows(EntityDoesNotBelongToUserException.class, () -> commentService.replyToComment(parentCommentId, childReplyComment));

        assertEquals(ENTITY_DOES_NOT_BELONG_TO_USER_EXCEPTION, exception.getMessage());
        verify(userService, times(1)).getCurrentUser();
        verify(commentRepository, never()).save(any(CommentEntity.class));
    }

    @Test
    void testGetRepliesForComment_ValidCommentAndExistingReplies_ReturnsListOfReplies() {
        UUID momentId = randomUUID();
        UUID commentId = randomUUID();

        UserEntity userEntity = UserEntity.builder().id(randomUUID()).build();
        CommentEntity parentCommentEntity = CommentEntity.builder().id(commentId).userEntity(userEntity).build();
        CommentEntity commentEntityReply1 = CommentEntity.builder().id(commentId).userEntity(userEntity).build();
        CommentEntity commentEntityReply2 = CommentEntity.builder().id(commentId).userEntity(userEntity).build();
        List<CommentEntity> replyEntities = List.of(commentEntityReply1, commentEntityReply2);

        when(userService.getCurrentUser()).thenReturn(userEntity);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(parentCommentEntity));
        when(commentRepository.findAllByParentCommentEntityId(commentId)).thenReturn(replyEntities);

        List<Comment> replies = commentService.getRepliesForComment(momentId, commentId);
        assertEquals(replyEntities.size(), replies.size());
    }

    @Test
    void testGetRepliesForComment_ValidCommentButNoReplies_ReturnsEmptyList() {
        UUID momentId = randomUUID();
        UUID commentId = randomUUID();

        UserEntity userEntity = UserEntity.builder().id(randomUUID()).build();
        CommentEntity parentCommentEntity = CommentEntity.builder().id(commentId).userEntity(userEntity).build();

        when(userService.getCurrentUser()).thenReturn(userEntity);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(parentCommentEntity));
        when(commentRepository.findAllByParentCommentEntityId(commentId)).thenReturn(emptyList());

        List<Comment> replies = commentService.getRepliesForComment(momentId, commentId);
        assertEquals(0, replies.size());
    }

    @Test
    void testGetRepliesForComment_CommentNotFound_ThrowsException() {
        UUID momentId = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();

        UserEntity userEntity = UserEntity.builder().id(randomUUID()).build();

        when(userService.getCurrentUser()).thenReturn(userEntity);
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        CommentNotFoundException exception = assertThrows(CommentNotFoundException.class,
                () -> commentService.getRepliesForComment(momentId, commentId));

        assertEquals(COMMENT_NOT_FOUND_EXCEPTION, exception.getMessage());
    }

    @Test
    void testGetRepliesForComment_EntityDoesNotBelongToUser_ThrowsException() {
        UUID momentId = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();

        UserEntity userEntity = UserEntity.builder().id(randomUUID()).build();
        UserEntity anotherUserEntity = UserEntity.builder().id(randomUUID()).build();

        CommentEntity commentEntity = CommentEntity.builder().id(commentId).userEntity(anotherUserEntity).build();

        when(userService.getCurrentUser()).thenReturn(userEntity);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(commentEntity));

        EntityDoesNotBelongToUserException exception = assertThrows(EntityDoesNotBelongToUserException.class,
                () -> commentService.getRepliesForComment(momentId, commentId));

        assertEquals(ENTITY_DOES_NOT_BELONG_TO_USER_EXCEPTION, exception.getMessage());
    }

    // Helpers
    private CommentEntity getCommentEntity(UUID commentId) {
        return CommentEntity.builder()
                .id(commentId)
                .content("anyText")
                .creationDate(creationDate)
                .lastUpdatedDate(lastUpdatedDate)
                .build();
    }

}
