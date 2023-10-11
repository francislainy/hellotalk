package com.example.hellotalk.pact.consumer.message;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.example.hellotalk.model.message.Message;
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

@ExtendWith(PactConsumerTestExt.class)
class CreateMessageIT {

    Map<String, String> headers = new HashMap<>();

    String path = "/api/v1/ht/messages/";
    UUID messageId = randomUUID();
    UUID userToId = randomUUID();

    @Pact(provider = PACT_PROVIDER, consumer = PACT_CONSUMER)
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");

        DateTimeFormatter formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME; // todo: verify formatting for this method - 02/20/2023
        ZonedDateTime creationDate = ZonedDateTime.parse("2022-12-31T23:59:59Z", formatter);

        DslPart bodyReceived = new PactDslJsonBody()
                .stringType("content", "anyText")
                .uuid("userToId", "ca3569ee-cb62-4f45-b1c2-199028ba5562")
                .close();

        DslPart bodyReturned = new PactDslJsonBody()
                .uuid("id", messageId)
                .nullValue("parentId") // todo: handle case where message is a reply to another message - 02/10/2023
                .stringType("content", "anyText")
                .stringType("creationDate", creationDate.format(formatter))
                .uuid("userToId", randomUUID())
                .uuid("userFromId", randomUUID())
                .close();

        return builder
                .uponReceiving("A request to create a message")
                .path(path)
                .body(Objects.requireNonNull(bodyReceived))
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

        Message message = Message.builder()
                .content("anyText")
                .userToId(userToId)
                .build();

        Response response = getMockRequest(headers).body(message).post(path);
        assertEquals(201, response.getStatusCode());
    }
}
