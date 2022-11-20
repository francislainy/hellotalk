package com.example.hellotalk.controller;

import com.example.hellotalk.exception.UserNotFoundException;
import com.example.hellotalk.model.user.HobbyAndInterest;
import com.example.hellotalk.model.user.Hometown;
import com.example.hellotalk.model.user.User;
import com.example.hellotalk.service.user.UserService;
import org.junit.jupiter.api.BeforeAll;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.example.hellotalk.util.Utils.convertToNewObject;
import static com.example.hellotalk.util.Utils.jsonStringFromObject;
import static java.util.UUID.randomUUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@ExtendWith(MockitoExtension.class)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerTest {

    private UUID userId;
    private User userRequest;
    private User userResponse;
    private String jsonRequest;
    private String jsonResponse;

    @Autowired MockMvc mockMvc;

    @MockBean UserService userService;

    @BeforeAll
    void initData() {
        userId = UUID.fromString("1bfff94a-b70e-4b39-bd2a-be1c0f898589");

        Hometown hometownRequest = Hometown.builder().city("anyCity").country("anyCountry").build();
        Hometown hometownResponse = convertToNewObject(hometownRequest, Hometown.class);
        hometownResponse.setId(randomUUID());
        HobbyAndInterest hobbyAndInterestRequest = HobbyAndInterest.builder().title("anyInterest").build();
        Set<HobbyAndInterest> hobbyAndInterestsRequest = new HashSet<>();
        hobbyAndInterestsRequest.add(hobbyAndInterestRequest);

        HobbyAndInterest hobbyAndInterestResponse = convertToNewObject(hobbyAndInterestRequest, HobbyAndInterest.class);
        hobbyAndInterestResponse.setId(randomUUID());
        Set<HobbyAndInterest> hobbyAndInterestsResponse = new HashSet<>();
        hobbyAndInterestsResponse.add(hobbyAndInterestResponse);

        userRequest = User.builder()
                .name("anyName")
                .dob("anyDob")
                .gender("anyGender")
                .subscriptionType("anySubscriptionType")
                .creationDate("anyCreationDate")
                .handle("anyHandle")
                .status("anyStatus")
                .nativeLanguage("anyNativeLanguage")
                .targetLanguage("anyTargetLanguage")
                .selfIntroduction("anySelfIntroduction")
                .occupation("anyOccupation")
                .placesToVisit("anyPlacesToVisit")
                .hometown(hometownRequest)
                .hobbyAndInterests(hobbyAndInterestsRequest)
                .build();

        jsonRequest = jsonStringFromObject(userRequest);
        userResponse = convertToNewObject(userRequest, User.class);
        userResponse.setId(userId);
        userResponse.setHometown(hometownResponse);
        userResponse.setHobbyAndInterests(hobbyAndInterestsResponse);

        jsonResponse = jsonStringFromObject(userResponse);
    }

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext,
            RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation)).alwaysDo(document("{method-name}",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())))
                .build();
    }

    @Test
    void testGetUser() throws Exception {

        when(userService.getUser(userId)).thenReturn(userResponse);

        mockMvc.perform(get("/api/v1/ht/user/{userId}", userId))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(jsonResponse));
    }

    @Test
    void testCreateUser() throws Exception {

        when(userService.createUser(any())).thenReturn(userResponse);

        mockMvc.perform(post("/api/v1/ht/user").content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(jsonResponse));
    }

    @Test
    void testUpdateUser() throws Exception {

        when(userService.updateUser(any(), any())).thenReturn(userResponse);

        mockMvc.perform(put("/api/v1/ht/user/{userId}", userId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(jsonResponse));
    }

    @Test
    void testUpdateUser_ThrowsExceptionWhenUserDoesNotExist() throws Exception {

        when(userService.updateUser(any(), any())).thenThrow(new UserNotFoundException("No user found with this id"));

        mockMvc.perform(put("/api/v1/ht/user/{userId}", userId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(204));
//                //                .andExpect(content().json("{\"error\": \"No user found with this id\"}"));
//                .andExpect(content().json("""
//                        {"error": "No user found with this id"}
//                        """));
    }

    @Test
    void testDeleteUser() throws Exception {

        when(userService.getUser(any())).thenReturn(userResponse);

        mockMvc.perform(delete("/api/v1/ht/user/{userId}", userId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void testDeleteUser_ThrowsExceptionWhenUserNotFound() throws Exception {

        doThrow(new UserNotFoundException("No user found with this id")).when(userService).deleteUser(any());

        mockMvc.perform(delete("/api/v1/ht/user/{userId}", userId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("No user found with this id"));
    }

}
