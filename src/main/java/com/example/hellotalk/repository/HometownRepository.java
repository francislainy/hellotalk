package com.example.hellotalk.repository;

import com.example.hellotalk.entity.user.HometownEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface HometownRepository extends JpaRepository<HometownEntity, UUID> {
}
