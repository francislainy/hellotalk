package com.example.hellotalk.pact.consumer.moment;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.example.hellotalk.model.moment.Moment;
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
class UpdateMomentIT {

    Map<String, String> headers = new HashMap<>();

    String path = "/api/v1/ht/moments/";
    UUID momentId = UUID.fromString("e1f6bea6-4684-403e-9c41-8704fb0600c0");

    @Pact(provider = PACT_PROVIDER, consumer = PACT_CONSUMER)
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");

        DslPart bodyReceived = new PactDslJsonBody()
                .stringType("text", "anyText")
                .close();

        DslPart bodyReturned = new PactDslJsonBody()
                .uuid("id", momentId)
                .stringType("text", "anyText")
                .close();

        return builder
                .given("A request to update a moment")
                .uponReceiving("A request to update a moment")
                .pathFromProviderState(path + "${userId}", path + momentId)
                .body(Objects.requireNonNull(bodyReceived))
                .method("PUT")
                .headers(headers)
                .willRespondWith()
                .status(200)
                .body(Objects.requireNonNull(bodyReturned))
                .toPact();
    }

    @Test
    @PactTestFor(providerName = PACT_PROVIDER, port = MOCK_PACT_PORT, pactVersion = PactSpecVersion.V3)
    void runTest() {

        Moment moment = Moment.builder()
                .text("anyText")
                .build();

        Response response = getMockRequest(headers).body(moment).put(path + momentId);
        assertEquals(200, response.getStatusCode());
    }

}
