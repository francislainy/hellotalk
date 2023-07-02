package com.example.hellotalk.steps.moment;

import com.example.hellotalk.client.RestClient;
import com.example.hellotalk.model.moment.Moment;
import com.example.hellotalk.steps.ApiStep;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.restassured.specification.RequestSpecification;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;

import java.util.Arrays;
import java.util.List;
@RequiredArgsConstructor
@Data
public class MomentStep {

    private final MomentContext mc;

    private final ApiStep apiStep;
    private final RestClient restClient;

    @And("the user creates a moment with some basic and simple content")
    public void theUserCreatesAMomentWithSomeBasicAndSimpleContent() {
        Moment moment = Moment.builder().text("A great day testing").build();

        RequestSpecification rq = restClient.getRequestSpecification();
        apiStep.setResponse(rq.body(moment).post("/api/v1/ht/moments/"));
    }

    @Then("the moment should be created successfully")
    public void iGetResponse() {
        apiStep.getResponse().then().statusCode(201);
    }

    @And("the user should be able to see the moment in their list of moments")
    public void userSeeMomentInMomentList() {
        Moment moment = apiStep.getResponse().as(Moment.class);

        RequestSpecification rq = restClient.getRequestSpecification();
        apiStep.setResponse(rq.get("/api/v1/ht/moments/"));

        List<Moment> momentList = Arrays.asList(apiStep.getResponse().as(Moment[].class));

        boolean isMomentFound = momentList.stream().anyMatch(m -> m.getId().equals(moment.getId()));
        Assertions.assertTrue(isMomentFound);
    }
}
