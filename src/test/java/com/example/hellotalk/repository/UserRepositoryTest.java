package com.example.hellotalk.repository;

import com.example.hellotalk.entity.user.HometownEntity;
import com.example.hellotalk.entity.user.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest extends PostgresContainer { // We can use both this class or BaseIntegrationClass

    @Autowired UserRepository userRepository;
    @Autowired HometownRepository hometownRepository;

    @Test
    void saveUser() {

        HometownEntity hometownEntity = HometownEntity.builder().city("anyCity").country("anyCountry").build();
        hometownRepository.save(hometownEntity);
        UserEntity userEntity = UserEntity.builder().name("anyName").selfIntroduction("anySelfIntroduction").hometownEntity(hometownEntity).build();
        userEntity = userRepository.save(userEntity);

        userEntity = userRepository.findById(userEntity.getId()).orElse(null);
        assertTrue(userRepository.findAll().size() > 0);
        assertNotNull(userEntity);
        assertNotNull(userRepository.findById(userEntity.getId()));

        UserEntity finalUserEntity = userEntity;
        assertAll(
                () -> assertEquals("anyName", finalUserEntity.getName()),
                () -> assertEquals("anySelfIntroduction", finalUserEntity.getSelfIntroduction()),
                () -> assertEquals("anyCity", finalUserEntity.getHometownEntity().getCity()),
                () -> assertEquals("anyCountry", finalUserEntity.getHometownEntity().getCountry())
        );
    }
}
