package com.example.hellotalk.model.user;

import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.model.HobbyAndInterest;
import com.example.hellotalk.model.Hometown;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private static final ModelMapper modelMapper = new ModelMapper();

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
        User user = modelMapper.map(userEntity, User.class);

        if (userEntity.getHobbyAndInterestEntities() != null) {
            Set<HobbyAndInterest> hobbyAndInterests = new HashSet<>();
            userEntity.getHobbyAndInterestEntities().forEach(h -> hobbyAndInterests.add(modelMapper.map(h, HobbyAndInterest.class)));
            user.setHobbyAndInterests(hobbyAndInterests);
        }

        return user;
    }
}
