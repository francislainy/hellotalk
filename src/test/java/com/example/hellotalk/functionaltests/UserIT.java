package com.example.hellotalk.functionaltests;

import com.example.hellotalk.client.DBClient;
import com.example.hellotalk.config.BasePostgresConfig;
import com.example.hellotalk.model.HobbyAndInterest;
import com.example.hellotalk.model.Hometown;
import com.example.hellotalk.model.user.User;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.*;

import static com.example.hellotalk.util.Utils.convertToNewObject;
import static com.example.hellotalk.utils.Utils.getRequestSpecification;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/*
 * mvn -Dtest="functionaltests.*IT" integration-test
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserIT extends BasePostgresConfig {

    @LocalServerPort
    private int port;

    private RequestSpecification rq;

    @Autowired
    private DBClient dbClient;

    private final static String USERNAME_1 = "john@email.com";
    private final static String PASSWORD_1 = "1234";

    private final static String USERNAME_2 = "mary@email.com";
    private final static String PASSWORD_2 = "Password123!";
    private User user1;
    private User user2;

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
    void testValidateFieldsForGetUsersEndpoint() {
        // Given I access the get users endpoint
        Response getUsersResponse = rq.get("/api/v1/ht/users");

        // And I get a 200 successful response
        assertEquals(200, getUsersResponse.getStatusCode());

        // And The response has all the expected fields for the get users endpoint
        List<User> userList = Arrays.asList(getUsersResponse.as(User[].class));
        assertThat(userList).usingRecursiveComparison().isEqualTo(List.of(user1, user2));
    }

    @Test
    void testValidateFieldsForGetUsersEndpointAgainstDatabase() {
        // Given I access the get users endpoint
        Response getUsersResponse = rq.get("/api/v1/ht/users");

        // And I get a 200 successful response
        assertEquals(200, getUsersResponse.getStatusCode());

        // And I validate the response for the get users endpoint against the database
        assertThat(List.of(user1, user2)).usingRecursiveComparison()
                .ignoringFields("followerOf", "hometown", "hobbyAndInterests", "followedBy")

                .isEqualTo(dbClient.getUsers());
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
