package com.example.hellotalk.pact.consumer.user;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
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
import java.util.Objects;
import java.util.UUID;

import static com.example.hellotalk.config.Constants.*;
import static com.example.hellotalk.utils.Utils.getMockRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(PactConsumerTestExt.class)
class UnlikeMomentIT {

    Map<String, String> headers = new HashMap<>();

    String path = "/api/v1/ht/users/";
    UUID userId = UUID.fromString("1bfff94a-b70e-4b39-bd2a-be1c0f898589");
    UUID momentId = UUID.fromString("2afff94a-b70e-4b39-bd2a-be1c0f898545");

    @Pact(provider = PACT_PROVIDER, consumer = PACT_CONSUMER)
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");

        DslPart bodyReturned = new PactDslJsonBody()
                .stringValue("message", "Moment unliked successfully")
                .object("data")
                .uuid("id", "3cfff94a-b70e-4b39-bd2a-be1c0f898532")
                .uuid("userId", userId)
                .uuid("momentId", momentId)
                .closeObject()
                .close();

        return builder
                .given("A request to remove a like for a moment")
                .uponReceiving("A request to remove a like for a moment")
                .pathFromProviderState(path + "${userId}" + "/like/" + "${momentId}", path + userId + "like/" + momentId)
                .method("POST")
                .headers(headers)
                .willRespondWith()
                .status(201)
                .body(Objects.requireNonNull(bodyReturned))
                .toPact();
    }

    @Test
    @PactTestFor(providerName = PACT_PROVIDER, port = MOCK_PACT_PORT, pactVersion = PactSpecVersion.V3)
    void runTest() {

        Response response = getMockRequest(headers).post(path + userId + "like/" + momentId);
        assertEquals(201, response.getStatusCode());
    }
}
