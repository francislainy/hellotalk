package com.example.hellotalk.model.followship;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Followship {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID id;
    private UUID userToId;
    private UUID userFromId;

}
