package com.example.hellotalk.controller;

import capital.scalable.restdocs.AutoDocumentation;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.model.user.FollowingRequest;
import com.example.hellotalk.repository.UserRepository;
import com.example.hellotalk.service.user.FollowingRequestService;
import com.example.hellotalk.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.cli.CliDocumentation;
import org.springframework.restdocs.http.HttpDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

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
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FollowingRequestController.class)
@ExtendWith(MockitoExtension.class)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FollowingRequestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    FollowingRequestService followingRequestService;

    @MockBean
    UserService userService;

    @MockBean
    UserRepository userRepository;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation)).alwaysDo(document("{method-name}",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())))
                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentation)
                        .uris()
                        .withScheme("http")
                        .withHost("localhost")
                        .withPort(8080)
                        .and().snippets()
                        .withDefaults(CliDocumentation.curlRequest(),
                                HttpDocumentation.httpRequest(),
                                HttpDocumentation.httpResponse(),
                                AutoDocumentation.requestFields(),
                                AutoDocumentation.responseFields(),
                                AutoDocumentation.pathParameters(),
                                AutoDocumentation.requestParameters(),
                                AutoDocumentation.description(),
                                AutoDocumentation.methodAndPath(),
                                AutoDocumentation.section()))
                .alwaysDo(document("{class-name}/{method-name}",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())))
                .build();
    }

    @Test
    void testGetFollowingRequest() throws Exception {

        FollowingRequest followingRequest = FollowingRequest.builder()
                .id(randomUUID())
                .userFromId(randomUUID())
                .userToId(randomUUID())
                .build();

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

        FollowingRequest followingRequest = FollowingRequest.builder()
                .id(randomUUID())
                .userFromId(randomUUID())
                .userToId(randomUUID())
                .build();
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

        FollowingRequest followingRequest = FollowingRequest.builder()
                .id(randomUUID())
                .userFromId(randomUUID())
                .userToId(randomUUID())
                .build();
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
    void testCreateFollowingRequest() throws Exception {

        UUID userToId = randomUUID();
        UUID userFromId = randomUUID();

        FollowingRequest followingRequest = FollowingRequest.builder()
                .userToId(userToId)
                .userFromId(userFromId)
                .build();

        FollowingRequest followingResponse = FollowingRequest.builder()
                .id(userToId)
                .userToId(userToId)
                .userFromId(userFromId)
                .build();

        UserEntity userFromEntity = UserEntity.builder().id(userFromId).build();
        UserEntity userToEntity = UserEntity.builder().id(userToId).build();

        when(userRepository.findById(userFromEntity.getId())).thenReturn(Optional.of(userFromEntity));
        when(userRepository.findById(userToEntity.getId())).thenReturn(Optional.of(userToEntity));
        when(followingRequestService.createFollowingRequest(any())).thenReturn(followingResponse);

        String jsonRequest = jsonStringFromObject(followingRequest);
        String jsonResponse = jsonStringFromObject(followingResponse);
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/ht/follow").content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(jsonResponse))
                .andDo(document("create-following-request-for-user",
                        resource("Create a following request for a user")))
                .andReturn();
    }
}
