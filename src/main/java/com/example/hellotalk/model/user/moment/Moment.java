package com.example.hellotalk.model.user.moment;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class Moment {

    UUID id;
    String text;
}
