package com.suti.community.event;

import com.alibaba.fastjson.JSONObject;
import com.suti.community.dao.UserMapper;
import com.suti.community.entity.Event;
import com.suti.community.entity.User;
import com.suti.community.entity.UserRegisterMessage;
import com.suti.community.util.CommunityUtil;
import com.suti.community.util.MailClient;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.Random;

public class UserRegisterConsumer {
    private static final Logger logger = LoggerFactory.getLogger(UserRegisterConsumer.class);
    //域名
    @Value("${community.path.domain}")
    private String domain;

    //项目路径
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private UserMapper userMapper;

    @KafkaListener(topics = "user-register-topic")
    public void consume(ConsumerRecord record) {
        System.out.println("inner kafka consumer");
        if (record == null || record.value() == null) {
            logger.error("消息的内容为空!");
            return;
        }
        UserRegisterMessage message = JSONObject.parseObject(record.value().toString(), UserRegisterMessage.class);

        if (message == null) {
            logger.error("消息格式错误!");
            return;
        }
        User user = message.getUser();

        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        user.setStatus(0);
        user.setType(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("https://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());

        userMapper.insert(user);

        // 激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);
    }
}