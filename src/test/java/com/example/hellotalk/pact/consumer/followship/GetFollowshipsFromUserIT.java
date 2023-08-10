package com.example.hellotalk.pact.consumer.followship;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonArray;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.example.hellotalk.config.Constants.*;
import static com.example.hellotalk.utils.Utils.getMockRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(PactConsumerTestExt.class)
class GetFollowshipsFromUserIT {

    Map<String, String> headers = new HashMap<>();

    String path = "/api/v1/ht/followship/from/user/";
    UUID userId = UUID.fromString("1bfff94a-b70e-4b39-bd2a-be1c0f898589");

    @Pact(provider = PACT_PROVIDER, consumer = PACT_CONSUMER)
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        DslPart bodyReturned = PactDslJsonArray.arrayEachLike()
                .uuid("id", "d3256c76-62d7-4481-9d1c-a0ccc4da380f")
                .uuid("userToId", "27256c76-62d7-4481-9d1c-a0ccc4da380f")
                .uuid("userFromId", "5b256c76-62d7-4481-9d1c-a0ccc4da380f")
                .closeObject();

        return builder
                .given("A request to retrieve a list of followships sent from a given user")
                .uponReceiving("A request to retrieve a list of followships sent from a given user")
                .pathFromProviderState(path + "${userId}", path + userId)
                .method("GET")
                .headers(headers)
                .willRespondWith()
                .body(bodyReturned)
                .toPact();
    }

    @Test
    @PactTestFor(providerName = PACT_PROVIDER, port = MOCK_PACT_PORT, pactVersion = PactSpecVersion.V3)
    void runTest() {

        Response response = getMockRequest(headers).get(path + userId);
        assertEquals(200, response.getStatusCode());
    }
}
