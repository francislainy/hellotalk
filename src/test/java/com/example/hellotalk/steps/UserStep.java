package com.example.hellotalk.steps;

import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.model.user.User;
import io.cucumber.java.en.Then;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class UserStep {

    private final ApiStep apiStep;
    private final UserContext uc;

    @Then("I validate the response for the get users endpoint against the database")
    public void iValidateResponseForGetUsersEndpointAgainstTheDatabase() {
        List<User> userListApi = Arrays.asList(apiStep.getResponse().as(User[].class));
        List<UserEntity> userListDB = uc.getUserListDB();

        for (UserEntity userDB : userListDB) {

            Optional<User> matchingUser = findApiUserMatchingDB(userListApi, userDB);

            if (matchingUser.isPresent()) {
                User userApi = matchingUser.get();

                assertThat(userApi)
                        .usingRecursiveComparison()
                        .ignoringFields("hometown", "hobbyAndInterests", "followerOf", "followedBy")
                        .isEqualTo(userDB);
            }
        }
    }

    private Optional<User> findApiUserMatchingDB(List<User> userListApi, UserEntity userDB) {
        return userListApi.stream().filter(userApi -> userApi.getId().equals(userDB.getId())).findFirst();
    }

}
