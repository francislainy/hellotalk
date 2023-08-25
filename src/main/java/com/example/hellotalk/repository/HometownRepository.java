package com.example.hellotalk.repository;

import com.example.hellotalk.entity.user.HometownEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HometownRepository extends JpaRepository<HometownEntity, UUID> {
}
