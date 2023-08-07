package com.example.hellotalk.steps.follow;

import com.example.hellotalk.model.comment.Comment;
import com.example.hellotalk.model.moment.Moment;
import io.cucumber.spring.ScenarioScope;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
@ScenarioScope
public class FollowContext {
    private Moment moment;
    private Moment updatedMoment;
    private Comment comment;
}
