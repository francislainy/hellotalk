package com.example.hellotalk.repository.moment;

import com.example.hellotalk.entity.moment.MomentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MomentRepository extends JpaRepository<MomentEntity, UUID> {
}
