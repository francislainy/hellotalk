package com.example.hellotalk.steps.followship;

import com.example.hellotalk.client.RestClient;
import com.example.hellotalk.model.followship.Followship;
import com.example.hellotalk.repository.followship.FollowshipRepository;
import com.example.hellotalk.repository.user.UserRepository;
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
public class FollowshipStep {

    private final FollowshipContext mc;
    private final UserContext uc;

    private final ApiStep apiStep;
    private final RestClient restClient;

    private final UserRepository userRepository;
    private final FollowshipRepository followshipRepository;

    @And("the authenticated user triggers the request to follow another user")
    public void theUserTriggersTheFollowingRequestToStartFollowingAnotherUser() {
        UUID userFromId = uc.getUserDB().getId();
        UUID userToId = uc.getSecondUserDB().getId();

        Followship followship = Followship.builder().userFromId(userFromId).userToId(userToId).build();

        RequestSpecification rq = apiStep.getRqWithAuth();
        Response response = rq.body(followship).post("/api/v1/ht/followship/");
        assertEquals(201, response.getStatusCode());

        apiStep.setResponse(response);
    }

    @And("the authenticated user triggers the request to follow themself")
    public void theUserTriggersTheFollowingRequestToStartFollowingThemself() {
        UUID userFromId = uc.getUserDB().getId();

        Followship followship = Followship.builder().userFromId(userFromId).userToId(userFromId).build();

        RequestSpecification rq = apiStep.getRqWithAuth();
        Response response = rq.body(followship).post("/api/v1/ht/followship/");

        apiStep.setResponse(response);
    }

    @And("the authenticated user triggers the request to stop following the other user")
    public void theUserTriggersTheFollowingRequestToStopFollowingOtherUser() {
        UUID userFromId = uc.getUserDB().getId();
        UUID userToId = uc.getSecondUserDB().getId();

        Followship followship = Followship.builder().userFromId(userFromId).userToId(userToId).build();

        RequestSpecification rq = apiStep.getRqWithAuth();
        Response response = rq.body(followship).post("/api/v1/ht/followship/");
        assertEquals(206, response.getStatusCode());

        apiStep.setResponse(response);
    }

    @And("the follower user should have their list of users they follow updated to include the user they are following")
    public void theListOfFolloweesShouldUpdateToIncludeUser() {
        UUID userFromId = uc.getUserDB().getId();
        UUID userToId = uc.getSecondUserDB().getId();

        RequestSpecification rq = apiStep.getRqWithAuth();
        Response response = rq.get("/api/v1/ht/followship/from/user/" + userFromId);
        assertEquals(200, response.getStatusCode());

        List<Followship> followshipList = Arrays.asList(response.as(Followship[].class));
        assertEquals(userToId.toString(), followshipList.get(0).getUserToId().toString());

        apiStep.setResponse(response);
    }

    @And("the follower user should have their list of users they follow updated to not include the user they are no longer following")
    public void theListOfFolloweesShouldUpdateToNotIncludeUser() {
        UUID userFromId = uc.getUserDB().getId();

        RequestSpecification rq = apiStep.getRqWithAuth();
        Response response = rq.get("/api/v1/ht/followship/from/user/" + userFromId);
        assertEquals(200, response.getStatusCode());

        List<Followship> followshipList = Arrays.asList(response.as(Followship[].class));
        assertEquals(0, followshipList.size());
    }

    @And("the followed user should have their list of followers updated to include the new follower")
    public void theListOfFollowersShouldUpdateToIncludeRelationship() {
        UUID userFromId = uc.getUserDB().getId();
        UUID userToId = uc.getSecondUserDB().getId();

        RequestSpecification rq = apiStep.getRqWithAuth();
        Response response = rq.get("/api/v1/ht/followship/to/user/" + userToId);
        assertEquals(200, response.getStatusCode());

        List<Followship> followshipList = Arrays.asList(response.as(Followship[].class));
        assertEquals(userFromId.toString(), followshipList.get(0).getUserFromId().toString());

        apiStep.setResponse(response);
    }

    @And("the followed user should have their list of followers updated to not include the new follower")
    public void theListOfFollowersShouldUpdateToNotIncludeRelationship() {
        UUID userToId = uc.getSecondUserDB().getId();

        RequestSpecification rq = apiStep.getRqWithAuth();
        Response response = rq.get("/api/v1/ht/followship/to/user/" + userToId);
        assertEquals(200, response.getStatusCode());

        List<Followship> followshipList = Arrays.asList(response.as(Followship[].class));
        assertEquals(0, followshipList.size());
    }

    @And("I delete the followship")
    public void iDeleteTheFollowship() {
        Followship[] followships = apiStep.getResponse().as(Followship[].class);
        Followship followship = Arrays.stream(followships)
                .filter(fr -> fr.getUserFromId().toString().equals(uc.getUserDB().getId().toString()))
                .findFirst()
                .get();
        followshipRepository.deleteById(followship.getId());
    }

}
