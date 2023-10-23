package com.example.hellotalk.controller.moment;

import com.example.hellotalk.config.BaseDocTestConfig;
import com.example.hellotalk.entity.moment.MomentEntity;
import com.example.hellotalk.entity.user.LikeEntity;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.exception.EntityBelongsToUserException;
import com.example.hellotalk.exception.MomentAlreadyLikedException;
import com.example.hellotalk.exception.MomentNotFoundException;
import com.example.hellotalk.exception.UserNotFoundException;
import com.example.hellotalk.model.ResultInfo;
import com.example.hellotalk.model.moment.Moment;
import com.example.hellotalk.service.moment.MomentService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.example.hellotalk.exception.AppExceptionHandler.MOMENT_NOT_FOUND_EXCEPTION;
import static com.example.hellotalk.util.Utils.convertToNewObject;
import static com.example.hellotalk.util.Utils.jsonStringFromObject;
import static java.util.UUID.randomUUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MomentController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MomentControllerTest extends BaseDocTestConfig {

    UUID momentId;
    Moment momentResponse;
    String jsonRequest;
    String jsonResponse;

    @MockBean
    MomentService momentService;

    @BeforeAll
    void initData() {
        momentId = randomUUID();
        UUID userId = randomUUID();

        Moment momentRequest = Moment.builder()
                .content("anyText")
                .build();

        jsonRequest = jsonStringFromObject(momentRequest);
        momentResponse = convertToNewObject(momentRequest, Moment.class);
        momentResponse.setId(momentId);
        momentResponse.setUserId(userId);

        jsonResponse = jsonStringFromObject(momentResponse);
    }

    @Test
    void testGetMoment() throws Exception {

        Moment moment = this.momentResponse;
        when(momentService.getMoment(any())).thenReturn(moment);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/ht/moments/{momentId}/", momentId)
                .header("authorization", "anyValidUUID"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(jsonResponse))
                .andDo(document("get-moment",
                        resource("Get a moment's details")))
                .andReturn();
    }

    @Test
    void testGetAllMoments() throws Exception {

        Moment moment = this.momentResponse;
        when(momentService.getAllMoments()).thenReturn(List.of(moment));

        List<Moment> momentList = new ArrayList<>();
        momentList.add(momentResponse);

        String jsonResponse = jsonStringFromObject(momentList);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/ht/moments/"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(jsonResponse))
                .andDo(document("get-moments",
                        resource("Get a list of moments")))
                .andReturn();
    }

    @Test
    void testGetAllMomentsForUser() throws Exception {

        Moment moment = this.momentResponse;
        UUID userId = randomUUID();
        when(momentService.getAllMomentsForUser(any())).thenReturn(List.of(moment));

        List<Moment> momentList = new ArrayList<>();
        momentList.add(momentResponse);

        String jsonResponse = jsonStringFromObject(momentList);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/ht/moments/user/")
                .queryParam("userId", String.valueOf(userId)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(jsonResponse))
                .andDo(document("get-moments-for-user",
                        resource("Get a list of moments for a user")))
                .andReturn();
    }

    @Test
    void testCreateMoment() throws Exception {

        Moment moment = momentResponse;
        when(momentService.createMoment(any())).thenReturn(moment);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/ht/moments/")
                .header("authorization", "anyValidUUID")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(jsonResponse))
                .andDo(document("create-moment",
                        resource("Create a moment")))
                .andReturn();
    }

    @Test
    void testUpdateMoment() throws Exception {

        Moment moment = momentResponse;
        when(momentService.updateMoment(any(), any())).thenReturn(moment);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/v1/ht/moments/{momentId}", momentId)
                .header("authorization", "anyValidUUID")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(jsonResponse))
                .andDo(document("update-moment",
                        resource("Update a moment's details")))
                .andReturn();
    }

    @Test
    void testUpdateMoment_ThrowsExceptionWhenUserDoesNotExist() throws Exception {

        when(momentService.updateMoment(any(), any())).thenThrow(new MomentNotFoundException(MOMENT_NOT_FOUND_EXCEPTION));

        String jsonError = """
                {"message": "jsonError"}
                """;
        jsonError = jsonError.replace("jsonError", MOMENT_NOT_FOUND_EXCEPTION);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/v1/ht/moments/{momentId}", momentId)
                        .header("authorization", "anyValidUUID")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("momentId", String.valueOf(momentId)))
                .andExpect(status().is(404))
                .andExpect(content().json(jsonError))
                .andDo(document("update-moment-throws-exception",
                        resource("Updating a moment's details throws exception when moment does not exist")))
                .andReturn();
    }

    @Test
    void testDeleteMoment() throws Exception {

        Moment moment = momentResponse;
        when(momentService.getMoment(any())).thenReturn(moment);

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/ht/moments/{momentId}", momentId)
                .contentType(MediaType.APPLICATION_JSON)
                .param("momentId", String.valueOf(momentId)))
                .andExpect(status().is2xxSuccessful())
                .andDo(document("delete-moment",
                        resource("Delete a moment")))
                .andReturn();
    }

    @Test
    void testDeleteMoment_ThrowsExceptionWhenMomentNotFound() throws Exception { // todo: to find out why this gets printed before the delete exception on swagger - 04/02/2023

        doThrow(new MomentNotFoundException(MOMENT_NOT_FOUND_EXCEPTION)).when(momentService).deleteMoment(any());
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/ht/moments/{momentId}", momentId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andDo(document("delete-moment-throws-exception",
                        resource("Deleting a moment throws exception when moment does not exist")))
                .andReturn();
    }

    @Test
    void testLikeMoment() throws Exception {

        MomentEntity momentEntity = MomentEntity.builder().id(randomUUID()).build();
        UserEntity userEntity = UserEntity.builder().id(randomUUID()).build();
        LikeEntity likeEntity = LikeEntity.builder()
                .id(randomUUID())
                .userEntity(userEntity)
                .momentEntity(momentEntity)
                .build();

        ResultInfo resultInfo = ResultInfo.builder()
                .id(likeEntity.getId())
                .userId(likeEntity.getUserEntity().getId())
                .momentId(likeEntity.getMomentEntity().getId())
                .build();
        HashMap<String, Object> map = new HashMap<>();
        map.put("message", "Moment liked successfully");
        map.put("data", resultInfo);

        when(momentService.likeMoment(any())).thenReturn(map);

        String jsonResponse = jsonStringFromObject(map);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/v1/ht/moments/{momentId}/like", momentEntity.getId()).content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(jsonResponse))
                .andDo(document("like-moment",
                        resource("Like a moment")))
                .andReturn();
    }

    @Test
    void testUnlikeMoment() throws Exception {

        MomentEntity momentEntity = MomentEntity.builder().id(randomUUID()).build();
        UserEntity userEntity = UserEntity.builder().id(randomUUID()).build();
        LikeEntity likeEntity = LikeEntity.builder()
                .id(randomUUID())
                .userEntity(userEntity)
                .momentEntity(momentEntity)
                .build();

        ResultInfo resultInfo = ResultInfo.builder()
                .id(likeEntity.getId())
                .userId(likeEntity.getUserEntity().getId())
                .momentId(likeEntity.getMomentEntity().getId())
                .build();
        HashMap<String, Object> map = new HashMap<>();
        map.put("message", "Moment unliked successfully");
        map.put("data", resultInfo);

        when(momentService.unlikeMoment(any())).thenReturn(map);

        String jsonResponse = jsonStringFromObject(map);

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/ht/moments/{momentId}/unlike", momentEntity.getId()).content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(jsonResponse))
                .andDo(document("unlike-moment",
                        resource("Unlike a moment")))
                .andReturn();
    }

    @Test
    void testLikeMoment_Throws409ErrorWhenMomentAlreadyLiked() throws Exception {

        UUID userId = randomUUID();
        UUID momentId = randomUUID();

        when(momentService.likeMoment(any())).thenThrow(MomentAlreadyLikedException.class);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/v1/ht/moments/{momentId}/like", userId, momentId).content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andDo(document("like-moment-throws-409-exception-when-moment-already-liked",
                        resource("Liking a moment throws exception when a moment has already been liked before")))
                .andReturn();
    }

    @Test
    void testLikeMoment_ThrowsNotFoundErrorWhenMomentDoesNotExist() throws Exception {

        UUID userId = randomUUID();
        UUID momentId = randomUUID();

        when(momentService.likeMoment(any())).thenThrow(MomentNotFoundException.class);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/v1/ht/users/{userId}/like/{momentId}", userId, momentId).content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(document("like-moment-throws-not-found-exception-when-moment-does-not-exist",
                        resource("Liking a moment throws exception when the moment does not exist")))
                .andReturn();
    }

    @Test
    void testLikeMoment_ThrowsNotFoundExceptionWhenUserDoesNotExist() throws Exception {

        UUID userId = randomUUID();
        UUID momentId = randomUUID();

        when(momentService.likeMoment(any())).thenThrow(UserNotFoundException.class);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/v1/ht/users/{userId}/like/{momentId}", userId, momentId).content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(document("like-moment-throws-not-found-exception-when-user-does-not-exist",
                        resource("Liking a moment throws exception when the user does not exist")))
                .andReturn();
    }

    @Test
    void testLikeMoment_ThrowsNotFoundExceptionWhenUserIsTryingToLikeTheirOwnMoment() throws Exception {

        UUID userId = randomUUID();
        UUID momentId = randomUUID();

        when(momentService.likeMoment(any())).thenThrow(EntityBelongsToUserException.class);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/v1/ht/moments/{momentId}/like", userId, momentId).content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(document("like-moment-throws-exception-when-user-tries-to-like-their-own-moment",
                        resource("Liking a moment throws exception when it belongs to the same user who is doing the liking")))
                .andReturn();
    }
}
