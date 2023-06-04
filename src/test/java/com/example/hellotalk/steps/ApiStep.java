package com.example.hellotalk.steps;

import com.example.hellotalk.client.RestClient;
import com.example.hellotalk.model.user.User;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ApiStep {

    private final RestClient restClient;
    private Response response;

    private final String username = "testUsername";
    private final String password = "testPassword";

    @Given("I access the get users endpoint")
    public void iAccessGetUsersEndpoint() {

        RequestSpecification rq = restClient.getRequestSpecification()
                .auth().basic(username, password);
        setResponse(rq.get("/api/v1/ht/users/"));
    }

    @Then("I get a 200 successful response")
    public void iGetResponse() {
        response.then().statusCode(200);
    }

    @And("The response has all the expected fields for the get users endpoint")
    public void responseHasExpectedFieldsForGetUsersEndpoint() {
        getResponse().as(User[].class);
    }
}
