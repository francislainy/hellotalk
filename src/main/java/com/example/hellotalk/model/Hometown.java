package com.example.hellotalk.model;

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
public class Hometown {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID id;
    private String city;
    private String country;

}
