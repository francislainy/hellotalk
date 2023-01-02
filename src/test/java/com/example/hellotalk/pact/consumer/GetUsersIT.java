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
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;

import static com.example.hellotalk.utils.Utils.getRequestSpecification;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(PactConsumerTestExt.class)
class GetUsersIT {

    Map<String, String> headers = new HashMap<>();

    String path = "/api/v1/ht/user";

    @Pact(provider = "MY_PROVIDER", consumer = "MY_CONSUMER")
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
    @PactTestFor(providerName = "MY_PROVIDER", port = "8082", pactVersion = PactSpecVersion.V3)
    void runTest() {

        // Mock url
        RequestSpecification rq = getRequestSpecification().baseUri("http://localhost:8082").headers(headers);

        Response response = rq.get(path);

        assertEquals(200, response.getStatusCode());
    }

}
