package com.example.hellotalk.repository;

import com.example.hellotalk.entity.user.LikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LikeRepository extends JpaRepository<LikeEntity, UUID> {

    List<LikeEntity> findAllByMomentEntityIdContaining(UUID momentId);

    LikeEntity findByUserEntity_IdAndMomentEntity_Id(UUID momentId, UUID userId);

    List<LikeEntity> findAllByMomentEntity_Id(UUID momentId);

    @Query("SELECT COUNT(l) FROM LikeEntity l WHERE l.momentEntity.id = :momentId")
    int countLikesByMomentId(@Param("momentId") UUID momentId);
}
