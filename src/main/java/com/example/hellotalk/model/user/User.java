package com.example.hellotalk.model.user;

import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.model.HobbyAndInterest;
import com.example.hellotalk.model.Hometown;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.example.hellotalk.model.Hometown.buildHometownFromEntity;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID id;
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

    public static User buildUserFromEntity(UserEntity userEntity) {

        Set<HobbyAndInterest> hobbyAndInterests = new HashSet<>();
        userEntity.getHobbyAndInterestEntities().forEach(h -> hobbyAndInterests.add(HobbyAndInterest.builder()
                .id(h.getId())
                .title(h.getTitle())
                .build()));

        return User.builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .dob(userEntity.getDob())
                .gender(userEntity.getGender())
                .subscriptionType(userEntity.getSubscriptionType())
                .creationDate(userEntity.getCreationDate())
                .handle(userEntity.getHandle())
                .status(userEntity.getStatus())
                .nativeLanguage(userEntity.getNativeLanguage())
                .targetLanguage(userEntity.getTargetLanguage())
                .occupation(userEntity.getOccupation())
                .selfIntroduction(userEntity.getSelfIntroduction())
                .placesToVisit(userEntity.getPlacesToVisit())
                .hometown(buildHometownFromEntity(userEntity.getHometownEntity()))
                .hobbyAndInterests(hobbyAndInterests)
                .build();
    }
}
