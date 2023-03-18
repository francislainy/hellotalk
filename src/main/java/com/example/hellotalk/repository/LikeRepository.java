package com.example.hellotalk.repository;

import com.example.hellotalk.entity.user.LikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LikeRepository extends JpaRepository<LikeEntity, UUID> {

    List<LikeEntity> findAllByMomentEntityIdContaining(UUID momentId);

    List<LikeEntity> findAllByUserEntity_IdAndMomentEntity_Id(UUID momentId, UUID userId);
}
