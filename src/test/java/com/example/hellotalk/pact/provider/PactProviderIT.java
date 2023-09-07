package com.example.hellotalk.pact.provider;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.VerificationReports;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import com.example.hellotalk.config.BasePostgresConfig;
import com.example.hellotalk.model.HobbyAndInterest;
import com.example.hellotalk.model.Hometown;
import com.example.hellotalk.model.user.User;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.hc.core5.http.HttpRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.example.hellotalk.Constants.USER_ID;
import static com.example.hellotalk.util.Utils.convertToNewObject;
import static com.example.hellotalk.utils.Utils.getRequestSpecification;
import static com.example.hellotalk.utils.Utils.logCurlFromPact;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

/*
 * mvn -Dtest=pact.provider.PactProviderIT integration-test
 * mvn -Dtest="pact.provider.**.*IT" integration-test
 */

@Provider("MY_PROVIDER")
/* Uncomment this and comment @PactBroker instead to test locally by pasting a .json file for the contract under
 the target/pacts folder */
@PactFolder("target/pacts")
// @PactBroker(host = BROKER_PACT_URL, consumers = {"MY_CONSUMER"})
@VerificationReports(value = {"markdown"}, reportDir = "target/pacts")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "classpath:clean-up.sql", executionPhase = BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:init.sql", executionPhase = BEFORE_TEST_METHOD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PactProviderIT extends BasePostgresConfig {

    @LocalServerPort
    int port;

    private RequestSpecification rq;

    private final String username = "usernamed";
    private final String password = "passwordd";

    @BeforeAll
    void setUp() {
        Map<String, String> headers = new HashMap<>();
        rq = getRequestSpecification().baseUri("http://localhost:" + port)
                .contentType(ContentType.JSON)
                .auth().basic(username, password)
                .headers(headers);

        createUserResponse(username, password);
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactTestTemplate(PactVerificationContext context, HttpRequest request) {
        String encoded = Base64.getEncoder()
                .encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
        request.addHeader("Authorization", "Basic " + encoded);

        logCurlFromPact(context, request, "http://localhost:" + port);

        context.verifyInteraction();
    }

    @BeforeEach
    void before(PactVerificationContext context) {
        context.setTarget(new HttpTestTarget("localhost", port, ""));
    }

    @State("A request to retrieve a user")
    Map<String, Object> getUser() {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", "d3256c76-62d7-4481-9d1c-a0ccc4da380f");
        return map;
    }

    @State("A request to update a user")
    Map<String, Object> updateUser() {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", "d3256c76-62d7-4481-9d1c-a0ccc4da380f");
        return map;
    }

    @State("A request to retrieve a followship")
    Map<String, Object> getFollowship() {
        Map<String, Object> map = new HashMap<>();
        map.put("followshipId", "1b00ce80-806b-4d16-b0ec-32f5396ba4b0");
        return map;
    }

    @State("A request to retrieve a list of followships sent from a given user")
    Map<String, Object> getFollowingRequestsFromUser() {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", "ca3569ee-cb62-4f45-b1c2-199028ba5562");
        return map;
    }

    @State("A request to retrieve a list of followships sent to a given user")
    Map<String, Object> getFollowingRequestsToUser() {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", "ca3569ee-cb62-4f45-b1c2-199028ba5562");
        return map;
    }

    @State("A request to retrieve a moment")
    Map<String, Object> getMoment() {
        Map<String, Object> map = new HashMap<>();
        map.put("momentId", "e1f6bea6-4684-403e-9c41-8704fb0600c0");
        return map;
    }

    @State("A request to update a moment")
    Map<String, Object> updateMoment() {
        Map<String, Object> map = new HashMap<>();
        map.put("momentId", "e1f6bea6-4684-403e-9c41-8704fb0600c0");
        return map;
    }

    @State("A request to like a moment")
    Map<String, Object> likeMoment() {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", USER_ID);
        map.put("momentId", "b3f6bea6-4684-403e-9c41-8704fb0600c0");
        return map;
    }

    @State("A request to remove a like for a moment")
    Map<String, Object> unlikeMoment() {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", USER_ID);
        map.put("momentId", "c3f6bea6-4684-403e-9c41-8704fb0600c0");
        return map;
    }

    @State("A request to create a comment for a moment")
    Map<String, Object> createComment() {
        Map<String, Object> map = new HashMap<>();
        map.put("momentId", "b3f6bea6-4684-403e-9c41-8704fb0600c0");
        return map;
    }

    @State("A request to retrieve a comment")
    Map<String, Object> getComment() {
        Map<String, Object> map = new HashMap<>();
        map.put("momentId", "b3f6bea6-4684-403e-9c41-8704fb0600c0");
        map.put("commentId", "a2f6bea6-4684-403e-9c41-8704fb0600c0");
        return map;
    }

    @State("A request to retrieve a list of comments for a moment")
    Map<String, Object> getCommentsForMoment() {
        Map<String, Object> map = new HashMap<>();
        map.put("momentId", "b3f6bea6-4684-403e-9c41-8704fb0600c0");
        return map;
    }

    @State("A request to update a comment for a moment")
    Map<String, Object> updateCommentForMoment() {
        Map<String, Object> map = new HashMap<>();
        map.put("momentId", "b3f6bea6-4684-403e-9c41-8704fb0600c0");
        map.put("commentId", "a2f6bea6-4684-403e-9c41-8704fb0600c0");
        return map;
    }

    @State("A request to create a reply to a comment")
    Map<String, Object> createReplyToComment() {
        Map<String, Object> map = new HashMap<>();
        map.put("momentId", "b3f6bea6-4684-403e-9c41-8704fb0600c0");
        map.put("commentId", "a2f6bea6-4684-403e-9c41-8704fb0600c0");
        return map;
    }

    // Helpers
    private Response createUserResponse(String username, String password) {
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

        Response response = rq.body(user).post("/api/v1/ht/users");
        assertEquals(201, response.getStatusCode());

        return response;
    }
}
