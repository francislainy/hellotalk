package com.example.hellotalk.steps;

import com.example.hellotalk.client.RestClient;
import com.example.hellotalk.model.user.User;
import com.example.hellotalk.steps.user.UserContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import static com.example.hellotalk.config.Constants.PASSWORD;
import static com.example.hellotalk.config.Constants.USERNAME;

@Data
@RequiredArgsConstructor
public class ApiStep {

    private final RestClient restClient;

    private Response response;

    private final UserContext uc;

    @Given("I access the get users endpoint")
    public void iAccessGetUsersEndpoint() {
        RequestSpecification rq = restClient.getRequestSpecification()
                .auth().basic(USERNAME, PASSWORD);
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

    public RequestSpecification getRqWithAuth() {
        return restClient.getRequestSpecification().auth().basic(uc.getUserDB().getUsername(), uc.getUserDB().getPassword());
    }

}
