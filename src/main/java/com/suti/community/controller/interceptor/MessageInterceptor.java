package com.suti.community.controller.interceptor;

import com.suti.community.entity.User;
import com.suti.community.service.MessageService;
import com.suti.community.util.HostHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class MessageInterceptor implements HandlerInterceptor {
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private MessageService messageService;

    //调用controller之后模板之前调用该方法
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(user != null) {
            int noticeUnread = messageService.findNoticeUnreadCount(user.getId(), null);
            int messageUnRead = messageService.findLetterUnreadCount(user.getId(), null);
            modelAndView.addObject("unRead",noticeUnread + messageUnRead);
        }

    }
}
