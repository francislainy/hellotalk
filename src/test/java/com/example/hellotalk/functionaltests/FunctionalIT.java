package com.example.hellotalk.functionaltests;

import com.example.hellotalk.model.HobbyAndInterest;
import com.example.hellotalk.model.Hometown;
import com.example.hellotalk.model.user.User;
import com.example.hellotalk.repository.BasePostgresConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static com.example.hellotalk.util.Utils.convertToNewObject;
import static com.example.hellotalk.utils.Utils.getRequestSpecification;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.*;

/*
 * mvn -Dtest="functionaltests.*IT" integration-test
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FunctionalIT extends BasePostgresConfig {

    @LocalServerPort
    int port;

    RequestSpecification rq;

    @BeforeAll
    void setUp() {
        Map<String, String> headers = new HashMap<>();
        rq = getRequestSpecification().baseUri("http://localhost:" + port)
                .headers(headers);
    }

    @Test
    void testGetAllUsers() throws JsonProcessingException {

        createUserResponse();

        Response response = rq.get("/api/v1/ht/users");
        assertEquals(200, response.getStatusCode());

        List<User> list = response.jsonPath().get();
        assertFalse(list.isEmpty());

        ObjectMapper mapper = new ObjectMapper();
        List<User> userList = Arrays.asList(mapper.readValue(response.asString(), User[].class));
        User user = userList.get(0);

        assertAll(
                () -> assertNotNull(user.getId()),
                () -> assertEquals("anyCreationDate", user.getCreationDate()),
                () -> assertEquals("anyDob", user.getDob()),
                () -> assertEquals("anyGender", user.getGender()),
                () -> assertEquals("anyHandle", user.getHandle()),
                () -> assertEquals("anyName", user.getName()),
                () -> assertEquals("anyNativeLanguage", user.getNativeLanguage()),
                () -> assertEquals("anyOccupation", user.getOccupation()),
                () -> assertEquals("anyPlacesToVisit", user.getPlacesToVisit()),
                () -> assertEquals("anySelfIntroduction", user.getSelfIntroduction()),
                () -> assertEquals("anyStatus", user.getStatus()),
                () -> assertEquals("anySubscriptionType", user.getSubscriptionType()),
                () -> assertEquals("anyTargetLanguage", user.getTargetLanguage()),
                () -> assertEquals("anyCity", user.getHometown().getCity()),
                () -> assertEquals("anyCity", user.getHometown().getCity()),
                () -> assertEquals("anyCountry", user.getHometown().getCountry()));
    }

    @Test
    void testGetUser() {

        Response response = createUserResponse();
        UUID userId = response.as(User.class).getId();

        response = rq.get("/api/v1/ht/users/" + userId);
        assertEquals(200, response.getStatusCode());

        User user = response.as(User.class);

        assertAll(
                () -> assertNotNull(user.getId()),
                () -> assertEquals("anyCreationDate", user.getCreationDate()),
                () -> assertEquals("anyDob", user.getDob()),
                () -> assertEquals("anyGender", user.getGender()),
                () -> assertEquals("anyHandle", user.getHandle()),
                () -> assertEquals("anyName", user.getName()),
                () -> assertEquals("anyNativeLanguage", user.getNativeLanguage()),
                () -> assertEquals("anyOccupation", user.getOccupation()),
                () -> assertEquals("anyPlacesToVisit", user.getPlacesToVisit()),
                () -> assertEquals("anySelfIntroduction", user.getSelfIntroduction()),
                () -> assertEquals("anyStatus", user.getStatus()),
                () -> assertEquals("anySubscriptionType", user.getSubscriptionType()),
                () -> assertEquals("anyTargetLanguage", user.getTargetLanguage()),
                () -> assertNotNull(user.getHometown().getId()),
                () -> assertEquals("anyCity", user.getHometown().getCity()),
                () -> assertEquals("anyCountry", user.getHometown().getCountry()));
    }

    @Test
    void testCreateUser() {

        Response response = createUserResponse();
        User user = response.as(User.class);

        assertAll(
                () -> assertNotNull(user.getId()),
                () -> assertEquals("anyCreationDate", user.getCreationDate()),
                () -> assertEquals("anyDob", user.getDob()),
                () -> assertEquals("anyGender", user.getGender()),
                () -> assertEquals("anyHandle", user.getHandle()),
                () -> assertEquals("anyName", user.getName()),
                () -> assertEquals("anyNativeLanguage", user.getNativeLanguage()),
                () -> assertEquals("anyOccupation", user.getOccupation()),
                () -> assertEquals("anyPlacesToVisit", user.getPlacesToVisit()),
                () -> assertEquals("anySelfIntroduction", user.getSelfIntroduction()),
                () -> assertEquals("anyStatus", user.getStatus()),
                () -> assertEquals("anySubscriptionType", user.getSubscriptionType()),
                () -> assertEquals("anyTargetLanguage", user.getTargetLanguage()),
                () -> assertNotNull(user.getHometown().getId()),
                () -> assertEquals("anyCity", user.getHometown().getCity()),
                () -> assertEquals("anyCountry", user.getHometown().getCountry()));

        user.getHobbyAndInterests().forEach(h -> assertEquals("anyInterest", h.getTitle()));
    }

    @Test
    void testDeleteUser() {

        Response response = createUserResponse();
        UUID userId = response.as(User.class).getId();

        response = rq.delete("/api/v1/ht/users/" + userId);
        assertEquals(206, response.getStatusCode());

        response = rq.get("/api/v1/ht/users/" + userId);
        assertEquals(404, response.getStatusCode()); // Deleted item no longer found
    }

    private Response createUserResponse() {
        Hometown hometownRequest = Hometown.builder().city("anyCity").country("anyCountry").build();
        Hometown hometownResponse = convertToNewObject(hometownRequest, Hometown.class);
        hometownResponse.setId(randomUUID());
        HobbyAndInterest hobbyAndInterestRequest = HobbyAndInterest.builder().title("anyInterest").build();
        Set<HobbyAndInterest> hobbyAndInterestsRequest = new HashSet<>();
        hobbyAndInterestsRequest.add(hobbyAndInterestRequest);

        User userRequest = User.builder()
                .name("anyName")
                .dob("anyDob")
                .gender("anyGender")
                .subscriptionType("anySubscriptionType")
                .creationDate("anyCreationDate")
                .handle("anyHandle")
                .status("anyStatus")
                .nativeLanguage("anyNativeLanguage")
                .targetLanguage("anyTargetLanguage")
                .selfIntroduction("anySelfIntroduction")
                .occupation("anyOccupation")
                .placesToVisit("anyPlacesToVisit")
                .hometown(hometownRequest)
                .hobbyAndInterests(hobbyAndInterestsRequest)
                .build();

        return rq.body(userRequest).post("/api/v1/ht/users");
    }
}
