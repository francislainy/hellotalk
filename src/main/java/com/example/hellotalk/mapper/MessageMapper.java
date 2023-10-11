package com.example.hellotalk.mapper;

import com.example.hellotalk.entity.message.MessageEntity;
import com.example.hellotalk.model.message.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface MessageMapper {

    @Mapping(source = "userFromId", target = "userFromEntity.id")
    @Mapping(source = "userToId", target = "userToEntity.id")
    MessageEntity toEntity(Message message);

    @Mapping(source = "userFromEntity.id", target = "userFromId")
    @Mapping(source = "userToEntity.id", target = "userToId")
    Message toModel(MessageEntity messageEntity);
}
