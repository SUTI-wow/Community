package com.suti.community;

import com.suti.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
//导入NewCoderCommunity的配置类
@ContextConfiguration(classes = STCommunityApplication.class)
public class MailTest {
    @Autowired
    MailClient mailClient;

    @Autowired
    TemplateEngine templateEngine;

    @Test
    public void testMail(){
        mailClient.sendMail("1042615391@qq.com","test","Come on!You are the BEST!!!");
    }

    @Test
    public void testHTMLMail(){
        Context context = new Context();
        context.setVariable("username","xixi");
        String content = templateEngine.process("/mail/demo",context);
        System.out.println(content);
        mailClient.sendMail("1042615391@qq.com","htmltest",content);
    }

}
