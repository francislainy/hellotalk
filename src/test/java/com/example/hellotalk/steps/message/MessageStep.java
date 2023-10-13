package com.example.hellotalk.steps.message;

import com.example.hellotalk.model.message.Message;
import com.example.hellotalk.steps.ApiStep;
import com.example.hellotalk.steps.user.UserContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor
@Data
public class MessageStep {

    private final ApiStep apiStep;
    private final MessageContext mc;

    private final UserContext userContext;

    @Given("the user sends a message to another user")
    public void sendMessageToAnotherUser() {
        Message moment = Message.builder().content("text").userToId(userContext.getUserDB().getId()).build();

        RequestSpecification rq = apiStep.getRqWithAuth();
        Response response = rq.body(moment).post("/api/v1/ht/messages/");
        assertEquals(201, response.getStatusCode());

        apiStep.setResponse(response);
        mc.setMessage(response.as(Message.class));
    }

    @And("the user creates a message with content {string}")
    public void theUserCreatesAMessageWithContent(String text) {
        Message message = Message.builder().content(text).userToId(userContext.getUserDB().getId()).build();

        RequestSpecification rq = apiStep.getRqWithAuth();
        Response response = rq.body(message).post("/api/v1/ht/messages/");
        assertEquals(201, response.getStatusCode());

        apiStep.setResponse(response);
        mc.setMessage(response.as(Message.class));
    }

    @And("the user edits the text for the message to {string}")
    public void theUserEditsAMomentWithSomeContent(String text) {
        mc.getMessage().setContent(text);

        RequestSpecification rq = apiStep.getRqWithAuth();
        Response response = rq.body(mc.getMessage()).put("/api/v1/ht/messages/" + mc.getMessage().getId());
        assertEquals(200, response.getStatusCode());

        apiStep.setResponse(response);
        mc.setMessage(response.as(Message.class));
        assertEquals(text, mc.getMessage().getContent());
    }

    @And("the authenticated user attempts to edit the message that belongs to user {string}")
    public void theUserAttemptsToEditAMomentWithSomeContent(String text) {
        mc.getMessage().setContent(text);

        RequestSpecification rq = apiStep.getRqWithAuth();
        apiStep.setResponse(rq.body(mc.getMessage()).put("/api/v1/ht/messages/" + mc.getMessage().getId()));
    }

    @And("the user deletes the message")
    public void theUserDeletesTheMessage() {
        RequestSpecification rq = apiStep.getRqWithAuth();
        Response response = rq.delete("/api/v1/ht/messages/" + mc.getMessage().getId());
        assertEquals(204, response.getStatusCode());
    }

    @And("the user attempts to delete the message")
    public void theUserAttemptsToDeleteTheMessage() {
        RequestSpecification rq = apiStep.getRqWithAuth();
        rq.delete("/api/v1/ht/messages/" + mc.getMessage().getId());
    }

    @And("the message should no longer exist in the system")
    public void theMomentShouldNoLongerExistInTheSystem() {
        RequestSpecification rq = apiStep.getRqWithAuth();
        Response response = rq.get("/api/v1/ht/messages/" + mc.getMessage().getId());
        assertEquals(404, response.getStatusCode());
    }

    @And("the message should still exist in the system")
    public void theMessageShouldStillExistInTheSystem() {
        RequestSpecification rq = apiStep.getRqWithAuth();
        Response response = rq.get("/api/v1/ht/messages/" + mc.getMessage().getId());
        assertEquals(200, response.getStatusCode());
    }

    @And("the message should have its text updated successfully across the whole system")
    public void theMessageShouldHaveItsTextUpdatedSuccessfully() {
        RequestSpecification rq = apiStep.getRqWithAuth();
        Response response = rq.body(mc.getMessage()).put("/api/v1/ht/messages/" + mc.getMessage().getId());
        assertEquals(200, response.getStatusCode());

        apiStep.setResponse(response);

        Message moment = response.as(Message.class);
        assertEquals(mc.getMessage().getContent(), moment.getContent());

        mc.setMessage(moment);
    }
}
