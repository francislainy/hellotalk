package com.example.hellotalk.repository;

import com.example.hellotalk.entity.user.LikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LikeRepository extends JpaRepository<LikeEntity, UUID> {

    LikeEntity findByUserEntityIdAndMomentEntityId(UUID momentId, UUID userId);

    List<LikeEntity> findAllByMomentEntityId(UUID momentId);

    int countByMomentEntityId(UUID momentId);
}
