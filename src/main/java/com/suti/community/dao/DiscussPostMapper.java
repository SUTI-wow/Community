package com.suti.community.dao;

import com.suti.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    // 需求：选择讨论帖
    // 在首页上显示帖子的时候其实并不需要输入userId，但是为了在显示我的里面能够提取到自己的帖子的需求
    // 动态的sql 对于userId为0的时候，不考虑这个参数；当其有实际意义的时候，则添加where条件
    // 考虑分页的需求【sql语句中需要提供两个参数】
    List<DiscussPost> selectDiscussPosts(int userId, int offset,int limit);

    //需求：统计帖子的总数，便于后面的页数计算
    // @Param注解用于给参数取别名
    // 如果只有一个参数，并且在<if>里使用，则一定要加别名
    int selectDiscussPostRows(@Param("userId") int userId);

    //插入帖子
    int insertDiscussPost(DiscussPost post);

    //查询帖子
    DiscussPost selectDiscussPostById(int id);
}
