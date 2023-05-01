package com.example.hellotalk.pact.provider;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.VerificationReports;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import com.example.hellotalk.config.BasePostgresConfig;
import org.apache.hc.core5.http.HttpRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

import java.util.HashMap;
import java.util.Map;

import static com.example.hellotalk.Constants.USER_ID;
import static com.example.hellotalk.utils.Utils.logCurlFromPact;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

/*
 * mvn -Dtest=pact.provider.PactProviderIT integration-test
 * mvn -Dtest="pact.provider.*IT" integration-test
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
class PactProviderIT extends BasePostgresConfig {

    @LocalServerPort
    int port;

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactTestTemplate(PactVerificationContext context, HttpRequest request) {

        request.addHeader("authorization", USER_ID);

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

    @State("A request to retrieve a following request")
    Map<String, Object> getFollowingRelationship() {
        Map<String, Object> map = new HashMap<>();
        map.put("followingRequestId", "1b00ce80-806b-4d16-b0ec-32f5396ba4b0");
        return map;
    }

    @State("A request to retrieve a list of following requests sent from a given user")
    Map<String, Object> getFollowingRequestsFromUser() {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", "ca3569ee-cb62-4f45-b1c2-199028ba5562");
        return map;
    }

    @State("A request to retrieve a list of following requests sent to a given user")
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
}
