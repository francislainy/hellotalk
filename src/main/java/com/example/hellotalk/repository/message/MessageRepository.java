package com.example.hellotalk.repository.message;

import com.example.hellotalk.entity.message.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MessageRepository extends JpaRepository<MessageEntity, UUID> {
}
