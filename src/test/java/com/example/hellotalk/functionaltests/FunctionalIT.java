package com.example.hellotalk.functionaltests;

import com.example.hellotalk.model.user.User;
import com.example.hellotalk.repository.BaseIntegrationTest;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.hellotalk.utils.Utils.getRequestSpecification;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Sql(scripts = "classpath:init.sql", executionPhase = BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:clean-up.sql", executionPhase = AFTER_TEST_METHOD)
class FunctionalIT extends BaseIntegrationTest {

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

        Response response = rq.get("/api/v1/ht/users");
        assertEquals(200, response.getStatusCode());

        List<User> list = response.jsonPath().get();
        assertFalse(list.isEmpty());

        ObjectMapper mapper = new ObjectMapper();
        List<User> userList = Arrays.asList(mapper.readValue(response.asString(), User[].class));
        User user = userList.get(0);

        assertAll(
                () -> assertNotNull(user.getId()),
                () -> assertEquals("2022-12-01", user.getCreationDate()),
                () -> assertEquals("17121989", user.getDob()),
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
                () -> assertEquals("anyCountry", user.getHometown().getCountry())
        );
    }

}
