package com.example.hellotalk.repository.moment;

import com.example.hellotalk.entity.moment.MomentEntity;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MomentRepositoryTest {

    @Autowired
    MomentRepository momentRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    void testFindAllByUserEntity_IdContains() {

        UserEntity userEntity1 = UserEntity.builder().name("user1").build();
        userEntity1 = userRepository.save(userEntity1);

        MomentEntity momentEntity1 = new MomentEntity();
        momentEntity1.setUserEntity(userEntity1);
        momentRepository.save(momentEntity1);

        UserEntity userEntity2 = UserEntity.builder().name("user2").build();
        userEntity2 = userRepository.save(userEntity2);

        MomentEntity momentEntity2 = new MomentEntity();
        momentEntity2.setUserEntity(userEntity2);
        momentRepository.save(momentEntity2);

        List<MomentEntity> moments = momentRepository.findAllByUserEntityId(userEntity1.getId());

        assertEquals(1, moments.size());
        assertEquals(moments.get(0).getId(), momentEntity1.getId());
    }
}
