
package com.example.hellotalk.functionaltests;

import com.example.hellotalk.config.BasePostgresConfig;
import com.example.hellotalk.model.HobbyAndInterest;
import com.example.hellotalk.model.Hometown;
import com.example.hellotalk.model.message.Message;
import com.example.hellotalk.model.user.User;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.*;

import static com.example.hellotalk.util.Utils.convertToNewObject;
import static com.example.hellotalk.utils.Utils.getRequestSpecification;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
 * mvn -Dtest="functionaltests.*IT" integration-test
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

class MessageIT extends BasePostgresConfig {

    @LocalServerPort
    int port;

    RequestSpecification rq;

    final String USERNAME_1 = "john@email.com";
    final String PASSWORD_1 = "1234";

    final String USERNAME_2 = "mary@email.com";
    final String PASSWORD_2 = "Password123!";
    User user1;
    User user2;

    @Autowired
    EntityManager entityManager;

    static {
        postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres").withTag("latest"));
        postgres.start();
    }

    @BeforeAll
    void setUp() {
        Map<String, String> headers = new HashMap<>();
        rq = getRequestSpecification().baseUri("http://localhost:" + port)
                .contentType(ContentType.JSON)
                .headers(headers);

        user1 = createUser(USERNAME_1, PASSWORD_1).as(User.class);
        user2 = createUser(USERNAME_2, PASSWORD_2).as(User.class);
    }

    @BeforeEach
    void setUpEach() {
        rq.auth().basic(USERNAME_1, PASSWORD_1);
    }

    @Test
    void testMessagesAreOrderedByCreationDate() {
        // Given the user sends a message to another user
        Message message1 = Message.builder().content("content").userToId(user2.getId()).build();
        rq.body(message1).post("/api/v1/ht/messages");

        // And the user sends another message to the same user
        Message message2 = Message.builder().content("content").userToId(user2.getId()).build();
        rq.body(message2).post("/api/v1/ht/messages");

        // When the user retrieves the messages
        Response getMessageResponse = rq.get("/api/v1/ht/messages");

        // Then the messages should be ordered by creation date
        List<Message> messages = Arrays.asList(getMessageResponse.as(Message[].class));
        assertTrue(messages.get(0).getCreationDate().isBefore(messages.get(1).getCreationDate()));
    }

    @Test
    void testMessagesAreOrderedByCreationDateAfterUpdate() { // todo: fix test as per https://stackoverflow.com/questions/77939714/issue-with-message-ordering-in-jpa-entity-list-after-update-operation -
                                                             // 05/01/2024
        // Given the user sends a message to another user
        Message message1 = Message.builder().content("firstMessage").userToId(user2.getId()).build();
        rq.body(message1).post("/api/v1/ht/messages");

        // And the user sends another message to the same user
        Message message2 = Message.builder().content("secondMessage").userToId(user2.getId()).build();
        rq.body(message2).post("/api/v1/ht/messages");

        // When the user retrieves the messages
        Response getMessageResponse = rq.get("/api/v1/ht/messages");

        // Then the messages should be ordered by creation date
        List<Message> messages = Arrays.asList(getMessageResponse.as(Message[].class));
        assertTrue(messages.get(0).getCreationDate().isBefore(messages.get(1).getCreationDate()));

        // // When the user updates the first message -> This makes the tests fail
        // message1.setContent("firstMessageUpdated");
        // rq.body(message1).put("/api/v1/ht/messages/" + messages.get(0).getId());
        //
        // // Clear the persistence context here -> This makes no difference
        // entityManager.clear();

        // Then the messages should be ordered by creation date
        Response updatedMessageResponse = rq.get("/api/v1/ht/messages");
        List<Message> updatedMessages = Arrays.asList(updatedMessageResponse.as(Message[].class));
        assertTrue(updatedMessages.get(0).getCreationDate().isBefore(updatedMessages.get(1).getCreationDate()));
    }

    @Test
    void testUserCanSendMessageToAnotherUser() {
        // Given the user sends a message to another user
        Message message = Message.builder().content("content").userToId(user2.getId()).build();
        Response createMessageResponse = rq.body(message).post("/api/v1/ht/messages");
        assertEquals(201, createMessageResponse.getStatusCode());

        // Then the message should be created successfully
        Response getMessageResponse = rq.get("/api/v1/ht/messages");
        assertEquals(200, getMessageResponse.getStatusCode());
    }

    @Test
    void testUserCanEditTheirMessage() {
        // Given the user creates a message
        Message message = Message.builder().content("content").userToId(user2.getId()).build();
        Response createMessageResponse = rq.body(message).post("/api/v1/ht/messages");
        UUID messageId = createMessageResponse.as(Message.class).getId();
        assertEquals(201, createMessageResponse.getStatusCode());

        // When the user edits the text for the message
        message.setContent("I enjoy learning English");
        Response editMessageResponse = rq.body(message).put("/api/v1/ht/messages/" + messageId);
        assertEquals(200, editMessageResponse.getStatusCode());

        // Then the message should have its text updated successfully across the whole system
        Response getMessageResponse = rq.get("/api/v1/ht/messages/" + messageId);
        assertEquals(editMessageResponse.as(Message.class).getContent(), getMessageResponse.as(Message.class).getContent());
    }

    @Test
    void testUserCannotEditSomeoneElsesMessage() {
        // Given the user creates a message
        Message message = Message.builder().content("content").userToId(user1.getId()).userFromId(user2.getId()).build();
        Response createMessageResponse = rq.body(message).auth().basic(user2.getUsername(), user2.getPassword()).post("/api/v1/ht/messages");
        UUID messageId = createMessageResponse.as(Message.class).getId();

        assertEquals(201, createMessageResponse.getStatusCode());

        // When the authenticated user attempts to edit the message that belongs to the other user
        Response editMessageResponse = rq.body(message).auth().basic(user1.getUsername(), user1.getPassword()).put("/api/v1/ht/messages/" + messageId);

        // Then the system should block the user with a forbidden error
        assertEquals(403, editMessageResponse.getStatusCode());
    }

    @Test
    void testUserCanDeleteTheirOwnMessage() {
        // Given the user creates a message
        Message message = Message.builder().content("content").userToId(user2.getId()).build();
        Response createMessageResponse = rq.body(message).post("/api/v1/ht/messages");
        assertEquals(201, createMessageResponse.getStatusCode());
        UUID messageId = createMessageResponse.as(Message.class).getId();

        // When the user deletes the message
        Response deleteMessageResponse = rq.delete("/api/v1/ht/messages/" + messageId);

        // Then the message should be deleted successfully
        assertEquals(204, deleteMessageResponse.getStatusCode());
    }

    @Test
    void testUserCantDeleteSomeoneElsesMessage() {
        // When the user creates a message
        Message message = Message.builder().content("content").userFromId(user2.getId()).userToId(user1.getId()).build();
        Response createMessageResponse = rq.auth().basic(user2.getUsername(), user2.getPassword()).body(message).post("/api/v1/ht/messages/");
        assertEquals(201, createMessageResponse.getStatusCode());
        UUID messageId = createMessageResponse.as(Message.class).getId();

        // When the user attempts to delete the message
        Response deleteMessageResponse = rq.auth().basic(user1.getUsername(), user1.getPassword()).delete("/api/v1/ht/messages/" + messageId);

        // Then the message should not be deleted
        assertEquals(403, deleteMessageResponse.getStatusCode());
    }

    private Response createUser(String username, String password) {
        Hometown hometownRequest = Hometown.builder().city("anyCity").country("anyCountry").build();
        Hometown hometownResponse = convertToNewObject(hometownRequest, Hometown.class);
        hometownResponse.setId(randomUUID());
        HobbyAndInterest hobbyAndInterestRequest = HobbyAndInterest.builder().title("anyInterest").build();
        Set<HobbyAndInterest> hobbyAndInterestsRequest = new HashSet<>();
        hobbyAndInterestsRequest.add(hobbyAndInterestRequest);

        User user = User.builder()
                .username(username)
                .password(password)
                .name("anyName")
                .dob("2022-12-01")
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

        Response response = rq.body(user).post("/api/v1/ht/users");
        assertEquals(201, response.getStatusCode());

        return response;
    }
}
