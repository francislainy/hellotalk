package com.example.hellotalk.repository;

import com.example.hellotalk.entity.user.HobbyAndInterestEntity;
import com.example.hellotalk.entity.user.HometownEntity;
import com.example.hellotalk.entity.user.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest extends PostgresContainer { // We can use both this class or BaseIntegrationClass

    @Autowired UserRepository userRepository;
    @Autowired HometownRepository hometownRepository;
    @Autowired HobbyAndInterestRepository hobbyAndInterestRepository;

    @Test
    void getUser() {

        HometownEntity hometownEntity = HometownEntity.builder().city("anyCity").country("anyCountry").build();

        hometownRepository.save(hometownEntity);

        HobbyAndInterestEntity hobbyAndInterestEntity = HobbyAndInterestEntity.builder()
                .title("anyInterest")
                .build();
        hobbyAndInterestEntity = hobbyAndInterestRepository.save(hobbyAndInterestEntity);
        Set<HobbyAndInterestEntity> hobbyAndInterestEntities = new HashSet<>();
        hobbyAndInterestEntities.add(hobbyAndInterestEntity);

        UserEntity userEntity = UserEntity.builder()
                .name("anyName")
                .dob("anyDob")
                .nativeLanguage("anyNativeLanguage")
                .targetLanguage("anyTargetLanguage")
                .selfIntroduction("anySelfIntroduction")
                .occupation("anyOccupation")
                .placesToVisit("anyPlacesToVisit")
                .hometownEntity(hometownEntity)
                .hobbyAndInterestEntities(hobbyAndInterestEntities)
                .build();
        userEntity = userRepository.save(userEntity);
        
        Set<UserEntity> userEntitySet = new HashSet<>();
        hobbyAndInterestEntity.setUserEntities(userEntitySet);

        userEntity = userRepository.findById(userEntity.getId()).orElse(null);
        assertTrue(userRepository.findAll().size() > 0);
        assertNotNull(userEntity);
        assertNotNull(userRepository.findById(userEntity.getId()));

        UserEntity finalUserEntity = userEntity;
        assertAll(
                () -> assertEquals("anyName", finalUserEntity.getName()),
                () -> assertEquals("anyDob", finalUserEntity.getDob()),
                () -> assertEquals("anyNativeLanguage", finalUserEntity.getNativeLanguage()),
                () -> assertEquals("anyTargetLanguage", finalUserEntity.getTargetLanguage()),
                () -> assertEquals("anySelfIntroduction", finalUserEntity.getSelfIntroduction()),
                () -> assertEquals("anyCity", finalUserEntity.getHometownEntity().getCity()),
                () -> assertEquals("anyCountry", finalUserEntity.getHometownEntity().getCountry()),
                () -> assertEquals("anyOccupation", finalUserEntity.getOccupation()),
                () -> assertTrue(finalUserEntity.getHobbyAndInterestEntities().size() > 0),
                () -> assertEquals("anyPlacesToVisit", finalUserEntity.getPlacesToVisit())
        );

        finalUserEntity.getHobbyAndInterestEntities().forEach(h -> assertEquals("anyInterest", h.getTitle()));
    }
}
