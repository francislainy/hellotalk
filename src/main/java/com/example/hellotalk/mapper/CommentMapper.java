package com.example.hellotalk.mapper;

import com.example.hellotalk.entity.comment.CommentEntity;
import com.example.hellotalk.model.comment.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CommentMapper {

    @Mapping(source = "parentId", target = "parentCommentEntity.id")
    @Mapping(source = "user.id", target = "userEntity.id")
    @Mapping(source = "momentId", target = "momentEntity.id")
    CommentEntity toEntity(Comment comment);

    @Mapping(source = "parentCommentEntity.id", target = "parentId")
    @Mapping(source = "userEntity.id", target = "user.id")
    @Mapping(source = "momentEntity.id", target = "momentId")
    Comment toModel(CommentEntity commentEntity);
}
