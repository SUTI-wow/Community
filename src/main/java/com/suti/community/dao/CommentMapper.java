package com.suti.community.dao;

import com.suti.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    //根据某一实体查询的一页的评论
    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    //查询某个实体的评论总数
    int selectCountByEntity(int entityType, int entityId);

    int insertComment(Comment comment);

    Comment selectCommentById(int id);
}
