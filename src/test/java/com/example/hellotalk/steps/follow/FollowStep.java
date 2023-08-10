package com.example.hellotalk.steps.follow;

import com.example.hellotalk.client.RestClient;
import com.example.hellotalk.model.FollowingRequest;
import com.example.hellotalk.repository.FollowingRequestRepository;
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
    private final FollowingRequestRepository followingRequestRepository;

    @And("the authenticated user triggers the request to follow another user")
    public void theUserTriggersTheFollowingRequestToStartFollowingAnotherUser() {
        UUID userFromId = uc.getUserDB().getId();
        UUID userToId = uc.getSecondUserDB().getId();

        FollowingRequest followingRequest = FollowingRequest.builder().userFromId(userFromId).userToId(userToId).build();

        RequestSpecification rq = apiStep.getRqWithAuth();
        Response response = rq.body(followingRequest).post("/api/v1/ht/follow/");
        assertEquals(201, response.getStatusCode());

        apiStep.setResponse(response);
    }

    @And("the authenticated user triggers the request to follow themself")
    public void theUserTriggersTheFollowingRequestToStartFollowingThemself() {
        UUID userFromId = uc.getUserDB().getId();

        FollowingRequest followingRequest = FollowingRequest.builder().userFromId(userFromId).userToId(userFromId).build();

        RequestSpecification rq = apiStep.getRqWithAuth();
        Response response = rq.body(followingRequest).post("/api/v1/ht/follow/");

        apiStep.setResponse(response);
    }

    @And("the authenticated user triggers the request to stop following the other user")
    public void theUserTriggersTheFollowingRequestToStopFollowingOtherUser() {
        UUID userFromId = uc.getUserDB().getId();
        UUID userToId = uc.getSecondUserDB().getId();

        FollowingRequest followingRequest = FollowingRequest.builder().userFromId(userFromId).userToId(userToId).build();

        RequestSpecification rq = apiStep.getRqWithAuth();
        Response response = rq.body(followingRequest).post("/api/v1/ht/follow/");
        assertEquals(206, response.getStatusCode());

        apiStep.setResponse(response);
    }


    @And("the follower user should have their list of users they follow updated to include the user they are following")
    public void theListOfFolloweesShouldUpdateToIncludeUser() {
        UUID userFromId = uc.getUserDB().getId();
        UUID userToId = uc.getSecondUserDB().getId();

        RequestSpecification rq = apiStep.getRqWithAuth();
        Response response = rq.get("/api/v1/ht/follow/from/user/" + userFromId);
        assertEquals(200, response.getStatusCode());

        List<FollowingRequest> followingRequestList = Arrays.asList(response.as(FollowingRequest[].class));
        assertEquals(userToId.toString(), followingRequestList.get(0).getUserToId().toString());

        apiStep.setResponse(response);
    }

    @And("the follower user should have their list of users they follow updated to not include the user they are no longer following")
    public void theListOfFolloweesShouldUpdateToNotIncludeUser() {
        UUID userFromId = uc.getUserDB().getId();

        RequestSpecification rq = apiStep.getRqWithAuth();
        Response response = rq.get("/api/v1/ht/follow/from/user/" + userFromId);
        assertEquals(200, response.getStatusCode());

        List<FollowingRequest> followingRequestList = Arrays.asList(response.as(FollowingRequest[].class));
        assertEquals(0, followingRequestList.size());
    }

    @And("the followed user should have their list of followers updated to include the new follower")
    public void theListOfFollowersShouldUpdateToIncludeRelationship() {
        UUID userFromId = uc.getUserDB().getId();
        UUID userToId = uc.getSecondUserDB().getId();

        RequestSpecification rq = apiStep.getRqWithAuth();
        Response response = rq.get("/api/v1/ht/follow/to/user/" + userToId);
        assertEquals(200, response.getStatusCode());

        List<FollowingRequest> followingRequestList = Arrays.asList(response.as(FollowingRequest[].class));
        assertEquals(userFromId.toString(), followingRequestList.get(0).getUserFromId().toString());

        apiStep.setResponse(response);
    }

    @And("the followed user should have their list of followers updated to not include the new follower")
    public void theListOfFollowersShouldUpdateToNotIncludeRelationship() {
        UUID userToId = uc.getSecondUserDB().getId();

        RequestSpecification rq = apiStep.getRqWithAuth();
        Response response = rq.get("/api/v1/ht/follow/to/user/" + userToId);
        assertEquals(200, response.getStatusCode());

        List<FollowingRequest> followingRequestList = Arrays.asList(response.as(FollowingRequest[].class));
        assertEquals(0, followingRequestList.size());
    }

    @And("I delete the following relationship")
    public void iDeleteTheRelationship() {
        FollowingRequest[] followingRequests = apiStep.getResponse().as(FollowingRequest[].class);
        FollowingRequest followingRequest = Arrays.stream(followingRequests)
                .filter(fr -> fr.getUserFromId().toString().equals(uc.getUserDB().getId().toString()))
                .findFirst()
                .get();
        followingRequestRepository.deleteById(followingRequest.getId());
    }

}
