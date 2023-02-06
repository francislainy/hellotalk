package com.example.hellotalk.pact.consumer.user;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.example.hellotalk.model.HobbyAndInterest;
import com.example.hellotalk.model.Hometown;
import com.example.hellotalk.model.user.User;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.*;

import static com.example.hellotalk.config.Constants.*;
import static com.example.hellotalk.utils.Utils.getMockRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(PactConsumerTestExt.class)
class UpdateUserIT {

    Map<String, String> headers = new HashMap<>();

    String path = "/api/v1/ht/users/";
    UUID userId = UUID.fromString("1bfff94a-b70e-4b39-bd2a-be1c0f898589");

    @Pact(provider = PACT_PROVIDER, consumer = PACT_CONSUMER)
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");

        DslPart bodyReceived = new PactDslJsonBody()
                .stringType("name", "anyName")
                .stringType("dob", "anyDob") // todo: use date type
                .stringType("gender", "anyGender")
                .stringType("selfIntroduction", "anySelfIntroduction")
                .stringType("creationDate", "anyCreationDate")
                .stringType("status", "anyStatus")
                .stringType("handle", "anyHandle")
                .stringType("nativeLanguage", "anyNativeLanguage")
                .stringType("targetLanguage", "anyTargetLanguage")
                .stringType("occupation", "anyOccupation")
                .stringType("placesToVisit", "anyPlacesToVisit")
                .stringType("subscriptionType", "anySubscriptionType")
                .object("hometown")
                .stringType("city", "anyCity")
                .stringType("country", "anyCountry")
                .closeObject()
                .eachLike("hobbyAndInterests")
                .stringType("title", "anyInterest")
                .closeArray()
                .close();

        DslPart bodyReturned = new PactDslJsonBody()
                .uuid("id", userId)
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
                .stringType("title", "anyInterest")
                .closeArray()
                .close();

        return builder
                .given("A request to update a user")
                .uponReceiving("A request to update a user")
                .pathFromProviderState(path + "${userId}", path + userId)
                .body(bodyReceived)
                .method("PUT")
                .headers(headers)
                .willRespondWith()
                .status(200)
                .body(bodyReturned)
                .toPact();
    }

    @Test
    @PactTestFor(providerName = PACT_PROVIDER, port = MOCK_PACT_PORT, pactVersion = PactSpecVersion.V3)
    void runTest() {

        Hometown hometown = Hometown.builder().city("anyCity").country("anyCountry").build();
        HobbyAndInterest hobbyAndInterest = HobbyAndInterest.builder().title("anyInterest").build();
        Set<HobbyAndInterest> hobbyAndInterests = new HashSet<>();
        hobbyAndInterests.add(hobbyAndInterest);

        User user = User.builder()
                .name("anyName")
                .dob("anyDob")
                .gender("anyGender")
                .selfIntroduction("anySelfIntroduction")
                .creationDate("anyCreationDate")
                .handle("anyHandle")
                .status("anyStatus")
                .nativeLanguage("anyNativeLanguage")
                .targetLanguage("anyTargetLanguage")
                .occupation("anyOccupation")
                .placesToVisit("anyPlacesToVisit")
                .subscriptionType("anySubscriptionType")
                .hometown(hometown)
                .hobbyAndInterests(hobbyAndInterests)
                .build();

        Response response = getMockRequest(headers).body(user).put(path + userId);
        assertEquals(200, response.getStatusCode());
    }

}
