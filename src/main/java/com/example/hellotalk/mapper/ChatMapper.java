package com.example.hellotalk.mapper;

import com.example.hellotalk.entity.message.ChatEntity;
import com.example.hellotalk.model.message.Chat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = MessageMapper.class)
public interface ChatMapper {

    @Mapping(source = "messageList", target = "messageEntityList")
    @Mapping(source = "participantList", target = "participantEntityList")
    ChatEntity toEntity(Chat chat);

    @Mapping(source = "messageEntityList", target = "messageList")
    @Mapping(source = "participantEntityList", target = "participantList")
    Chat toModel(ChatEntity chatEntity);
}
