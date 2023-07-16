package com.example.hellotalk.steps.moment;

import com.example.hellotalk.client.RestClient;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.model.comment.Comment;
import com.example.hellotalk.model.moment.Moment;
import com.example.hellotalk.repository.UserRepository;
import com.example.hellotalk.steps.ApiStep;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

import static com.mongodb.internal.connection.tlschannel.util.Util.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor
@Data
public class MomentStep {

    private final MomentContext mc;

    private final ApiStep apiStep;
    private final RestClient restClient;

    private final UserRepository userRepository;

    @And("the user creates a moment with some basic and simple content")
    public void theUserCreatesAMomentWithSomeBasicAndSimpleContent() {
        Moment moment = Moment.builder().text("A great day testing").build();

        RequestSpecification rq = apiStep.getRqWithAuth();
        Response response = rq.body(moment).post("/api/v1/ht/moments/");
        assertEquals(201, response.getStatusCode());

        apiStep.setResponse(response);
        mc.setMoment(response.as(Moment.class));
    }

    @And("the user creates a moment with content {string}")
    public void theUserCreatesAMomentWithSomeContent(String text) {
        Moment moment = Moment.builder().text(text).build();

        RequestSpecification rq = apiStep.getRqWithAuth();
        Response response = rq.body(moment).post("/api/v1/ht/moments/");
        assertEquals(201, response.getStatusCode());

        apiStep.setResponse(response);
        mc.setMoment(response.as(Moment.class));
    }

    @Then("^the (.*) should be created successfully$")
    public void iGe201tResponse(String item) {
        apiStep.getResponse().then().statusCode(201);
    }

    @Then("the request works fine")
    public void iGet200Response() {
        apiStep.getResponse().then().statusCode(200);
    }

    @And("^the user should be able to see the moment in their list of moments$")
    public void userSeeMomentInMomentList() {
        RequestSpecification rq = apiStep.getRqWithAuth();
        apiStep.setResponse(rq.get("/api/v1/ht/moments/"));

        List<Moment> momentList = Arrays.asList(apiStep.getResponse().as(Moment[].class));

        boolean isMomentFound = momentList.stream().anyMatch(m -> m.getId().equals(mc.getMoment().getId()));
        assertTrue(isMomentFound);
    }

    @And("the user adds a comment to the moment with grammar correction {string}")
    public void userAddsCommentToMoment(String text) {
        Comment comment = Comment.builder().text(text).build();

        RequestSpecification rq = apiStep.getRqWithAuth();
        Response response = rq.body(comment).post("/api/v1/ht/moments/" + mc.getMoment().getId() + "/comments");
        apiStep.setResponse(response);

        assertEquals(201, apiStep.getResponse().getStatusCode(), "Comment added successfully");
        mc.setComment(response.as(Comment.class));
    }

    @And("the comment should be added to the list of comments for that moment")
    public void commentShouldBeAddedToTheListOfComments() {
        RequestSpecification rq = apiStep.getRqWithAuth();
        Response response = rq.get("/api/v1/ht/moments/" + mc.getMoment().getId() + "/comments");
        apiStep.setResponse(response);
        List<Comment> commentList = Arrays.asList(response.as(Comment[].class));

        boolean hasFoundComment = commentList.stream().anyMatch(comment -> comment.getMomentId().equals(mc.getMoment().getId()));

        assertTrue(hasFoundComment);
    }

    @When("the user with username {string} likes the moment")
    public void theUserLikesTheMoment(String username) {
        Moment moment = apiStep.getResponse().as(Moment.class);
        UserEntity userEntity = userRepository.findByUsername(username);

        RequestSpecification rq = restClient.getRequestSpecification().auth().basic(userEntity.getUsername(), userEntity.getPassword());
        apiStep.setResponse(rq.post("/api/v1/ht/moments/" + moment.getId() + "/like"));
    }

    @When("the moment should receive a like from the user")
    public void likeMoment(String username) {
        RequestSpecification rq = apiStep.getRqWithAuth();;
        Response response = rq.get("/api/v1/ht/moments/" + mc.getMoment().getId());
        apiStep.setResponse(response);

        mc.setUpdatedMoment(response.as(Moment.class));

        UserEntity userEntity = userRepository.findByUsername(username);
        assertTrue(mc.getUpdatedMoment().getLikedByIds().contains(userEntity.getId()));
    }

    @When("the total number of likes for the moment should increase")
    public void momentNumberIncreases() {
        assertTrue(mc.getUpdatedMoment().getNumLikes() == mc.getMoment().getNumLikes() + 1);
    }

    @When("the user removes his like for the moment")
    public void removeLike() {
        mc.setMoment(mc.getUpdatedMoment());
        RequestSpecification rq = apiStep.getRqWithAuth();
        Response response = rq.get("/api/v1/ht/moments/" + mc.getMoment().getId());

        apiStep.setResponse(response);
        mc.setUpdatedMoment(response.as(Moment.class));
    }

    @When("the total number of likes for the moment should decrease")
    public void removingLikeUpdatesMoment() {
        assertTrue(mc.getUpdatedMoment().getNumLikes() == mc.getMoment().getNumLikes() - 1);
    }
}
