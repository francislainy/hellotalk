package com.example.hellotalk.pact.consumer;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.example.hellotalk.config.Constants.*;
import static com.example.hellotalk.utils.Utils.getRequestSpecification;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(PactConsumerTestExt.class)
class GetFollowingRequestIT {

    Map<String, String> headers = new HashMap<>();

    String path = "/api/v1/ht/follow/";
    UUID followingRequestId = UUID.fromString("1bfff94a-b70e-4b39-bd2a-be1c0f898589");

    @Pact(provider = PACT_PROVIDER, consumer = PACT_CONSUMER)
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");

        DslPart bodyReturned = new PactDslJsonBody()
                .uuid("id", followingRequestId)
                .uuid("userFromId", "499cfb0e-ede3-45a2-9272-e23135ac40fb")
                .uuid("userToId", "ca3569ee-cb62-4f45-b1c2-199028ba5562")
                .close();

        return builder
                .given("A request to retrieve a following relationship")
                .uponReceiving("A request to retrieve a following relationship")
                .pathFromProviderState(path + "${followingRequestId}", path + followingRequestId)
                .method("GET")
                .headers(headers)
                .willRespondWith()
                .status(200)
                .body(bodyReturned)
                .toPact();
    }

    @Test
    @PactTestFor(providerName = PACT_PROVIDER, port = MOCK_PACT_PORT, pactVersion = PactSpecVersion.V3)
    void runTest() {

        // Mock url
        RequestSpecification rq = getRequestSpecification().baseUri(MOCK_PACT_URL).headers(headers);

        Response response = rq.get(path + followingRequestId);

        assertEquals(200, response.getStatusCode());
    }

}
