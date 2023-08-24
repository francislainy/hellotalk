package com.example.hellotalk.mapper;

import com.example.hellotalk.entity.comment.CommentEntity;
import com.example.hellotalk.model.comment.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Mapping(source = "user.id", target = "userEntity.id")
    @Mapping(source = "momentId", target = "momentEntity.id")
    CommentEntity toEntity(Comment comment);

    @Mapping(source = "userEntity.id", target = "user.id")
    @Mapping(source = "momentEntity.id", target = "momentId")
    Comment toModel(CommentEntity commentEntity);

    void updateEntityFromDto(Comment comment, @MappingTarget CommentEntity commentEntity);
}
