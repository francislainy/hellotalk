package com.example.hellotalk.repository.followship;

import com.example.hellotalk.entity.followship.FollowshipEntity;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FollowshipRepositoryTest {

    @Autowired
    private FollowshipRepository followshipRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindFollowshipsSentByUser_UserFrom() {

        UserEntity userFromEntity = userRepository.save(new UserEntity());
        UserEntity userToEntity1 = userRepository.save(new UserEntity());
        UserEntity userToEntity2 = userRepository.save(new UserEntity());

        FollowshipEntity followship1 = FollowshipEntity.builder().userFromEntity(userFromEntity).userToEntity(userToEntity1).build();
        FollowshipEntity followship2 = FollowshipEntity.builder().userFromEntity(userFromEntity).userToEntity(userToEntity2).build();

        followshipRepository.save(followship1);
        followshipRepository.save(followship2);

        List<FollowshipEntity> followships = followshipRepository.findFollowshipsByUserFromId(userFromEntity.getId());

        assertEquals(2, followships.size());
        assertTrue(followships.contains(followship1));
        assertTrue(followships.contains(followship2));
    }

    @Test
    void testFindFollowshipsReceivedByUser_UserTo() {

        UserEntity userFromEntity1 = userRepository.save(new UserEntity());
        UserEntity userFromEntity2 = userRepository.save(new UserEntity());
        UserEntity userToEntity = userRepository.save(new UserEntity());

        FollowshipEntity followship1 = FollowshipEntity.builder().userFromEntity(userFromEntity1).userToEntity(userToEntity).build();
        FollowshipEntity followship2 = FollowshipEntity.builder().userFromEntity(userFromEntity2).userToEntity(userToEntity).build();

        followshipRepository.save(followship1);
        followshipRepository.save(followship2);

        List<FollowshipEntity> followships = followshipRepository.findFollowingsByUserToId(userToEntity.getId());

        assertEquals(2, followships.size());
        assertTrue(followships.contains(followship1));
        assertTrue(followships.contains(followship2));
    }

    @Test
    void testFindFollowshipsFromUserToUser() {

        UserEntity userFromEntity = userRepository.save(new UserEntity());
        UserEntity userToEntity = userRepository.save(new UserEntity());

        FollowshipEntity followship = FollowshipEntity.builder().userFromEntity(userFromEntity).userToEntity(userToEntity).build();
        followshipRepository.save(followship);

        Optional<FollowshipEntity> followships = followshipRepository.findByUserFromIdAndUserToId(userFromEntity.getId(), userToEntity.getId());
        assertTrue(followships.isPresent());
    }
}
