package com.example.hellotalk.steps;

import com.example.hellotalk.client.DBClient;
import com.example.hellotalk.client.RestClient;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.model.Hometown;
import com.example.hellotalk.repository.UserRepository;
import com.example.hellotalk.steps.user.UserContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.it.Data;
import io.cucumber.spring.ScenarioScope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.example.hellotalk.entity.user.HometownEntity.buildHometownEntity;

@Slf4j
@RequiredArgsConstructor
public class DBStep {

    private final DBClient dbClient;
    private final RestClient restClient;
    private final UserRepository userRepository;
    private final UserContext uc;

    @Given("I add a user to the DB with username {} and password {}")
    public void iAddUserToDB(String username, String password) {
        UserEntity userEntity = userRepository.findByUsername(username);
        if (userEntity == null) {
            userEntity = getUserEntity();
            userEntity.setUsername(username);
            userEntity.setPassword(password);
            userEntity = userRepository.save(userEntity);
        }

        uc.setUserDB(userEntity);
    }

    @Given("I access the users DB data")
    public void iAccessUsersDBData() {
        uc.setUserListDB(dbClient.getUsers());
    }

    @Given("an authenticated user with username {string} and password {string} logs into the system")
    public void userWithUsernameLogsIntoSystem(String username, String password) {
        restClient.getRequestSpecification().and().auth().basic(username, password);
    }

    private UserEntity getUserEntity() {
        Hometown hometown = Hometown.builder().city("anyCity").country("anyCountry").build();
        return UserEntity.builder()
                .username("anyUsername")
                .password("anyPassword")
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
