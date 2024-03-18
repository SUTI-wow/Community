package com.suti.community.service;

import com.suti.community.dao.DiscussPostMapper;
import com.suti.community.entity.DiscussPost;
import com.suti.community.util.CommunityUtil;
import com.suti.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    SensitiveFilter sensitiveFilter;

    public List<DiscussPost> findDiscussPost(int userId,int offset,int limit){
        return discussPostMapper.selectDiscussPosts(userId,offset,limit);
    }

    public int findDiscussPostRows(int userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    public int insertDiscussPost(DiscussPost post){
        if(post==null)
            throw new IllegalArgumentException("参数不能为空!");
        //转义html标记，防止出现<script>dd</script>的标题或内容损害页面
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        //敏感词过滤
        post.setTitle(sensitiveFilter.filter(post.getTitle()));

        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        return discussPostMapper.insertDiscussPost(post);
    }

    public DiscussPost findDiscussPostById(int id){
        return discussPostMapper.selectDiscussPostById(id);
    }
}
