package com.example.hellotalk.steps.definitions;

import com.example.hellotalk.model.user.User;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;

public class HelloTalkStepDef extends BaseClass {

    private Response response;
    private final String BASE_PATH = "users/";

    @Given("I access the users endpoint")
    public void iAccessTheUsersEndpoint() {
        response = restClient.getRequest(BASE_PATH);
    }

    @Then("I get a {int} successful response")
    public void iGetResponse(int responseCode) {
        response.then().statusCode(responseCode);
    }

    @Then("The response has all the expected fields for the users endpoint")
    public void responseHasExpectedUsersFields() {
        response.as(User[].class);
    }
}
