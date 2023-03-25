package com.example.hellotalk.repository.moment;

import com.example.hellotalk.entity.moment.MomentEntity;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.repository.BasePostgresConfig;
import com.example.hellotalk.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MomentRepositoryTest extends BasePostgresConfig {

    @Autowired
    private MomentRepository momentRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindAllByUserEntity_IdContains() {

        UserEntity userEntity1 = UserEntity.builder().name("user1").build();
        userEntity1 = userRepository.save(userEntity1);

        MomentEntity moment1 = new MomentEntity();
        moment1.setUserEntity(userEntity1);
        momentRepository.save(moment1);

        UserEntity userEntity2 = UserEntity.builder().name("user2").build();
        userEntity2 = userRepository.save(userEntity2);

        MomentEntity moment2 = new MomentEntity();
        moment2.setUserEntity(userEntity2);
        momentRepository.save(moment2);

        // Act
        List<MomentEntity> moments = momentRepository.findAllByUserEntity_IdContains(userEntity1.getId());

        // Assert
        assertEquals(1, moments.size());
        assertEquals(moments.get(0).getId(), moment1.getId());
    }
}
