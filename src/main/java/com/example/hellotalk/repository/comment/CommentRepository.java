package com.example.hellotalk.repository.comment;

import com.example.hellotalk.entity.comment.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<CommentEntity, UUID> {

    @Query("SELECT c FROM CommentEntity c WHERE c.momentEntity.id = :momentId")
    List<CommentEntity> findAllByMomentEntity_IdContains(@Param("momentId") UUID momentId);
}
