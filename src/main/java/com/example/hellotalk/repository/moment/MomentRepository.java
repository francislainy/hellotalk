package com.example.hellotalk.repository.moment;

import com.example.hellotalk.entity.moment.MomentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MomentRepository extends JpaRepository<MomentEntity, UUID> {

    List<MomentEntity> findAllByUserEntityId(UUID userId);
}
