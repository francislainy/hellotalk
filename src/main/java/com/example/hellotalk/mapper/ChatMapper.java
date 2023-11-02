package com.example.hellotalk.mapper;

import com.example.hellotalk.entity.message.ChatEntity;
import com.example.hellotalk.model.message.Chat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ChatMapper {

    @Mapping(source = "messageList", target = "messageEntityList")
    ChatEntity toEntity(Chat chat);

    @Mapping(source = "messageEntityList", target = "messageList")
    Chat toModel(ChatEntity chatEntity);
}

