package com.example.hellotalk.model.user;

import com.example.hellotalk.entity.user.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

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

    private Hometown hometown;
    private Set<HobbyAndInterest> hobbyAndInterests;

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
                .creationDate(userEntity.getCreationDate())
                .handle(userEntity.getHandle())
                .status(userEntity.getStatus())
                .nativeLanguage(userEntity.getNativeLanguage())
                .targetLanguage(userEntity.getTargetLanguage())
                .occupation(userEntity.getOccupation())
                .selfIntroduction(userEntity.getSelfIntroduction())
                .placesToVisit(userEntity.getPlacesToVisit())
                .hometown(Hometown.builder().city(userEntity.getHometownEntity().getCity()).country(userEntity.getHometownEntity().getCountry()).build())
                .hobbyAndInterests(hobbyAndInterests)
                .build();
    }
}
