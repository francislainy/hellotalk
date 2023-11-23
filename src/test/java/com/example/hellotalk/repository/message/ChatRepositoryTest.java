package com.example.hellotalk.repository.message;

import com.example.hellotalk.entity.message.ChatEntity;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ChatRepositoryTest {

    @Autowired
    ChatRepository chatRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    void testFindByParticipants_ReturnsChatForUsers() {
        List<UserEntity> expectedUsers = new ArrayList<>();
        UserEntity userEntity1 = UserEntity.builder().username("user1").build();
        UserEntity userEntity2 = UserEntity.builder().username("user2").build();
        expectedUsers.add(userEntity1);
        expectedUsers.add(userEntity2);

        userEntity1 = userRepository.save(userEntity1);
        userEntity2 = userRepository.save(userEntity2);

        ChatEntity chatEntity = ChatEntity.builder().participantEntityList(expectedUsers).build();
        chatEntity = chatRepository.save(chatEntity);

        Optional<ChatEntity> actualChat = chatRepository.findByParticipants(expectedUsers, expectedUsers.size());

        assertTrue(actualChat.isPresent());
        assertEquals(chatEntity, actualChat.get());
    }

    @Test
    void testFindByParticipants_ReturnsEmptyOptionalForNonExistentUsers() {
        List<UserEntity> nonExistentUsers = new ArrayList<>();
        UserEntity userEntity1 = UserEntity.builder().id(UUID.randomUUID()).username("nonExistentUser1").build();
        UserEntity userEntity2 = UserEntity.builder().id(UUID.randomUUID()).username("nonExistentUser2").build();
        nonExistentUsers.add(userEntity1);
        nonExistentUsers.add(userEntity2);

        Optional<ChatEntity> actualChat = chatRepository.findByParticipants(nonExistentUsers, nonExistentUsers.size());

        assertTrue(actualChat.isEmpty());
    }

    @Test
    void testFindByParticipants_ReturnsEmptyOptionalForWrongParticipantCount() {
        List<UserEntity> expectedUsers = new ArrayList<>();
        UserEntity userEntity1 = UserEntity.builder().username("user1").build();
        UserEntity userEntity2 = UserEntity.builder().username("user2").build();
        expectedUsers.add(userEntity1);
        expectedUsers.add(userEntity2);

        userEntity1 = userRepository.save(userEntity1);
        userEntity2 = userRepository.save(userEntity2);

        ChatEntity chatEntity = ChatEntity.builder().participantEntityList(expectedUsers).build();
        chatEntity = chatRepository.save(chatEntity);

        Optional<ChatEntity> actualChat = chatRepository.findByParticipants(expectedUsers, expectedUsers.size() + 1);

        assertTrue(actualChat.isEmpty());
    }
}

