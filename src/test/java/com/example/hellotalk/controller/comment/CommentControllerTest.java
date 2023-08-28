package com.example.hellotalk.controller.comment;

import com.example.hellotalk.config.BaseDocTestConfig;
import com.example.hellotalk.model.comment.Comment;
import com.example.hellotalk.model.user.UserSmall;
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

    UUID commentId;
    UUID userCreatorId;
    Comment commentRequest;
    Comment commentResponse;
    String jsonRequest;
    String jsonResponse;

    @MockBean
    CommentService commentService;

    @BeforeAll
    void initData() {
        commentId = randomUUID();
        userCreatorId = randomUUID();

        commentRequest = Comment.builder()
                .content("anyText")
                .build();

        jsonRequest = jsonStringFromObject(commentRequest);
        commentResponse = convertToNewObject(commentRequest, Comment.class);
        commentResponse.setId(commentId);

        commentResponse.setUser(UserSmall.builder().id(userCreatorId).build());

        jsonResponse = jsonStringFromObject(commentResponse);
    }

    @Test
    void testGetComment() throws Exception {

        UUID momentId = randomUUID();
        Comment comment = this.commentResponse;
        when(commentService.getComment(any())).thenReturn(comment);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/ht/moments/{momentId}/comments/{commentId}", momentId, commentId))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(jsonResponse))
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
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(jsonResponse))
                .andDo(document("create-comment",
                        resource("Create a comment for a moment")))
                .andReturn();
    }

    @Test
    void testUpdateComment() throws Exception {

        Comment comment = commentResponse;
        when(commentService.updateComment(any(), any())).thenReturn(comment);
        UUID momentId = randomUUID();

        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/v1/ht/moments/{momentId}/comments/{commentId}", momentId, commentId)
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(jsonResponse))
                .andDo(document("update-comment",
                        resource("Update a comment's details")))
                .andReturn();
    }

    @Test
    void testDeleteComment() throws Exception {

        Comment comment = commentResponse;
        UUID momentId = randomUUID();
        when(commentService.getComment(any())).thenReturn(comment);

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/ht/moments/{momentId}/comments/{commentId}", momentId, commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .param("commentId", String.valueOf(commentId)))
                .andExpect(status().is2xxSuccessful())
                .andDo(document("delete-comment",
                        resource("Delete a comment")))
                .andReturn();
    }
}
