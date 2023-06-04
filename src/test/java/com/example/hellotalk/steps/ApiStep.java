package com.example.hellotalk.steps;

import com.example.hellotalk.client.RestClient;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@RequiredArgsConstructor
public class ApiStep {

    private final RestClient restClient;
    private Response response;

    private final String username = "testUsername";
    private final String password = "testPassword";

    @Given("I access the get users endpoint")
    public void iAccessGetUsersEndpoint() {

        Map<String, String> headers = new HashMap<>();
        RequestSpecification rq = restClient.getRequestSpecification()
                .contentType(ContentType.JSON)
                .auth().basic(username, password)
                .headers(headers);
        setResponse(rq.get("/api/v1/ht/users/"));
    }

    @Then("I get a 200 successful response")
    public void iGetResponse() {
        response.then().statusCode(200);
    }
}
