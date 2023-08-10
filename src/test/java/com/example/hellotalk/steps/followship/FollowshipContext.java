package com.example.hellotalk.steps.followship;

import com.example.hellotalk.model.comment.Comment;
import com.example.hellotalk.model.moment.Moment;
import io.cucumber.spring.ScenarioScope;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
@ScenarioScope
public class FollowshipContext {
    private Moment moment;
    private Moment updatedMoment;
    private Comment comment;
}
