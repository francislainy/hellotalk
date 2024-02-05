package com.example.hellotalk.repository.message;

import com.example.hellotalk.entity.message.ChatEntity;
import com.example.hellotalk.entity.message.MessageEntity;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.repository.user.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void testFindByParticipants_ReturnsEmptyOptionalForPartialMatch() {
        // Create and save a user that will be a participant in the chat
        UserEntity existingUser = UserEntity.builder().username("existingUser").build();
        existingUser = userRepository.save(existingUser);

        UserEntity existingUser2 = UserEntity.builder().username("existingUser2").build();
        existingUser2 = userRepository.save(existingUser2);

        UserEntity existingUser3 = UserEntity.builder().username("existingUser3").build();
        existingUser3 = userRepository.save(existingUser3);

        // Create a chat with the existing user as a participant
        List<UserEntity> participants = new ArrayList<>();
        participants.add(existingUser);
        participants.add(existingUser2);
        ChatEntity chatEntity = ChatEntity.builder().participantEntityList(participants).build();
        chatRepository.save(chatEntity);

        // Create a chat with different user as a participant
        List<UserEntity> participantsToCheck = new ArrayList<>();
        participantsToCheck.add(existingUser);
        participantsToCheck.add(existingUser3);

        Optional<ChatEntity> actualChat = chatRepository.findByParticipants(participantsToCheck, participantsToCheck.size());

        // Assert that no chat is found
        assertTrue(actualChat.isEmpty());
    }


    @Autowired
    TestEntityManager entityManager;

    ChatEntity chatEntity;
    MessageEntity messageEntity1;
    MessageEntity messageEntity2;
    MessageEntity messageEntity3;

    @BeforeEach
    void setup() {
        messageEntity1 = MessageEntity.builder().creationDate(ZonedDateTime.now().minusDays(1)).build();
        messageEntity2 = MessageEntity.builder().creationDate(ZonedDateTime.now().minusDays(2)).build();
        messageEntity3 = MessageEntity.builder().creationDate(ZonedDateTime.now().minusDays(3)).build();

        List<MessageEntity> messageEntityList = new ArrayList<>();
        messageEntityList.add(messageEntity2);
        messageEntityList.add(messageEntity1);
        messageEntityList.add(messageEntity3);

        chatEntity = ChatEntity.builder()
                .messageEntityList(messageEntityList)
                .build();

        chatEntity = chatRepository.save(chatEntity);
    }

    @Test
    void testMessageOrder() {
        MessageEntity messageEntity1 = MessageEntity.builder().creationDate(ZonedDateTime.now().minusDays(1)).build();
        MessageEntity messageEntity2 = MessageEntity.builder().creationDate(ZonedDateTime.now().minusDays(2)).build();
        MessageEntity messageEntity3 = MessageEntity.builder().creationDate(ZonedDateTime.now().minusDays(3)).build();

        List<MessageEntity> messageEntityList = new ArrayList<>();
        messageEntityList.add(messageEntity2);
        messageEntityList.add(messageEntity1);
        messageEntityList.add(messageEntity3);

        ChatEntity chatEntity = ChatEntity.builder()
                .messageEntityList(messageEntityList)
                .build();

        chatEntity = chatRepository.save(chatEntity);

        ChatEntity chatEntityList = chatRepository.findById(chatEntity.getId()).orElseThrow();

        List<MessageEntity> messages = chatEntityList.getMessageEntityList();
        for (int i = 0; i < messages.size() - 1; i++) {
            assertTrue(messages.get(i).getCreationDate().isBefore(messages.get(i + 1).getCreationDate()));
        }
    }

    @Test
    void testMessageOrder2() {

//        entityManager.flush();
//        entityManager.refresh(chatEntity);

        // Add a new message
        MessageEntity messageEntity4 = MessageEntity.builder().creationDate(ZonedDateTime.now()).build();
        chatEntity.getMessageEntityList().add(messageEntity4);
        chatRepository.saveAndFlush(chatEntity);

        ChatEntity chatEntityList = chatRepository.findById(chatEntity.getId()).orElseThrow();

        List<MessageEntity> messages = chatEntityList.getMessageEntityList();
        for (int i = 0; i < messages.size() - 1; i++) {
            assertTrue(messages.get(i).getCreationDate().isBefore(messages.get(i + 1).getCreationDate()));
        }
    }


    @Test
    void testMessageOrder3() {
        // Create messages with different creation dates
        MessageEntity messageEntity1 = MessageEntity.builder().creationDate(ZonedDateTime.now().minusDays(1)).build();
        MessageEntity messageEntity2 = MessageEntity.builder().creationDate(ZonedDateTime.now().minusDays(2)).build();
        MessageEntity messageEntity3 = MessageEntity.builder().creationDate(ZonedDateTime.now().minusDays(3)).build();

        // Add messages to list in an unordered manner
        List<MessageEntity> messageEntityList = new ArrayList<>();
        messageEntityList.add(messageEntity1);
        messageEntityList.add(messageEntity3);
        messageEntityList.add(messageEntity2);

        // Create chat entity with the list of messages
        ChatEntity chatEntity = ChatEntity.builder()
                .messageEntityList(messageEntityList)
                .build();

        // Save chat entity
        chatEntity = chatRepository.saveAndFlush(chatEntity);
        entityManager.flush();
        entityManager.refresh(chatEntity);

        // Add a new message
        MessageEntity messageEntity4 = MessageEntity.builder().creationDate(ZonedDateTime.now()).build();
        chatEntity.getMessageEntityList().add(messageEntity4);
        chatRepository.save(chatEntity);

        // Retrieve the chat entity
        ChatEntity chatEntityList = chatRepository.findById(chatEntity.getId()).orElseThrow();

        // Retrieve the list of messages
        List<MessageEntity> messages = chatEntityList.getMessageEntityList();

        // Check the order of messages
        for (int i = 0; i < messages.size() - 1; i++) {
            assertTrue(messages.get(i).getCreationDate().isBefore(messages.get(i + 1).getCreationDate()));
        }
    }
}

