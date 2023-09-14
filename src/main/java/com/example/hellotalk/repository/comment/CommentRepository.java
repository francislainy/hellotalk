package com.example.hellotalk.repository.comment;

import com.example.hellotalk.entity.comment.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<CommentEntity, UUID> {

    List<CommentEntity> findAllByMomentEntityId(UUID momentId);

    List<CommentEntity> findAllByParentCommentEntityId(UUID commentId);
}
