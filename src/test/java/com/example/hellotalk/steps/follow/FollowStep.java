package com.example.hellotalk.steps.follow;

import com.example.hellotalk.client.RestClient;
import com.example.hellotalk.model.FollowingRequest;
import com.example.hellotalk.repository.UserRepository;
import com.example.hellotalk.steps.ApiStep;
import com.example.hellotalk.steps.user.UserContext;
import io.cucumber.java.en.And;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor
@Data
public class FollowStep {

    private final FollowContext mc;
    private final UserContext uc;

    private final ApiStep apiStep;
    private final RestClient restClient;

    private final UserRepository userRepository;

    @And("the authenticated user triggers the request to follow another user")
    public void theUserTriggersTheFollowingRequest() {
        UUID userFromId = uc.getUserDB().getId();
        UUID userToId = uc.getSecondUserDB().getId();

        FollowingRequest moment = FollowingRequest.builder()
                .userFromId(userFromId)
                .userToId(userToId)
                .build();

        RequestSpecification rq = apiStep.getRqWithAuth();
        Response response = rq.body(moment).post("/api/v1/ht/follow/");
        assertEquals(201, response.getStatusCode());

        apiStep.setResponse(response);
    }

    @And("the follower user should have their list of users they follow updated")
    public void theListOfFolloweesShouldUpdate() {
        UUID userFromId = uc.getUserDB().getId();
        UUID userToId = uc.getSecondUserDB().getId();

        RequestSpecification rq = apiStep.getRqWithAuth();
        Response response = rq.get("/api/v1/ht/follow/from/user/" + userFromId);
        assertEquals(200, response.getStatusCode());

        List<FollowingRequest> followingRequestList = Arrays.asList(response.as(FollowingRequest[].class));

        assertEquals(userToId.toString(), followingRequestList.get(0).getUserToId().toString());

        apiStep.setResponse(response);
    }

    @And("the followed user should have their list of followers updated to include the new follower")
    public void theListOfFollowersShouldUpdate() {
        UUID userFromId = uc.getUserDB().getId();
        UUID userToId = uc.getSecondUserDB().getId();

        RequestSpecification rq = apiStep.getRqWithAuth();
        Response response = rq.get("/api/v1/ht/follow/to/user/" + userToId);
        assertEquals(200, response.getStatusCode());

        List<FollowingRequest> followingRequestList = Arrays.asList(response.as(FollowingRequest[].class));

        assertEquals(userFromId.toString(), followingRequestList.get(0).getUserToId().toString());

        apiStep.setResponse(response);
    }
}
