package com.example.hellotalk.repository.message;

import com.example.hellotalk.entity.message.ChatEntity;
import com.example.hellotalk.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatRepository extends JpaRepository<ChatEntity, UUID> {

    @Query("SELECT c FROM ChatEntity c WHERE :participantCount = (SELECT COUNT(DISTINCT p) FROM c.participantEntityList p WHERE p IN :participants)")
    Optional<ChatEntity> findByParticipants(@Param("participants") List<UserEntity> participants, @Param("participantCount") int participantCount);
}
