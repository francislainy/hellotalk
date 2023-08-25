package com.example.hellotalk.repository;

import com.example.hellotalk.entity.user.HobbyAndInterestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HobbyAndInterestRepository extends JpaRepository<HobbyAndInterestEntity, UUID> {
}
