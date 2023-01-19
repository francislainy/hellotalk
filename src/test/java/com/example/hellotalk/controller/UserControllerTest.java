package com.example.hellotalk.controller;

import capital.scalable.restdocs.AutoDocumentation;
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
import org.springframework.restdocs.cli.CliDocumentation;
import org.springframework.restdocs.http.HttpDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;

import static com.example.hellotalk.exception.AppExceptionHandler.USER_NOT_FOUND_EXCEPTION;
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

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

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
    void testGetUser() throws Exception {

        User user = this.userResponse;
        when(userService.getUser(userId)).thenReturn(user);

        mockMvc.perform(get("/api/v1/ht/user/{userId}", userId))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(jsonResponse));
    }

    @Test
    void testGetAllUsers() throws Exception {

        User user = this.userResponse;
        when(userService.getAllUsers()).thenReturn(List.of(user));

        List<User> userList = new ArrayList<>();
        userList.add(userResponse);

        String jsonResponse = jsonStringFromObject(userList);

        mockMvc.perform(get("/api/v1/ht/user/"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(jsonResponse));
    }

    @Test
    void testCreateUser() throws Exception {

        User user = userResponse;
        when(userService.createUser(any())).thenReturn(user);

        mockMvc.perform(post("/api/v1/ht/user").content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(jsonResponse));
    }

    @Test
    void testUpdateUser() throws Exception {

        User user = userResponse;
        when(userService.updateUser(any(), any())).thenReturn(user);

        mockMvc.perform(put("/api/v1/ht/user/{userId}", userId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(jsonResponse));
    }

    @Test
    void testUpdateUser_ThrowsExceptionWhenUserDoesNotExist() throws Exception {

        when(userService.updateUser(any(), any())).thenThrow(new UserNotFoundException(USER_NOT_FOUND_EXCEPTION));

        String jsonError = """
                {"message": "jsonError"}
                """;
        jsonError = jsonError.replace("jsonError", USER_NOT_FOUND_EXCEPTION);

        mockMvc.perform(put("/api/v1/ht/user/{userId}", userId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404))
                .andExpect(content().json(jsonError));
    }

    @Test
    void testDeleteUser() throws Exception {

        User user = userResponse;
        when(userService.getUser(any())).thenReturn(user);

        mockMvc.perform(delete("/api/v1/ht/user/{userId}", userId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void testDeleteUser_ThrowsExceptionWhenUserNotFound() throws Exception {

        doThrow(new UserNotFoundException(USER_NOT_FOUND_EXCEPTION)).when(userService).deleteUser(any());

        String jsonError = """
                {"message": "jsonError"}
                """;
        jsonError = jsonError.replace("jsonError", USER_NOT_FOUND_EXCEPTION);

        mockMvc.perform(delete("/api/v1/ht/user/{userId}", userId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(content().json(jsonError
                ));
    }

}
