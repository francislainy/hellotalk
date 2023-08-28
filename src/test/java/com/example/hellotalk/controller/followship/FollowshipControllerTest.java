package com.example.hellotalk.controller.followship;

import com.example.hellotalk.config.BaseDocTestConfig;
import com.example.hellotalk.exception.FollowshipDeletedException;
import com.example.hellotalk.exception.FollowshipNotCreatedUserCantFollowThemselfException;
import com.example.hellotalk.model.followship.Followship;
import com.example.hellotalk.service.followship.FollowshipService;
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
import static com.example.hellotalk.exception.AppExceptionHandler.FOLLOWSHIP_NOT_CREATED_USER_CANT_FOLLOW_THEMSELF;
import static com.example.hellotalk.util.Utils.jsonStringFromObject;
import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = FollowshipController.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FollowshipControllerTest extends BaseDocTestConfig {

    @MockBean
    private FollowshipService followshipService;

    @Test
    void testGetFollowship() throws Exception {
        Followship followship = Followship.builder().id(randomUUID()).userFromId(randomUUID()).userToId(randomUUID()).build();
        when(followshipService.getFollowship(any())).thenReturn(followship);

        String jsonResponse = jsonStringFromObject(followship);
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/ht/followship/{followshipId}", randomUUID()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(jsonResponse))
                .andDo(document("get-followship",
                        resource("Get a followship")))
                .andReturn();
    }

    @Test
    void testGetAllFollowships() throws Exception {
        Followship followship = Followship.builder().build();
        when(followshipService.getAllFollowships()).thenReturn(List.of(followship));

        List<Followship> followshipList = new ArrayList<>();
        followshipList.add(followship);

        String jsonResponse = jsonStringFromObject(followshipList);
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/ht/followship/"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(jsonResponse))
                .andDo(document("get-followships",
                        resource("Get a list of followships")))
                .andReturn();
    }

    @Test
    void testGetAllFollowshipsFromUser() throws Exception {
        Followship followship = Followship.builder().id(randomUUID()).userFromId(randomUUID()).userToId(randomUUID()).build();
        when(followshipService.getAllFollowshipsFromUser(any())).thenReturn(List.of(followship));

        List<Followship> followshipList = new ArrayList<>();
        followshipList.add(followship);

        String jsonResponse = jsonStringFromObject(followshipList);
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/ht/followship/from/user/{userId}", randomUUID()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(jsonResponse))
                .andDo(document("get-all-followships-from-user",
                        resource("Get a list of followships from a given user")))
                .andReturn();
    }

    @Test
    void testGetAllFollowshipsToUser() throws Exception {
        Followship followship = Followship.builder().id(randomUUID()).userFromId(randomUUID()).userToId(randomUUID()).build();
        when(followshipService.getAllFollowshipsToUser(any())).thenReturn(List.of(followship));

        List<Followship> followshipList = new ArrayList<>();
        followshipList.add(followship);

        String jsonResponse = jsonStringFromObject(followshipList);
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/ht/followship/to/user/{userId}", randomUUID()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(jsonResponse))
                .andDo(document("get-all-followships-to-user",
                        resource("Get a list of followships to a given user")))
                .andReturn();
    }

    @Test
    void testCreateFollowship_WhenFollowingRelationshipDoesNotYetExist_ReturnsSuccess() throws Exception {
        UUID userToId = randomUUID();
        UUID userFromId = randomUUID();

        Followship followshipRequest = Followship.builder().userToId(userToId).userFromId(userFromId).build();
        Followship followshipResponse = Followship.builder().id(userToId).userToId(userToId).userFromId(userFromId).build();

        when(followshipService.createFollowship(any())).thenReturn(followshipResponse);

        String jsonRequest = jsonStringFromObject(followshipRequest);
        String jsonResponse = jsonStringFromObject(followshipResponse);
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/ht/followship/").content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(jsonResponse))
                .andDo(document("create-new-followship",
                        resource("Create a new followship")))
                .andReturn();
    }

    @Test
    void testCreateFollowship_WhenFollowingRelationDeletedExceptionIsThrown_ReturnsSuccess() throws Exception {
        UUID userToId = randomUUID();
        UUID userFromId = randomUUID();
        Followship followship = Followship.builder().userToId(userToId).userFromId(userFromId).build();

        when(followshipService.createFollowship(any())).thenThrow(FollowshipDeletedException.class);

        String jsonRequest = jsonStringFromObject(followship);
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/ht/followship/").content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andDo(document("unfollow-user-by-deleting-existing-followship",
                        resource("Unfollow a user by deleting existing followship")))
                .andReturn();
    }

    @Test
    void testCreateFollowship_FollowingRelationshipNotCreatedException_Returns400BadRequestError() throws Exception {

        UUID userToId = randomUUID();
        UUID userFromId = randomUUID();

        Followship followship = Followship.builder().userToId(userToId).userFromId(userFromId).build();

        when(followshipService.createFollowship(any())).thenThrow(new FollowshipNotCreatedUserCantFollowThemselfException(FOLLOWSHIP_NOT_CREATED_USER_CANT_FOLLOW_THEMSELF));

        String jsonRequest = jsonStringFromObject(followship);
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/ht/followship/").content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(FOLLOWSHIP_NOT_CREATED_USER_CANT_FOLLOW_THEMSELF)))
                .andDo(document("create-followship-fails-when-sender-and-receiver-users-are-the-same",
                        resource("Create a followship for a user to follow themself fails")))
                .andReturn();
    }

}
