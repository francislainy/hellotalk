package com.example.hellotalk.repository.user;

import com.example.hellotalk.entity.followship.FollowshipEntity;
import com.example.hellotalk.entity.user.HobbyAndInterestEntity;
import com.example.hellotalk.entity.user.HometownEntity;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.repository.HobbyAndInterestRepository;
import com.example.hellotalk.repository.HometownRepository;
import com.example.hellotalk.repository.followship.FollowshipRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FollowshipRepository followshipRepository;
    @Autowired
    private HometownRepository hometownRepository;
    @Autowired
    private HobbyAndInterestRepository hobbyAndInterestRepository;

    @Test
    void testGetUser() {

        UserEntity userEntity = saveUserEntity();

        assertTrue(userRepository.findAll().size() > 0);
        userEntity = userRepository.findById(userEntity.getId()).orElse(null);
        assertNotNull(userEntity);

        UserEntity finalUserEntity = userEntity;
        assertAll(
                () -> assertEquals("anyName", finalUserEntity.getName()),
                () -> assertEquals("anyDob", finalUserEntity.getDob()),
                () -> assertEquals("anyGender", finalUserEntity.getGender()),
                () -> assertEquals("anySubscriptionType", finalUserEntity.getSubscriptionType()),
                () -> assertEquals("anyHandle", finalUserEntity.getHandle()),
                () -> assertEquals("anyStatus", finalUserEntity.getStatus()),
                () -> assertEquals("anyCreationDate", finalUserEntity.getCreationDate()),
                () -> assertEquals("anyNativeLanguage", finalUserEntity.getNativeLanguage()),
                () -> assertEquals("anyTargetLanguage", finalUserEntity.getTargetLanguage()),
                () -> assertEquals("anySelfIntroduction", finalUserEntity.getSelfIntroduction()),
                () -> assertEquals("anyCity", finalUserEntity.getHometownEntity().getCity()),
                () -> assertEquals("anyCountry", finalUserEntity.getHometownEntity().getCountry()),
                () -> assertEquals("anyOccupation", finalUserEntity.getOccupation()),
                () -> assertEquals("anyPlacesToVisit", finalUserEntity.getPlacesToVisit()),
                () -> assertTrue(finalUserEntity.getFollowedByEntity().size() > 0),
                () -> assertTrue(finalUserEntity.getFollowerOfEntity().size() > 0),
                () -> assertTrue(finalUserEntity.getHobbyAndInterestEntities().size() > 0));

        finalUserEntity.getHobbyAndInterestEntities().forEach(h -> assertEquals("anyInterest", h.getTitle()));
    }

    @Test
    void testEditUser() {

        UserEntity userEntity = saveUserEntity();
        UUID originalUserId = userEntity.getId();

        final HobbyAndInterestEntity[] hobbyAndInterestEntityOriginal = new HobbyAndInterestEntity[1];
        userEntity.getHobbyAndInterestEntities().forEach(h -> hobbyAndInterestEntityOriginal[0] = h);

        HometownEntity hometownEntity = getUpdatedHometown(userEntity);
        userEntity = getUpdatedUserEntity(userEntity);
        UUID updatedUserId = userEntity.getId();
        final HobbyAndInterestEntity[] hobbyAndInterestEntityUpdated = new HobbyAndInterestEntity[1];
        userEntity.getHobbyAndInterestEntities().forEach(h -> hobbyAndInterestEntityUpdated[0] = h);

        UserEntity finalUserEntity = userEntity;
        assertAll(
                () -> assertEquals("anyUpdatedName", finalUserEntity.getName()),
                () -> assertEquals(originalUserId, updatedUserId),
                () -> assertEquals("anyUpdatedDob", finalUserEntity.getDob()),
                () -> assertEquals("anyUpdatedGender", finalUserEntity.getGender()),
                () -> assertEquals("anyUpdatedSubscriptionType", finalUserEntity.getSubscriptionType()),
                () -> assertEquals("anyUpdatedHandle", finalUserEntity.getHandle()),
                () -> assertEquals("anyUpdatedStatus", finalUserEntity.getStatus()),
                () -> assertEquals("anyUpdatedCreationDate", finalUserEntity.getCreationDate()),
                () -> assertEquals("anyUpdatedNativeLanguage", finalUserEntity.getNativeLanguage()),
                () -> assertEquals("anyUpdatedTargetLanguage", finalUserEntity.getTargetLanguage()),
                () -> assertEquals("anyUpdatedSelfIntroduction", finalUserEntity.getSelfIntroduction()),
                () -> assertEquals(hometownEntity.getId(), finalUserEntity.getHometownEntity().getId()),
                () -> assertEquals("anyUpdatedCity", finalUserEntity.getHometownEntity().getCity()),
                () -> assertEquals("anyUpdatedCountry", finalUserEntity.getHometownEntity().getCountry()),
                () -> assertEquals("anyUpdatedCountry", finalUserEntity.getHometownEntity().getCountry()),
                () -> assertEquals("anyUpdatedOccupation", finalUserEntity.getOccupation()),
                () -> assertEquals("anyUpdatedPlacesToVisit", finalUserEntity.getPlacesToVisit()),
                () -> assertTrue(finalUserEntity.getFollowedByEntity().size() > 0),
                // user who already has followers or follows someone should not have this changed after an update
                () -> assertTrue(finalUserEntity.getFollowerOfEntity().size() > 0),
                () -> assertTrue(finalUserEntity.getHobbyAndInterestEntities().size() > 0));

        finalUserEntity.getHobbyAndInterestEntities().forEach(h -> assertEquals("anyUpdatedInterest", h.getTitle()));
        finalUserEntity.getHobbyAndInterestEntities().forEach(h -> assertEquals(hobbyAndInterestEntityOriginal[0].getId(), hobbyAndInterestEntityUpdated[0].getId()));
    }

    @Test
    void testDeleteUser() {

        UserEntity userEntity = saveUserEntity();

        assertTrue(userRepository.findAll().size() > 0);
        userEntity = userRepository.findById(userEntity.getId()).orElse(null);
        assertNotNull(userEntity);

        userRepository.delete(userEntity);

        assertTrue(userRepository.findById(userEntity.getId()).isEmpty());
    }

    // Helpers
    @NotNull
    private UserEntity saveUserEntity() {

        UserEntity userFromEntity = UserEntity.builder().name("anySender").build();
        UserEntity userToEntity = UserEntity.builder().name("anyReceiver").build();
        userFromEntity = userRepository.save(userFromEntity);
        userToEntity = userRepository.save(userToEntity);

        FollowshipEntity followshipEntity = FollowshipEntity.builder().userFromEntity(userFromEntity).userToEntity(userToEntity).build();
        followshipRepository.save(followshipEntity);

        FollowshipEntity followerOfEntity = FollowshipEntity.builder().userToEntity(userToEntity).build();
        Set<FollowshipEntity> followerOfEntities = new HashSet<>();
        followerOfEntities.add(followerOfEntity);

        FollowshipEntity followedByEntity = FollowshipEntity.builder().userFromEntity(userFromEntity).build();
        Set<FollowshipEntity> followedByEntities = new HashSet<>();
        followedByEntities.add(followedByEntity);

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
                .gender("anyGender")
                .subscriptionType("anySubscriptionType")
                .creationDate("anyCreationDate")
                .handle("anyHandle")
                .status("anyStatus")
                .nativeLanguage("anyNativeLanguage")
                .targetLanguage("anyTargetLanguage")
                .selfIntroduction("anySelfIntroduction")
                .occupation("anyOccupation")
                .placesToVisit("anyPlacesToVisit")
                .hometownEntity(hometownEntity)
                .hobbyAndInterestEntities(hobbyAndInterestEntities)
                .followedByEntity(followedByEntities)
                .followerOfEntity(followerOfEntities)
                .build();
        userEntity = userRepository.save(userEntity);

        Set<UserEntity> userEntitySet = new HashSet<>();
        hobbyAndInterestEntity.setUserEntities(userEntitySet);
        return userEntity;
    }

    @NotNull
    private UserEntity getUpdatedUserEntity(UserEntity userEntity) {
        HometownEntity hometownEntity = getUpdatedHometown(userEntity);
        Set<HobbyAndInterestEntity> hobbyAndInterestEntities = new HashSet<>();
        hobbyAndInterestEntities.add(getUpdatedHobbyAndInterest(userEntity));

        userEntity.setName("anyUpdatedName");
        userEntity.setDob("anyUpdatedDob");
        userEntity.setGender("anyUpdatedGender");
        userEntity.setSubscriptionType("anyUpdatedSubscriptionType");
        userEntity.setCreationDate("anyUpdatedCreationDate");
        userEntity.setHandle("anyUpdatedHandle");
        userEntity.setStatus("anyUpdatedStatus");
        userEntity.setNativeLanguage("anyUpdatedNativeLanguage");
        userEntity.setTargetLanguage("anyUpdatedTargetLanguage");
        userEntity.setSelfIntroduction("anyUpdatedSelfIntroduction");
        userEntity.setOccupation("anyUpdatedOccupation");
        userEntity.setPlacesToVisit("anyUpdatedPlacesToVisit");
        userEntity.setHometownEntity(hometownEntity);
        userEntity.setHobbyAndInterestEntities(hobbyAndInterestEntities);

        userEntity = userRepository.save(userEntity);
        return userEntity;
    }

    @NotNull
    private HometownEntity getUpdatedHometown(UserEntity userEntity) {
        HometownEntity hometownEntity = userEntity.getHometownEntity();
        hometownEntity.setCity("anyUpdatedCity");
        hometownEntity.setCountry("anyUpdatedCountry");
        hometownEntity = hometownRepository.save(hometownEntity);
        return hometownEntity;
    }

    @NotNull
    private HobbyAndInterestEntity getUpdatedHobbyAndInterest(UserEntity userEntity) {
        final HobbyAndInterestEntity[] hobbyAndInterestEntityOriginal = new HobbyAndInterestEntity[1];
        userEntity.getHobbyAndInterestEntities().forEach(h -> hobbyAndInterestEntityOriginal[0] = h);

        HobbyAndInterestEntity hobbyAndInterestEntity = hobbyAndInterestEntityOriginal[0];
        hobbyAndInterestEntity.setTitle("anyUpdatedInterest");
        hobbyAndInterestEntity = hobbyAndInterestRepository.save(hobbyAndInterestEntity);
        return hobbyAndInterestEntity;
    }
}
