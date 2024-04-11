package com.suti.community.service;

import com.alibaba.fastjson.JSONObject;
import com.suti.community.dao.LoginTicketMapper;
import com.suti.community.dao.UserMapper;
import com.suti.community.entity.LoginTicket;
import com.suti.community.entity.User;
import com.suti.community.entity.UserRegisterMessage;
import com.suti.community.util.*;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private KafkaTemplate kafkaTemplate;
    //域名
    @Value("${community.path.domain}")
    private String domain;

    //项目路径
    @Value("${server.servlet.context-path}")
    private String contextPath;

    //@Autowired
    //private LoginTicketMapper loginTicketMapper;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    private RedisTemplate redisTemplate;


    public User findUserById(int id){

        //return userMapper.selectById(userId);
        User user = getCache(id);
        if (user == null) {
            user = initCache(id);
        }
        return user;
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
        //user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        //user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        //user.setStatus(0);
        //user.setType(0);
        //user.setActivationCode(CommunityUtil.generateUUID());
        //user.setHeaderUrl(String.format("https://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        //user.setCreateTime(new Date());
        //
        //userMapper.insert(user);

        // 将注册用户的操作封装为一个消息，并发送到 Kafka 中
        UserRegisterMessage message = new UserRegisterMessage(user);
        kafkaTemplate.send("user-register-topic", JSONObject.toJSONString(message));
        //激活邮件
        //Context context = new Context();
        //context.setVariable("email",user.getEmail());
        //String url = domain+contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode();
        //context.setVariable("url",url);
        //String content = templateEngine.process("/mail/activation",context);
        //mailClient.sendMail(user.getEmail(),"激活账号",content);

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

        //loginTicketMapper.insertTicket(loginTicket);
        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey, loginTicket);
        map.put("ticketMsg",loginTicket.getTicket());
        return map;
    }

    //退出
    public void logout(String ticket){

        //loginTicketMapper.updateStatus(ticket,1);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey, loginTicket);
    }

    //忘记密码  获得忘记密码的邮件激活码
    public Map<String,Object> getForgetActivationCode(String email) {
        Map<String,Object> map = new HashMap<>();
        if(StringUtils.isBlank(email)) {
            map.put("emailMsg","邮箱不能为空");
            return map;
        }
        User user = userMapper.selectByEmail(email);
        if(user == null) {
            map.put("emailMsg","邮箱未注册");
            return map;
        }
        if(user.getStatus() == 0) {
            map.put("emailMsg","账号未激活");
            return map;
        }
        Context context = new Context();
        context.setVariable("email",email);
        String forgetActivationCode = CommunityUtil.generateUUID().substring(0,5);
        context.setVariable("forgetActivationCode",forgetActivationCode);
        String process = templateEngine.process("/mail/forget", context);
        mailClient.sendMail(email,"忘记密码",process);
        map.put("forgetActivationCode",forgetActivationCode);//map中存放一份，为了之后和用户输入的验证码进行对比
        map.put("expirationTime", LocalDateTime.now().plusMinutes(5L));//过期时间
        return map;
    }

    //重置密码
    public Map<String,Object> forget(String email, String password) {
        Map<String,Object> map = new HashMap<>();
        if(StringUtils.isBlank(password)) {
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        User user = userMapper.selectByEmail(email);
        updatePassword(user.getId(),CommunityUtil.md5(password+user.getSalt()));
        return map;
    }


    //获取用户凭证
    public LoginTicket findLoginTicket(String ticket) {
//        return loginTicketMapper.selectByTicket(ticket);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }
    //更新头像
    public int updateHeaderUrl(int userId,String url){

        int rows = userMapper.updateHeader(userId, url);
        clearCache(userId);
        return rows;
    }

    public int updatePassword(int userId, String password) {

        int rows = userMapper.updatePassword(userId,password);
        clearCache(userId);
        return rows;
    }

    public User findUserByName(String username) {
        return userMapper.selectByName(username);
    }

    // 1.优先从缓存中取值
    private User getCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }

    // 2.取不到时初始化缓存数据
    private User initCache(int userId) {
        User user = userMapper.selectById(userId);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }

    // 3.数据变更时清除缓存数据
    private void clearCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }

}
