package com.example.hellotalk.repository.followship;

import com.example.hellotalk.entity.followship.FollowshipEntity;
import com.example.hellotalk.entity.user.UserEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FollowshipRepositoryTests {

    @Autowired
    private FollowshipRepository followshipRepository;

    @Test
    public void testFindFollowshipsByUserFromId() {
        UUID userFromId = UUID.randomUUID();

        // Positive test case
        List<FollowshipEntity> followships = followshipRepository.findFollowshipsByUserFromId(userFromId);
        assertTrue(followships.isEmpty());

        followshipRepository.save(new FollowshipEntity(UUID.randomUUID(), userFromId));
        followshipRepository.save(new FollowshipEntity(UUID.randomUUID(), userFromId));

        followships = followshipRepository.findFollowshipsByUserFromId(userFromId);
        assertEquals(2, followships.size());
    }
}
