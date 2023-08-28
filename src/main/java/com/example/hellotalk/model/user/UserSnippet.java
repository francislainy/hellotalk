package com.example.hellotalk.model.user;

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
public class UserSnippet {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID id;
    private String username;
    private String name;
    private String gender;
    private String creationDate;
    private String status;
    private String nativeLanguage;
    private String targetLanguage;
}
