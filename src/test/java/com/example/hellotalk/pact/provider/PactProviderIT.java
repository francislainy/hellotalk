package com.example.hellotalk.pact.provider;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.VerificationReports;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import com.example.hellotalk.repository.BasePostgresConfig;
import org.apache.hc.core5.http.HttpRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

import java.util.HashMap;
import java.util.Map;

import static com.example.hellotalk.utils.Utils.logCurlFromPact;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@Provider("MY_PROVIDER")
/* Uncomment this and comment @PactBroker instead to test locally by pasting a .json file for the contract under
 the target/pacts folder */
@PactFolder("target/pacts")
// @PactBroker(host = BROKER_PACT_URL, consumers = {"MY_CONSUMER"})
@VerificationReports(value = {"markdown"}, reportDir = "target/pacts")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "classpath:init.sql", executionPhase = BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:clean-up.sql", executionPhase = AFTER_TEST_METHOD)
class PactProviderIT extends BasePostgresConfig {

    @LocalServerPort
    int port;

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactTestTemplate(PactVerificationContext context, HttpRequest request) {

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
        map.put("followingRequestId", "e600ce80-806b-4d16-b0ec-32f5396ba4b0");
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
}
