package com.example.hellotalk.repository.message;

import com.example.hellotalk.entity.message.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChatRepository extends JpaRepository<ChatEntity, UUID> {

}
