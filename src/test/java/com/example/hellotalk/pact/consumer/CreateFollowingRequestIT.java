package com.example.hellotalk.pact.consumer;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.example.hellotalk.model.user.FollowingRequest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;

import static com.example.hellotalk.config.Constants.*;
import static com.example.hellotalk.utils.Utils.getMockRequest;
import static java.util.UUID.fromString;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(PactConsumerTestExt.class)
class CreateFollowingRequestIT {

    Map<String, String> headers = new HashMap<>();

    String path = "/api/v1/ht/follow/";

    @Pact(provider = PACT_PROVIDER, consumer = PACT_CONSUMER)
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");

        DslPart bodyReceived = new PactDslJsonBody()
                .uuid("userFromId", "499cfb0e-ede3-45a2-9272-e23135ac40fb")
                .uuid("userToId", "ca3569ee-cb62-4f45-b1c2-199028ba5562")
                .close();

        DslPart bodyReturned = new PactDslJsonBody()
                .uuid("id", "1bfff94a-b70e-4b39-bd2a-be1c0f898589")
                .uuid("userFromId", "499cfb0e-ede3-45a2-9272-e23135ac40fb")
                .uuid("userToId", "ca3569ee-cb62-4f45-b1c2-199028ba5562")
                .close();

        return builder
                .uponReceiving("A request to create a follower")
                .path(path)
                .body(bodyReceived)
                .method("POST")
                .headers(headers)
                .willRespondWith()
                .status(201)
                .body(bodyReturned)
                .toPact();
    }

    @Test
    @PactTestFor(providerName = PACT_PROVIDER, port = MOCK_PACT_PORT, pactVersion = PactSpecVersion.V3)
    void runTest() {

        FollowingRequest followingRequest = FollowingRequest.builder()
                .userFromId(fromString("499cfb0e-ede3-45a2-9272-e23135ac40fb"))
                .userToId(fromString("ca3569ee-cb62-4f45-b1c2-199028ba5562"))
                .build();

        Response response = getMockRequest(headers).body(followingRequest).post(path);
        assertEquals(201, response.getStatusCode());
    }

}
