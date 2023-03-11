package com.example.hellotalk.controller;

import com.example.hellotalk.config.WebConfig;
import com.example.hellotalk.exception.UserNotFoundException;
import com.example.hellotalk.model.HobbyAndInterest;
import com.example.hellotalk.model.Hometown;
import com.example.hellotalk.model.user.User;
import com.example.hellotalk.service.user.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import java.util.*;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.example.hellotalk.exception.AppExceptionHandler.USER_NOT_FOUND_EXCEPTION;
import static com.example.hellotalk.util.Utils.convertToNewObject;
import static com.example.hellotalk.util.Utils.jsonStringFromObject;
import static java.util.UUID.randomUUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@Import(WebConfig.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerTest extends BaseTestConfig {

    UUID userId;
    User userRequest;
    User userResponse;
    String jsonRequest;
    String jsonResponse;

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

    @Test
    void testGetUser() throws Exception {

        User user = this.userResponse;
        when(userService.getUser(userId)).thenReturn(user);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/ht/users/{userId}", userId))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(jsonResponse))
                .andDo(document("get-user",
                        resource("Get a user's details")))
                .andReturn();
    }

    @Test
    void testGetAllUsers() throws Exception {

        User user = this.userResponse;
        when(userService.getAllUsers()).thenReturn(List.of(user));

        List<User> userList = new ArrayList<>();
        userList.add(userResponse);

        String jsonResponse = jsonStringFromObject(userList);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/ht/users/"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(jsonResponse))
                .andDo(document("get-users",
                        resource("Get a list of users")))
                .andReturn();
    }

    @Test
    void testCreateUser() throws Exception {

        User user = userResponse;
        when(userService.createUser(any())).thenReturn(user);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/ht/users/").content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(jsonResponse))
                .andDo(document("create-user",
                        resource("Create a user")))
                .andReturn();
    }

    @Test
    void testUpdateUser() throws Exception {

        User user = userResponse;
        when(userService.updateUser(any(), any())).thenReturn(user);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/v1/ht/users/{userId}", userId)
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(jsonResponse))
                .andDo(document("update-user",
                        resource("Update a user's details")))
                .andReturn();
    }

    @Test
    void testUpdateUser_ThrowsExceptionWhenUserDoesNotExist() throws Exception {

        when(userService.updateUser(any(), any())).thenThrow(new UserNotFoundException(USER_NOT_FOUND_EXCEPTION));

        String jsonError = """
                {"message": "jsonError"}
                """;
        jsonError = jsonError.replace("jsonError", USER_NOT_FOUND_EXCEPTION);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/v1/ht/users/{userId}", userId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().is(404))
                .andExpect(content().json(jsonError))
                .andDo(document("update-user-throws-exception-when-user-does-not-exist",
                        resource("Updating a user's details throws exception when user does not exist")))
                .andReturn();
    }

    @Test
    void testDeleteUser() throws Exception {

        String json = """
                {"message": "User Deleted"}
                """;
        User user = userResponse;
        when(userService.getUser(any())).thenReturn(user);
        when(userService.deleteUser(any())).thenReturn(json);

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/ht/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(json))
                .andDo(document("delete-user",
                        resource("Delete a user")))
                .andReturn();
    }

    @Test
    void testDeleteUser_ThrowsExceptionWhenUserNotFound() throws Exception { //todo: need to find out why this gets printed before the delete exception on swagger - 04/02/2023

        doThrow(new UserNotFoundException(USER_NOT_FOUND_EXCEPTION)).when(userService).deleteUser(any());

        String jsonError = """
                {"message": "jsonError"}
                """;
        jsonError = jsonError.replace("jsonError", USER_NOT_FOUND_EXCEPTION);

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/ht/users/{userId}", userId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(content().json(jsonError))
                .andDo(document("delete-user-throws-exception-when-user-not-found",
                        resource("Deleting a user throws exception when user is not found")))
                .andReturn();
    }
}
