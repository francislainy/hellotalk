package com.example.hellotalk.controller;

import com.example.hellotalk.exception.UserDoesNotExistExistException;
import com.example.hellotalk.model.user.HobbyAndInterest;
import com.example.hellotalk.model.user.Hometown;
import com.example.hellotalk.model.user.User;
import com.example.hellotalk.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private User user;

    @BeforeAll
    void initData() {
        userId = UUID.fromString("1bfff94a-b70e-4b39-bd2a-be1c0f898589");

        Hometown hometown = Hometown.builder().city("anyCity").country("anyCountry").build();
        HobbyAndInterest hobbyAndInterest = HobbyAndInterest.builder()
                .title("anyInterest")
                .build();
        Set<HobbyAndInterest> hobbyAndInterests = new HashSet<>();
        hobbyAndInterests.add(hobbyAndInterest);

        user = User.builder()
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
                .hometown(hometown)
                .hobbyAndInterests(hobbyAndInterests)
                .build();
    }

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext,
            RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation)).alwaysDo(document("{method-name}",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())))
                .build();
    }

    @Autowired MockMvc mockMvc;

    @MockBean UserService userService;

    @Test
    void testGetUser() throws Exception {

        when(userService.getUser(userId)).thenReturn(user);

        mockMvc.perform(get("/api/v1/ht/user/{userId}", userId))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void testCreateUser() throws Exception {

        String json = jsonStringFromObject(user);
        ObjectMapper objectMapper = new ObjectMapper();
        User userWithId = objectMapper.readValue(json, User.class);
        userWithId.setId(userId);

        String jsonWithId = jsonStringFromObject(userWithId);

        when(userService.createUser(any())).thenReturn(userWithId);

        mockMvc.perform(post("/api/v1/ht/user").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(jsonWithId));
    }

    @Test
    void testUpdateUser() throws Exception {

        String json = jsonStringFromObject(user);
        User userWithId = convertToNewObject(user, User.class);
        userWithId.setId(userId);

        String jsonWithId = jsonStringFromObject(userWithId);

        when(userService.updateUser(any(), any())).thenReturn(userWithId);

        mockMvc.perform(put("/api/v1/ht/user/{userId}", userId)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(jsonWithId));
    }

    @Test
    void testUpdateUser_ThrowsExceptionWhenUserDoesNotExist() throws Exception {

        String json = jsonStringFromObject(user);

        when(userService.updateUser(any(), any())).thenThrow(new UserDoesNotExistExistException("No user found with this id"));

        mockMvc.perform(put("/api/v1/ht/user/{userId}", userId)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(204))
                .andExpect(content().string("No user found with this id"));
    }

    @Test
    void testDeleteUser() throws Exception {

        when(userService.getUser(any())).thenReturn(user);

        mockMvc.perform(delete("/api/v1/ht/user/{userId}", userId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void testDeleteUser_ThrowsExceptionWhenUserNotFound() throws Exception {

        doThrow(new UserDoesNotExistExistException("No user found with this id")).when(userService).deleteUser(any());

        mockMvc.perform(delete("/api/v1/ht/user/{userId}", userId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("No user found with this id"));
    }

}
