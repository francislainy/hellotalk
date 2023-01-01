package com.example.hellotalk.pact.consumer;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonArray;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.example.hellotalk.utils.Utils.getRequestSpecification;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(PactConsumerTestExt.class)
class PactConsumerGetUserIT {

    Map<String, String> headers = new HashMap<>();

    String path = "/api/v1/ht/user/";
    UUID userId = UUID.fromString("1bfff94a-b70e-4b39-bd2a-be1c0f898589");

    @Pact(provider = "MY_PROVIDER", consumer = "MY_CONSUMER")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");

        DslPart bodyReturned = new PactDslJsonBody()
                .uuid("id", userId)
                .stringType("name", "anyName")
                .stringType("dob", "anyDob") //todo: use date type
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
                .close();

        return builder
                .given("A request to retrieve a user")
                .uponReceiving("A request to retrieve a user")
                .pathFromProviderState(path + "${userId}", path + userId)
                .method("GET")
                .headers(headers)
                .willRespondWith()
                .status(200)
                .body(bodyReturned)
                .toPact();

    }

    @Test
    @PactTestFor(providerName = "MY_PROVIDER", port = "8082", pactVersion = PactSpecVersion.V3)
    void runTest() {

        //Mock url
        RequestSpecification rq = getRequestSpecification().baseUri("http://localhost:8082").headers(headers);

        Response response = rq.get(path + userId);

        assertEquals(200, response.getStatusCode());
    }

}
