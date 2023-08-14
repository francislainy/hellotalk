package com.example.hellotalk.model.user;

import com.example.hellotalk.model.HobbyAndInterest;
import com.example.hellotalk.model.Hometown;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID id;
    private String username;
    private String password;
    private String name;
    private String dob;
    private String gender;
    private String selfIntroduction;
    private String creationDate;
    private String status;
    private String handle;
    private String nativeLanguage;
    private String targetLanguage;
    private String occupation;
    private String placesToVisit;
    private String subscriptionType;

    private Hometown hometown;
    private Set<HobbyAndInterest> hobbyAndInterests;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<User> followedBy;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<User> followerOf;
}
