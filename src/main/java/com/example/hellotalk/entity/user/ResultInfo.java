package com.example.hellotalk.entity.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultInfo {
    UUID id;
    UUID userId;
    UUID momentId;
}
