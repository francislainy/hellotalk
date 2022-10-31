package com.example.hellotalk.repository;

import com.example.hellotalk.entity.user.HobbyAndInterestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface HobbyAndInterestRepository extends JpaRepository<HobbyAndInterestEntity, UUID> {
}
