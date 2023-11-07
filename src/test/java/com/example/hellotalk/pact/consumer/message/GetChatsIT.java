package com.example.hellotalk.pact.consumer.message;

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

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.example.hellotalk.config.Constants.*;
import static com.example.hellotalk.utils.Utils.getMockRequest;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpMethod.GET;

@ExtendWith(PactConsumerTestExt.class)
class GetChatsIT {

    Map<String, String> headers = new HashMap<>();

    String path = "/api/v1/ht/messages/chats/";
    UUID chatId = UUID.fromString("a2f6bea6-4684-403e-9c41-8704fb0600f4");
    DateTimeFormatter formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
    ZonedDateTime creationDate = ZonedDateTime.parse("2022-12-31T23:59:59Z", formatter);

    @Pact(provider = PACT_PROVIDER, consumer = PACT_CONSUMER)
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");

        DslPart bodyReturned = Objects.requireNonNull(PactDslJsonArray.arrayEachLike()
                .uuid("id", chatId)
                .eachLike("participants", 2)
                .uuid("id", randomUUID())
                .stringType("username", "anyName")
                //                .stringType("avatar")
                .closeArray()
                .eachLike("messages", 1)
                .uuid("id", randomUUID())
                .stringType("content", "anyText")
                .stringType("creationDate", creationDate.format(formatter))
                .uuid("userFromId", randomUUID())
                .uuid("userToId", randomUUID())
                .closeArray()
                .close());

        return builder
                .given("A request to retrieve a list of chats")
                .uponReceiving("A request to retrieve a list of chats")
                .path(path)
                .method(String.valueOf(GET))
                .headers(headers)
                .willRespondWith()
                .status(200)
                .body(Objects.requireNonNull(bodyReturned))
                .toPact();
    }

    @Test
    @PactTestFor(providerName = PACT_PROVIDER, port = MOCK_PACT_PORT, pactVersion = PactSpecVersion.V3)
    void runTest() {

        Response response = getMockRequest(headers).get(path);
        assertEquals(200, response.getStatusCode());
    }

}
