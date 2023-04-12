package com.example.hellotalk.steps.definitions;

import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.model.user.User;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class HelloTalkStepDef extends BaseClass {

    private Response response;
    private final String BASE_PATH = "users/";

    private List<UserEntity> userDBList;

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

    @Then("I access the users data from the database for a list of users")
    public void accessDBForUsersData() {
        userDBList = dbClient.getUserDBDataForList();
    }

    @Then("I validate the api data against the DB for the users endpoint")
    public void validateApiVsDBForUsersEndpoint() {

        User[] usersApi = response.as(User[].class);
        List<User> userApiList = Arrays.asList(usersApi);

        for (int i = 0; i < userDBList.size(); i++) {
            assertThat(userApiList.get(i))
                    .describedAs("Users API does not match DB for field '%s'")
                    .usingRecursiveComparison()
                    .ignoringFields("followerOf", "followedBy", "hometown", "hobbyAndInterests")
                    .isEqualTo(userDBList.get(i));
        }

    }

}
