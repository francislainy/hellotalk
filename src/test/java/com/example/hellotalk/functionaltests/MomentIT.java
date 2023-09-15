package com.example.hellotalk.functionaltests;

import com.example.hellotalk.config.BasePostgresConfig;
import com.example.hellotalk.model.HobbyAndInterest;
import com.example.hellotalk.model.Hometown;
import com.example.hellotalk.model.comment.Comment;
import com.example.hellotalk.model.moment.Moment;
import com.example.hellotalk.model.user.User;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

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
@RequiredArgsConstructor
class MomentIT extends BasePostgresConfig {

    @LocalServerPort
    private int port;

    private RequestSpecification rq;

    private final static String USERNAME_1 = "john@email.com";
    private final static String PASSWORD_1 = "1234";

    private final static String USERNAME_2 = "mary@email.com";
    private final static String PASSWORD_2 = "Password123!";

    @BeforeAll
    void setUp() {
        Map<String, String> headers = new HashMap<>();
        rq = getRequestSpecification().baseUri("http://localhost:" + port)
                .contentType(ContentType.JSON)
                .headers(headers);

        createUser(USERNAME_1, PASSWORD_1);
        createUser(USERNAME_2, PASSWORD_2);
    }

    @BeforeEach
    void setUpEach() {
        rq.auth().basic(USERNAME_1, PASSWORD_1);
    }

    @Test
    void testUserCreatesMomentAndAddsToList() {
        // When the user creates a moment with content "I enjoy learn English"
        Moment moment = Moment.builder().content("I enjoy learning English").build();
        Response createMomentResponse = rq.body(moment).post("/api/v1/ht/moments");
        assertEquals(201, createMomentResponse.getStatusCode());

        // Then the moment should be created successfully
        // And the user should be able to see the moment in their list of moments
        Response getMomentsResponse = rq.get("/api/v1/ht/moments");
        assertEquals(200, getMomentsResponse.getStatusCode());
        assertTrue(getMomentsResponse.asString().contains("I enjoy learning English"));
    }

    @Test
    void testUserCreatesCommentToAnotherUsersMoment() {
        // When the user creates a moment with content "I enjoy learn English"
        Moment moment = Moment.builder().content("I enjoy learning English").build();
        Response createMomentResponse = rq.body(moment).post("/api/v1/ht/moments");
        assertEquals(201, createMomentResponse.getStatusCode());

        // And the user adds a comment to the moment with grammar correction "I enjoy learning English"
        Comment comment = Comment.builder().content("I enjoy learning English").build();
        Response addCommentResponse = rq.body(comment).post("/api/v1/ht/moments/" + createMomentResponse.as(Moment.class).getId() + "/comments");
        assertEquals(201, addCommentResponse.getStatusCode());

        // Then the comment should be added to the list of comments for that moment
        Response getCommentsResponse = rq.get("/api/v1/ht/moments/" + createMomentResponse.as(Moment.class).getId() + "/comments");
        assertEquals(200, getCommentsResponse.getStatusCode());
        assertTrue(getCommentsResponse.asString().contains("I enjoy learning English"));
    }

    @Test
    void testUserLikesAndUnlikesMomentBelongingToAnotherUser() {
        // Given the user creates a moment with content "I enjoy learning English"
        Moment moment = Moment.builder().content("I enjoy learning English").build();
        Response createMomentResponse = rq.body(moment).post("/api/v1/ht/moments");
        UUID momentId = createMomentResponse.as(Moment.class).getId();

        // When the user with username "mary@email.com" likes the moment
        Response likeMomentResponse = rq.put("/api/v1/ht/moments/" + momentId + "/like");
        assertEquals(201, likeMomentResponse.getStatusCode());

        // Then the moment should indicate it has received a like from the user with username "mary@email.com"
        // And the total number of likes for the moment should increase by 1
        Response getMomentResponse = rq.get("/api/v1/ht/moments/" + momentId);
        assertEquals(200, getMomentResponse.getStatusCode());
        assertEquals(1, getMomentResponse.as(Moment.class).getLikedByIds().size());

        // When the user removes his like for the moment
        Response unlikeMomentResponse = rq.delete("/api/v1/ht/moments/" + momentId + "/unlike");
        assertEquals(200, unlikeMomentResponse.getStatusCode());

        // And the total number of likes for the moment should return to 0
        getMomentResponse = rq.get("/api/v1/ht/moments/" + momentId);
        assertEquals(200, getMomentResponse.getStatusCode());
        assertEquals(0, getMomentResponse.as(Moment.class).getLikedByIds().size());
    }

    @Test
    void testUserCanLikeAndUnlikeTheirOwnMoment() {
        // Given the user creates a moment with content "I enjoy learning English"
        Moment moment = Moment.builder().content("I enjoy learning English").build();
        Response createMomentResponse = rq.body(moment).post("/api/v1/ht/moments");
        UUID momentId = createMomentResponse.as(Moment.class).getId();

        // When the user with username "john@email.com" likes the moment
        Response likeMomentResponse = rq.put("/api/v1/ht/moments/" + momentId + "/like");
        assertEquals(201, likeMomentResponse.getStatusCode());

        // Then the moment should indicate it has received a like from the user with username "john@email.com"
        // And the total number of likes for the moment should increase by 1
        Response getMomentResponse = rq.get("/api/v1/ht/moments/" + momentId);
        assertEquals(200, getMomentResponse.getStatusCode());
        assertEquals(1, getMomentResponse.as(Moment.class).getLikedByIds().size());

        // When the user removes his like for the moment
        Response unlikeMomentResponse = rq.delete("/api/v1/ht/moments/" + momentId + "/unlike");
        assertEquals(200, unlikeMomentResponse.getStatusCode());

        // And the total number of likes for the moment should return to 0
        getMomentResponse = rq.get("/api/v1/ht/moments/" + momentId);
        assertEquals(200, getMomentResponse.getStatusCode());
        assertEquals(0, getMomentResponse.as(Moment.class).getLikedByIds().size());
    }

    @Test
    void testUserEditsTheirOwnMoment() {
        // And the user creates a moment with content "I enjoy learn English"
        Moment moment = Moment.builder().content("I enjoy learn English").build();
        Response createMomentResponse = rq.body(moment).post("/api/v1/ht/moments");
        UUID momentId = createMomentResponse.as(Moment.class).getId();

        // When the user edits the text for the moment to "I enjoy learning English"
        Moment editedMoment = Moment.builder().content("I enjoy learning English").build();
        Response editMomentResponse = rq.body(editedMoment).put("/api/v1/ht/moments/" + momentId);
        assertEquals(200, editMomentResponse.getStatusCode());

        // Then the moment should have its text updated successfully across the whole system
        Response getMomentResponse = rq.get("/api/v1/ht/moments/" + momentId);
        assertEquals(200, getMomentResponse.getStatusCode());
        assertEquals("I enjoy learning English", getMomentResponse.as(Moment.class).getContent());
    }

    @Test
    void testUserNotAbleToEditSomeoneElsesMoment() {
        // And the user creates a moment with content "I enjoy learn English"
        Moment moment = Moment.builder().content("I enjoy learning English").build();
        Response createMomentResponse = rq.body(moment).post("/api/v1/ht/moments");
        UUID momentId = createMomentResponse.as(Moment.class).getId();

        // When the authenticated user attempts to edit the moment that belongs to user "John"
        Moment editedMoment = Moment.builder().content("I enjoy learning English").build();
        Response editMomentResponse = rq.body(editedMoment).auth().basic(USERNAME_2, PASSWORD_2).put("/api/v1/ht/moments/" + momentId);

        // Then the system should block the user with a forbidden error
        assertEquals(403, editMomentResponse.getStatusCode());
    }

    @Test
    void testUserDeletesTheirOwnMoment() {
        // And the user creates a moment with content "I enjoy learning English"
        Moment moment = Moment.builder().content("I enjoy learning English").build();
        Response createMomentResponse = rq.body(moment).post("/api/v1/ht/moments");
        UUID momentId = createMomentResponse.as(Moment.class).getId();

        // When the user deletes the moment
        Response deleteMomentResponse = rq.delete("/api/v1/ht/moments/" + momentId);
        assertEquals(200, deleteMomentResponse.getStatusCode());

        // And the moment should no longer exist in the system
        Response getMomentResponse = rq.get("/api/v1/ht/moments/" + momentId);
        assertEquals(404, getMomentResponse.getStatusCode());
    }

    @Test
    void testUserCantDeleteSomeoneElsesMoment() {
        // And the user creates a moment with content "I enjoy learn English"
        Moment moment = Moment.builder().content("I enjoy learning English").build();
        Response createMomentResponse = rq.body(moment).post("/api/v1/ht/moments");
        UUID momentId = createMomentResponse.as(Moment.class).getId();

        // Then the user attempts to delete the moment
        Response deleteMomentResponse = rq.auth().basic(USERNAME_2, PASSWORD_2).delete("/api/v1/ht/moments/" + momentId);
        // And the moment should still exist in the system
        assertEquals(403, deleteMomentResponse.getStatusCode());
        Response getMomentResponse = rq.get("/api/v1/ht/moments/" + momentId);
        assertEquals(200, getMomentResponse.getStatusCode());
    }

    private void createUser(String username, String password) {
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
    }
}
