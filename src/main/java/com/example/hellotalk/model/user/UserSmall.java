package com.example.hellotalk.model.user;

import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.mapper.UserSmallMapper;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSmall {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID id;
    private String username;
    private String name;
    private String gender;
    private String creationDate;
    private String status;
    private String nativeLanguage;
    private String targetLanguage;

    public static UserSmall fromEntity(UserEntity userEntity) {
        return UserSmallMapper.INSTANCE.toModel(userEntity);
    }
}
