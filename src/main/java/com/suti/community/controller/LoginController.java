package com.suti.community.controller;

import com.google.code.kaptcha.Producer;
import com.suti.community.entity.User;
import com.suti.community.service.UserService;
import com.suti.community.util.CommunityConstant;
import com.suti.community.util.CommunityUtil;
import com.suti.community.util.RedisKeyUtil;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class LoginController implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserService userService;

    @Autowired
    Producer kaptchaProducer;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(path = "/register",method = RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register";
    }

    @RequestMapping(path = "/login",method = RequestMethod.GET)
    public String getLoginPage(){
        return "/site/login";
    }

    @RequestMapping(path = "/register",method = RequestMethod.POST)
    public String register(Model model, User user){
        Map<String, Object> map = userService.register(user);
        //注册成功  跳转到首页 中间需要一个操作结果网页的显示[第三方页面 operate-result]
        if(map==null ||map.isEmpty()){
            model.addAttribute("msg","您已成功注册，我们已向您的邮箱发送了激活邮件，请尽快激活！");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }
        else{
            model.addAttribute("usernameMessage",map.get("usernameMessage"));
            model.addAttribute("passwordMessage",map.get("passwordMessage"));
            model.addAttribute("emailMessage",map.get("emailMessage"));
            return "/site/register";
        }

    }

    @RequestMapping(path = "/activation/{userId}/{code}",method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId,@PathVariable("code")String code){
        int result = userService.activation(userId,code);
        if(result==ACTIVATION_SUCCESS){
            model.addAttribute("msg","激活成功！您可以登陆账号");
            model.addAttribute("target","/login");
        }else if(result==ACTIVATION_REPEAT){
            model.addAttribute("msg","无效激活！该账号已经被激活");
            model.addAttribute("target","/index");
        }else{
            model.addAttribute("msg","激活失败！激活码不正确");
            model.addAttribute("target","/index");
        }
        return "/site/operate-result";
    }

    // 生成验证码的方法
    // 需要跨请求的保留验证码数据
    // 验证码是敏感数据 要用session比较好
    @RequestMapping(path = "/kaptcha",method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response/*, HttpSession session*/){
        // 生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        // 存储验证码信息到session
        //session.setAttribute("kaptcha",text);

        // 验证码的归属
        String kaptchaOwner = CommunityUtil.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner", kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        response.addCookie(cookie);
        // 将验证码存入Redis
        String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(redisKey, text, 60, TimeUnit.SECONDS);

        // 将图片用响应输出
        response.setContentType("image/png");
        try {
            //响应图片用字节流
            ServletOutputStream os = response.getOutputStream();
            //用ImageIO输出图片
            ImageIO.write(image,"png",os);
        } catch (IOException e) {
            logger.error("响应验证码失败:"+e.getMessage());
        }
    }

    @RequestMapping(path = "/login",method = RequestMethod.POST)
    public String login(String username,String password,String code,boolean rememberme,Model model,
                        /*HttpSession session,*/HttpServletResponse response,
                        @CookieValue("kaptchaOwner") String kaptchaOwner){
        //检查验证码
        //String kaptcha = (String) session.getAttribute("kaptcha");
        String kaptcha = null;
        if (StringUtils.isNotBlank(kaptchaOwner)) {
            String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(redisKey);
        }
        if(StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg","验证码不正确！");
            return "/site/login";
        }

        //检查账号密码
        int expiredSeconds = rememberme? REMEMBERME_EXPIRED_SECONDS:DEFAULT_EXPIRED_SECONDS;
        Map<String,Object> map = userService.login(username,password,expiredSeconds);
        if(map.containsKey("ticketMsg")){
            Cookie cookie = new Cookie("ticket", (String) map.get("ticketMsg"));
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        }
        else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/login";
        }
    }

    //退出
    @RequestMapping(path = "/logout",method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/login";
    }

    @RequestMapping(path = "/getForgetActivationCode",method = RequestMethod.GET)
    public String getForgetActivationCode(String email, HttpSession session, Model model) {
        Map<String, Object> map = userService.getForgetActivationCode(email);
        if(map.containsKey("emailMsg")) {
            model.addAttribute("emailMsg",map.get("emailMsg"));
        }else {
            session.setAttribute("forgetActivationCode",map.get("forgetActivationCode"));
            session.setAttribute("expirationTime",map.get("expirationTime"));
        }
        return "/site/forget";
    }

    @RequestMapping(path = "/forget",method = RequestMethod.POST)
    public String forget(HttpSession session, Model model,
                         String email, String verifycode, String password) {
        if(verifycode == null) {
            model.addAttribute("verifycodeMsg","验证码不能为空");
            return "/site/forget";
        }
        if(!verifycode.equalsIgnoreCase((String) session.getAttribute("forgetActivationCode"))) {
            model.addAttribute("verifycodeMsg","验证码错误");
            return "/site/forget";
        }
        if (LocalDateTime.now().isAfter((LocalDateTime) session.getAttribute("expirationTime"))) {
            model.addAttribute("verifycodeMsg", "验证码已过期，请重新获取验证码！");
            return "/site/forget";
        }
        Map<String, Object> map = userService.forget(email, password);
        if(map.size() == 0) {
            model.addAttribute("msg","忘记密码已经成功修改");
            model.addAttribute("target","/login");
            return "/site/operate-result";
        }else {
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/forget";
        }
    }



}
