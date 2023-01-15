package com.example.hellotalk.pact.consumer;

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

import static com.example.hellotalk.config.Constants.*;
import static com.example.hellotalk.utils.Utils.getMockRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(PactConsumerTestExt.class)
class GetUsersIT {

    Map<String, String> headers = new HashMap<>();

    String path = "/api/v1/ht/user";

    @Pact(provider = PACT_PROVIDER, consumer = PACT_CONSUMER)
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        DslPart bodyReturned = PactDslJsonArray.arrayEachLike()
                .uuid("id", "d3256c76-62d7-4481-9d1c-a0ccc4da380f")
                .stringType("name", "anyName")
                .stringType("dob", "anyDob") // todo: use date type
                .stringType("gender", "anyGender")
                .stringType("selfIntroduction", "anySelfIntroduction")
                .stringType("creationDate", "anyDate")
                .stringType("status", "anyStatus")
                .stringType("handle", "anyHandle")
                .stringType("nativeLanguage", "anyNativeLanguage")
                .stringType("targetLanguage", "anyTargetLanguage")
                .stringType("occupation", "anyOccupation")
                .stringType("placesToVisit", "anyPlacesToVisit")
                .stringType("subscriptionType", "anySubscriptionType")
                .object("hometown")
                .uuid("id", "e135b321-c58d-47c3-b9c4-c081a5b4684f")
                .stringType("city", "anyCity")
                .stringType("country", "anyCountry")
                .closeObject()
                .eachLike("hobbyAndInterests")
                .uuid("id", "e135b321-c58d-47c3-b9c4-c081a5b4684f")
                .stringType("title", "anyCity")
                .closeArray()
                .closeObject();

        return builder
                .uponReceiving("A request to retrieve a list of users")
                .path(path)
                .method("GET")
                .headers(headers)
                .willRespondWith()
                .body(bodyReturned)
                .toPact();

    }

    @Test
    @PactTestFor(providerName = PACT_PROVIDER, port = MOCK_PACT_PORT, pactVersion = PactSpecVersion.V3)
    void runTest() {

        Response response = getMockRequest(headers).get(path);
        assertEquals(200, response.getStatusCode());
    }

}
