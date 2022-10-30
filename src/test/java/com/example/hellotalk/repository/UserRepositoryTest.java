package com.example.hellotalk.repository;

import com.example.hellotalk.entity.user.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest extends PostgresContainer { // Can use both this class or BaseIntegrationClass

    @Autowired UserRepository userRepository;

    @Test
    void saveUser() {
        UserEntity userEntity = UserEntity.builder().name("anyName").selfIntroduction("anySelfIntroduction").build();
        userEntity = userRepository.save(userEntity);

        UserEntity finalUserEntity = userEntity;
        assertAll(
                () -> assertTrue(userRepository.findAll().size() > 0),
                () -> assertNotNull(userRepository.findById(finalUserEntity.getId())),
                () -> assertEquals("anyName", userRepository.findById(finalUserEntity.getId()).get().getName()),
                () -> assertEquals("anySelfIntroduction", userRepository.findById(finalUserEntity.getId()).get().getSelfIntroduction())
        );
        
        
    }
}
