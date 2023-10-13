package com.example.hellotalk.controller.comment;

import com.example.hellotalk.config.BaseDocTestConfig;
import com.example.hellotalk.exception.CommentNotFoundException;
import com.example.hellotalk.exception.EntityDoesNotBelongToUserException;
import com.example.hellotalk.model.comment.Comment;
import com.example.hellotalk.model.user.UserSnippet;
import com.example.hellotalk.service.comment.CommentService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.example.hellotalk.util.Utils.convertToNewObject;
import static com.example.hellotalk.util.Utils.jsonStringFromObject;
import static java.util.UUID.randomUUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CommentController.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CommentControllerTest extends BaseDocTestConfig {

    Comment commentResponse;
    String commentRequestJson;
    String commentResponseJson;

    @MockBean
    CommentService commentService;

    @BeforeAll
    void setUp() {
        Comment commentRequest = Comment.builder()
                .content("anyText")
                .build();

        commentResponse = convertToNewObject(commentRequest, Comment.class);
        commentResponse.setId(randomUUID());
        commentResponse.setUser(UserSnippet.builder().id(randomUUID()).build());

        commentRequestJson = jsonStringFromObject(commentRequest);
        commentResponseJson = jsonStringFromObject(commentResponse);
    }

    @Test
    void testGetComment() throws Exception {

        UUID momentId = randomUUID();
        Comment comment = this.commentResponse;
        when(commentService.getComment(any())).thenReturn(comment);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/ht/moments/{momentId}/comments/{commentId}", momentId, comment.getId()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(commentResponseJson))
                .andDo(document("get-comment",
                        resource("Get a comment's details")))
                .andReturn();
    }

    @Test
    void testGetAllCommentsForMoment() throws Exception {

        Comment comment = this.commentResponse;
        when(commentService.getAllCommentsForMoment(any())).thenReturn(List.of(comment));
        UUID momentId = randomUUID();

        List<Comment> commentList = new ArrayList<>();
        commentList.add(commentResponse);

        String jsonResponse = jsonStringFromObject(commentList);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/ht/moments/{momentId}/comments", momentId))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(jsonResponse))
                .andDo(document("get-comments-for-moment",
                        resource("Get a list of comments for a moment")))
                .andReturn();
    }

    @Test
    void testCreateComment() throws Exception {

        Comment comment = commentResponse;
        UUID momentId = randomUUID();
        when(commentService.createComment(any(), any())).thenReturn(comment);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/ht/moments/{momentId}/comments", momentId)
                .content(commentRequestJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(commentResponseJson))
                .andDo(document("create-comment",
                        resource("Create a comment for a moment")))
                .andReturn();
    }

    @Test
    void testUpdateComment() throws Exception {

        Comment comment = commentResponse;
        when(commentService.updateComment(any(), any())).thenReturn(comment);
        UUID momentId = randomUUID();

        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/v1/ht/moments/{momentId}/comments/{commentId}", momentId, comment.getId())
                .content(commentRequestJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(commentResponseJson))
                .andDo(document("update-comment",
                        resource("Update a comment's details")))
                .andReturn();
    }

    @Test
    void testDeleteComment() throws Exception {

        Comment comment = commentResponse;
        UUID momentId = randomUUID();
        when(commentService.getComment(any())).thenReturn(comment);

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/ht/moments/{momentId}/comments/{commentId}", momentId, comment.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .param("commentId", String.valueOf(comment.getId())))
                .andExpect(status().is2xxSuccessful())
                .andDo(document("delete-comment",
                        resource("Delete a comment")))
                .andReturn();
    }

    @Test
    void testCreateReplyToComment() throws Exception {

        Comment comment = commentResponse;
        UUID momentId = randomUUID();
        when(commentService.replyToComment(any(), any())).thenReturn(comment);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/ht/moments/{momentId}/comments/{commentId}/replies", momentId, comment.getId())
                .content(commentRequestJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(commentResponseJson))
                .andDo(document("create-reply-comment",
                        resource("Create a reply for a comment")))
                .andReturn();
    }

    @Test
    void testCreateReplyToComment_ParentCommentDoesNotBelongToUser_ReturnsForbiddenStatusCode() throws Exception {

        Comment comment = commentResponse;
        UUID momentId = randomUUID();
        when(commentService.replyToComment(any(), any())).thenThrow(EntityDoesNotBelongToUserException.class);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/ht/moments/{momentId}/comments/{commentId}/replies", momentId, comment.getId())
                .content(commentRequestJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(document("create-reply-comment-error-when-user-not-found",
                        resource("Create a reply for a comment returns error when user not found")))
                .andReturn();
    }

    @Test
    void testCreateReplyToComment_ParentCommentNotFound_ReturnsFoundStatusCode() throws Exception {

        Comment comment = commentResponse;
        UUID momentId = randomUUID();
        when(commentService.replyToComment(any(), any())).thenThrow(CommentNotFoundException.class);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/ht/moments/{momentId}/comments/{commentId}/replies", momentId, comment.getId())
                .content(commentRequestJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(document("create-reply-comment-error-when-user-not-found",
                        resource("Create a reply for a comment returns error when user not found")))
                .andReturn();
    }

    @Test
    void testGetRepliesForComment() throws Exception {

        Comment comment = commentResponse;
        UUID momentId = randomUUID();
        List<Comment> commentList = List.of(comment);
        String commentListJson = jsonStringFromObject(commentList);

        when(commentService.getRepliesForComment(any(), any())).thenReturn(commentList);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/ht/moments/{momentId}/comments/{commentId}/replies", momentId, comment.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(commentListJson))
                .andDo(document("get-replies-comment",
                        resource("Get the replies for a comment")))
                .andReturn();
    }

    @Test
    void testGetRepliesForComment_ParentCommentDoesNotBelongToUser_ReturnsForbiddenStatusCode() throws Exception {

        Comment comment = commentResponse;
        UUID momentId = randomUUID();

        when(commentService.getRepliesForComment(any(), any())).thenThrow(EntityDoesNotBelongToUserException.class);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/ht/moments/{momentId}/comments/{commentId}/replies", momentId, comment.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(document("get-replies-comment-not-found-exception-when-user-does-not-exist",
                        resource("Getting replies for a comment throws not found when the user does not exist")))
                .andReturn();
    }

    @Test
    void testGetRepliesForComment_ParentCommentDoesNotExist_Returns404NotFound() throws Exception {

        Comment comment = commentResponse;
        UUID momentId = randomUUID();

        when(commentService.getRepliesForComment(any(), any())).thenThrow(CommentNotFoundException.class);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/ht/moments/{momentId}/comments/{commentId}/replies", momentId, comment.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(document("get-replies-comment-not-found-exception-when-user-does-not-exist",
                        resource("Getting replies for a comment throws not found when the user does not exist")))
                .andReturn();
    }
}
