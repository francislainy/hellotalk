package com.example.hellotalk.steps;

import com.example.hellotalk.client.DBClient;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.model.Hometown;
import com.example.hellotalk.repository.UserRepository;
import io.cucumber.java.en.Given;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.example.hellotalk.entity.user.HometownEntity.buildHometownEntity;

@Slf4j
@RequiredArgsConstructor
public class DBStep {

    private final DBClient dbClient;
    private final UserRepository userRepository;
    private final UserContext userContext;

    @Given("I add a user to the DB")
    public void iAddUserToDB() {
        userRepository.save(getUserEntity());
    }

    @Given("I access the users DB data")
    public void iAccessUsersDBData() {
        userContext.setUserListDB(dbClient.getUsers());
    }

    private UserEntity getUserEntity() {
        Hometown hometown = Hometown.builder().city("anyCity").country("anyCountry").build();
        return UserEntity.builder()
                .name("anyName")
                .dob("anyDob")
                .gender("anyGender")
                .creationDate("anyCreationDate")
                .handle("anyHandle")
                .status("anyStatus")
                .nativeLanguage("anyNativeLanguage")
                .targetLanguage("anyTargetLanguage")
                .occupation("anyOccupation")
                .selfIntroduction("anySelfIntroduction")
                .placesToVisit("anyPlacesToVisit")
                .subscriptionType("anySubscriptionType")
                .hometownEntity(buildHometownEntity(hometown))
                .build();
    }
}
