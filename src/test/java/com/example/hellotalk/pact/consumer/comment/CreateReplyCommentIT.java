package com.example.hellotalk.pact.consumer.comment;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.example.hellotalk.model.comment.Comment;
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
class CreateReplyCommentIT {

    Map<String, String> headers = new HashMap<>();

    String path = "/api/v1/ht/moments/";
    UUID momentId = randomUUID();
    UUID parentId = randomUUID();
    UUID commentId = randomUUID();

    @Pact(provider = PACT_PROVIDER, consumer = PACT_CONSUMER)
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");

        DateTimeFormatter formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
        ZonedDateTime creationDate = ZonedDateTime.parse("2022-12-31T23:59:59Z", formatter);

        DslPart bodyReceived = new PactDslJsonBody()
                .stringType("content", "anyText")
                .close();

        DslPart bodyReturned = new PactDslJsonBody()
                .uuid("id", commentId)
                .uuid("parentId", parentId)
                .stringType("content", "anyText")
                .stringType("creationDate", creationDate.format(formatter))
                .object("user")
                .uuid("id", "caf6bea6-4684-403e-9c41-8704fb0600c0")
                .close();

        return builder
                .given("A request to create a reply to a comment")
                .uponReceiving("A request to create a reply to a comment")
                .pathFromProviderState(path + "${momentId}" + "/comments/" + "${commentId}" + "/replies", path + momentId + "/comments/" + commentId + "/replies")
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

        Comment replyComment = Comment.builder()
                .content("anyText")
                .build();

        Response response = getMockRequest(headers).body(replyComment).post(path + momentId + "/comments/" + commentId + "/replies");
        assertEquals(201, response.getStatusCode());
    }

}
