package com.example.hellotalk.repository.moment;

import com.example.hellotalk.entity.moment.MomentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MomentRepository extends JpaRepository<MomentEntity, UUID> {

    @Query("SELECT m FROM MomentEntity m WHERE m.userEntity.id = :userId")
    List<MomentEntity> findAllByUserEntity_IdContains(@Param("userId") UUID userId);
}
