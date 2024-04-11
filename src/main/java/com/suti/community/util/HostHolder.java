package com.suti.community.util;

import com.suti.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * 用于持有用户信息，代替session功能
 * 考虑到服务器的多线程并发的实际，我们需要考虑多线程隔离 这里用到ThreadLocal
 */
@Component
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    public void clear(){
        users.remove();
    }
}
