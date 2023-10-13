package com.example.hellotalk.functionaltests;

import com.example.hellotalk.config.BasePostgresConfig;
import com.example.hellotalk.entity.followship.FollowshipEntity;
import com.example.hellotalk.model.HobbyAndInterest;
import com.example.hellotalk.model.Hometown;
import com.example.hellotalk.model.followship.Followship;
import com.example.hellotalk.model.user.User;
import com.example.hellotalk.repository.followship.FollowshipRepository;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
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
import static org.junit.jupiter.api.Assertions.*;

/*
 * mvn -Dtest="functionaltests.*IT" integration-test
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FollowshipIT extends BasePostgresConfig {

    @LocalServerPort
    int port;

    RequestSpecification rq;

    final static String USERNAME_1 = "john@email.com";
    final static String PASSWORD_1 = "1234";

    final static String USERNAME_2 = "mary@email.com";
    final static String PASSWORD_2 = "Password123!";
    User user1;
    User user2;

    @Autowired
    FollowshipRepository followshipRepository;

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

    @Test
    void testUserCanFollowAnotherUser() {
        // When the authenticated user triggers the request to follow another user
        Followship followship = Followship.builder().userFromId(user1.getId()).userToId(user2.getId()).build();
        Response followUserResponse = rq.body(followship).auth().basic(USERNAME_1, PASSWORD_1).post("/api/v1/ht/followship");
        followship = followUserResponse.as(Followship.class);

        // Then The creation request is successful
        assertEquals(201, followUserResponse.getStatusCode());

        // And the follower user should have their list of users they follow updated to include the user they are following
        List<FollowshipEntity> followshipsByUserFromId = followshipRepository.findFollowshipsByUserFromId(user1.getId());
        boolean isOnUserFromList = followshipsByUserFromId.stream().anyMatch(f -> user2.getId().equals(f.getUserToEntity().getId()));
        assertTrue(isOnUserFromList);

        // And the followed user should have their list of followers updated to include the new follower
        List<FollowshipEntity> followshipsByUserToId = followshipRepository.findFollowshipsByUserToId(user2.getId());
        boolean isOnUserToList = followshipsByUserToId.stream().anyMatch(f -> user1.getId().equals(f.getUserFromEntity().getId()));
        assertTrue(isOnUserToList);

        // And I delete the followship
        followshipRepository.deleteById(followship.getId());
    }

    @Test
    void testUserCanStopFollowingAnotherUser() {
        // When the authenticated user triggers the request to follow another user
        Followship followship = Followship.builder().userFromId(user1.getId()).userToId(user2.getId()).build();
        Response followUserResponse = rq.body(followship).auth().basic(USERNAME_1, PASSWORD_1).post("/api/v1/ht/followship");
        followship = followUserResponse.as(Followship.class);

        // Then The creation request is successful
        assertEquals(201, followUserResponse.getStatusCode());

        // And the authenticated user triggers the request to stop following the other user
        Response unfollowUserResponse = rq.delete("/api/v1/ht/followship/" + followship.getId());
        assertEquals(200, unfollowUserResponse.getStatusCode());

        // And the follower user should have their list of users they follow updated to not include the user they are no longer following
        List<FollowshipEntity> followshipsByUserFromId = followshipRepository.findFollowshipsByUserFromId(user1.getId());
        boolean isOnUserFromList = followshipsByUserFromId.stream().anyMatch(f -> user2.getId().equals(f.getUserToEntity().getId()));
        assertFalse(isOnUserFromList);

        // And the followed user should have their list of followers updated to not include the new follower
        List<FollowshipEntity> followshipsByUserToId = followshipRepository.findFollowshipsByUserToId(user2.getId());
        boolean isOnUserToList = followshipsByUserToId.stream().anyMatch(f -> user1.getId().equals(f.getUserFromEntity().getId()));
        assertFalse(isOnUserToList);
    }

    @Test
    void testUserCantFollowThemselves() {
        // When the authenticated user triggers the request to follow themselves
        Followship followship = Followship.builder().userFromId(user1.getId()).userToId(user1.getId()).build();
        Response followUserResponse = rq.body(followship).auth().basic(USERNAME_1, PASSWORD_1).post("/api/v1/ht/followship");

        // Then The creation request fails with a bad request error
        assertEquals(400, followUserResponse.getStatusCode());

        // And the followship is not created
        List<FollowshipEntity> followshipsByUserFromId = followshipRepository.findFollowshipsByUserFromId(user1.getId());
        boolean isOnUserFromList = followshipsByUserFromId.stream().anyMatch(f -> user1.getId().equals(f.getUserToEntity().getId()));
        assertFalse(isOnUserFromList);
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
