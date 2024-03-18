package com.suti.community.service;

import com.suti.community.dao.LoginTicketMapper;
import com.suti.community.dao.UserMapper;
import com.suti.community.entity.LoginTicket;
import com.suti.community.entity.User;
import com.suti.community.util.CommunityConstant;
import com.suti.community.util.CommunityUtil;
import com.suti.community.util.HostHolder;
import com.suti.community.util.MailClient;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    //域名
    @Value("${community.path.domain}")
    private String domain;

    //项目路径
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    HostHolder hostHolder;

    public User findUserById(int userId){
        return userMapper.selectById(userId);
    }

    public Map<String,Object> register(User user){
        Map<String,Object> map = new HashMap<>();
        //处理空值逻辑
        if(user == null)
            throw new IllegalArgumentException("参数不能为空！");
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMessage","用户名不能为空！");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMessage","密码不能为空！");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMessage","邮箱不能为空！");
            return map;
        }

        //处理用户名被占用
        User u = userMapper.selectByName(user.getUsername());
        if(u!=null){
            map.put("usernameMessage","用户名被占用！");
            return map;
        }

        u = userMapper.selectByEmail(user.getEmail());
        if(u!=null){
            map.put("emailMessage","邮箱已注册！");
            return map;
        }

        //注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        user.setStatus(0);
        user.setType(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("https://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());

        userMapper.insert(user);

        //激活邮件
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        String url = domain+contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode();
        context.setVariable("url",url);
        String content = templateEngine.process("/mail/activation",context);
        mailClient.sendMail(user.getEmail(),"激活账号",content);

        return map;
    }

    //激活验证
    public int activation(int userId,String code){
        User user = userMapper.selectById(userId);
        if(user.getStatus()==1){
            return ACTIVATION_REPEAT;
        }
        else if(user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId,1);
            return ACTIVATION_SUCCESS;
        }
        else{
            return ACTIVATION_FAILURE;
        }
    }

    //登录
    public Map<String,Object> login(String username,String password,int expiredSeconds){
        Map<String,Object> map = new HashMap<>();

        //处理空值逻辑
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg","用户名不能为空！");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg","密码不能为空！");
            return map;
        }

        //验证账号
        User user = userMapper.selectByName(username);
        //处理用户不存在
        if(user==null){
            map.put("usernameMsg","用户不存在！");
            return map;
        }
        //用户未激活
        if(user.getStatus()==0){
            map.put("usernameMsg","用户未激活！");
            return map;
        }
        password = CommunityUtil.md5(password+user.getSalt());
        if(!user.getPassword().equals(password)){
            map.put("passwordMsg","密码不正确！");
            return map;
        }

        //生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+expiredSeconds*1000));

        loginTicketMapper.insertTicket(loginTicket);
        map.put("ticketMsg",loginTicket.getTicket());
        return map;
    }

    //退出
    public void logout(String ticket){
        loginTicketMapper.updateStatus(ticket,1);
    }

    //获取用户凭证
    public LoginTicket getLoginTicket(String ticket){
        return loginTicketMapper.selectByTicket(ticket);
    }

    //更新头像
    public int updateHeaderUrl(int userId,String url){
        return userMapper.updateHeader(userId,url);
    }

    //更新密码
    public Map<String,Object> updatePassword(String oldPassword,String newPassword,String confirmPassword){
        Map<String,Object> map = new HashMap<>();
        if(StringUtils.isBlank(oldPassword)){
            map.put("passworderror","密码不能为空!");
            return map;
        }
        if(StringUtils.isBlank(newPassword)){
            map.put("newpassworderror","密码不能为空!");
            return map;
        }
        if(StringUtils.isBlank(confirmPassword)){
            map.put("confirmpassworderror","密码不能为空!");
            return map;
        }

        if(!newPassword.equals(confirmPassword)){
            map.put("confirmpassworderror","两次输入的密码不一致!");
            return map;
        }

        User user = hostHolder.getUser();
        oldPassword = CommunityUtil.md5(oldPassword+user.getSalt());
        if(!user.getPassword().equals(oldPassword)){
            map.put("passworderror","密码不正确!");
            return map;
        }

        newPassword = CommunityUtil.md5(newPassword+user.getSalt());
        if(user.getPassword().equals(newPassword)){
            map.put("newpassworderror","新密码不能与原密码一致!");
            return map;
        }
        userMapper.updatePassword(user.getId(),newPassword);
        map.put("password",newPassword);
        return map;
    }



}
