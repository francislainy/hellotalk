package com.example.hellotalk.controller.follow;

import com.example.hellotalk.controller.BaseTestConfig;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.exception.FollowingRelationshipDeletedException;
import com.example.hellotalk.model.FollowingRequest;
import com.example.hellotalk.repository.UserRepository;
import com.example.hellotalk.service.FollowingRequestService;
import com.example.hellotalk.service.user.UserService;
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
import java.util.Optional;
import java.util.UUID;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.example.hellotalk.util.Utils.jsonStringFromObject;
import static java.util.UUID.randomUUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FollowingRequestController.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FollowingRequestControllerTest extends BaseTestConfig {

    @MockBean
    FollowingRequestService followingRequestService;

    @MockBean
    UserService userService;

    @MockBean
    UserRepository userRepository;

    @Test
    void testGetFollowingRequest() throws Exception {
        FollowingRequest followingRequest = FollowingRequest.builder().id(randomUUID()).userFromId(randomUUID()).userToId(randomUUID()).build();
        when(followingRequestService.getFollowingRequest(any())).thenReturn(followingRequest);

        String jsonResponse = jsonStringFromObject(followingRequest);
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/ht/follow/{followingRequestId}", randomUUID()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(jsonResponse))
                .andDo(document("get-following-request",
                        resource("Get a following request")))
                .andReturn();
    }

    @Test
    void testGetAllFollowingRequests() throws Exception {
        FollowingRequest followingRequest = FollowingRequest.builder().build();
        when(followingRequestService.getAllFollowingRequests()).thenReturn(List.of(followingRequest));

        List<FollowingRequest> followingRequestList = new ArrayList<>();
        followingRequestList.add(followingRequest);

        String jsonResponse = jsonStringFromObject(followingRequestList);
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/ht/follow/"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(jsonResponse))
                .andDo(document("get-following-requests",
                        resource("Get a list of following requests")))
                .andReturn();
    }

    @Test
    void testGetAllFollowingRequestsFromUser() throws Exception {
        FollowingRequest followingRequest = FollowingRequest.builder().id(randomUUID()).userFromId(randomUUID()).userToId(randomUUID()).build();
        when(followingRequestService.getAllFollowingRequestsFromUser(any())).thenReturn(List.of(followingRequest));

        List<FollowingRequest> followingRequestList = new ArrayList<>();
        followingRequestList.add(followingRequest);

        String jsonResponse = jsonStringFromObject(followingRequestList);
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/ht/follow/from/user/{userId}", randomUUID()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(jsonResponse))
                .andDo(document("get-all-following-requests-from-user",
                        resource("Get a list of following requests from a given user")))
                .andReturn();
    }

    @Test
    void testGetAllFollowingRequestsToUser() throws Exception {
        FollowingRequest followingRequest = FollowingRequest.builder().id(randomUUID()).userFromId(randomUUID()).userToId(randomUUID()).build();
        when(followingRequestService.getAllFollowingRequestsToUser(any())).thenReturn(List.of(followingRequest));

        List<FollowingRequest> followingRequestList = new ArrayList<>();
        followingRequestList.add(followingRequest);

        String jsonResponse = jsonStringFromObject(followingRequestList);
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/ht/follow/to/user/{userId}", randomUUID()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(jsonResponse))
                .andDo(document("get-all-following-requests-to-user",
                        resource("Get a list of following requests to a given user")))
                .andReturn();
    }

    @Test
    void testCreateFollowingRequest_WhenRelationshipDoesNotYetExist_RelationshipCreatedAndSuccessReturned() throws Exception {
        UUID userToId = randomUUID();
        UUID userFromId = randomUUID();

        FollowingRequest followingRequest = FollowingRequest.builder().userToId(userToId).userFromId(userFromId).build();
        FollowingRequest followingResponse = FollowingRequest.builder().id(userToId).userToId(userToId).userFromId(userFromId).build();

        UserEntity userFromEntity = UserEntity.builder().id(userFromId).build();
        UserEntity userToEntity = UserEntity.builder().id(userToId).build();

        when(userRepository.findById(userFromEntity.getId())).thenReturn(Optional.of(userFromEntity));
        when(userRepository.findById(userToEntity.getId())).thenReturn(Optional.of(userToEntity));
        when(followingRequestService.createFollowingRequest(any())).thenReturn(followingResponse);

        String jsonRequest = jsonStringFromObject(followingRequest);
        String jsonResponse = jsonStringFromObject(followingResponse);
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/ht/follow/").content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(jsonResponse))
                .andDo(document("create-following-request-for-user-new-relationship",
                        resource("Create a following request for a user when the relationship does not yet exist")))
                .andReturn();
    }

    @Test
    void testCreateFollowingRequest_WhenRelationshipAlreadyExists_DeletesRelationshipAndReturnsSuccess() throws Exception {
        UUID userToId = randomUUID();
        UUID userFromId = randomUUID();

        FollowingRequest followingRequest = FollowingRequest.builder().userToId(userToId).userFromId(userFromId).build();

        UserEntity userFromEntity = UserEntity.builder().id(userFromId).build();
        UserEntity userToEntity = UserEntity.builder().id(userToId).build();

        when(userRepository.findById(userFromEntity.getId())).thenReturn(Optional.of(userFromEntity));
        when(userRepository.findById(userToEntity.getId())).thenReturn(Optional.of(userToEntity));
        when(followingRequestService.createFollowingRequest(any())).thenThrow(FollowingRelationshipDeletedException.class);

        String jsonRequest = jsonStringFromObject(followingRequest);
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/ht/follow/").content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isPartialContent())
                .andDo(document("create-following-request-for-user",
                        resource("Create a following request for a user")))
                .andReturn();
    }
}
