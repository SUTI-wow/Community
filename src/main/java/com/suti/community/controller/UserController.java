package com.suti.community.controller;

import com.suti.community.annotation.LoginRequired;
import com.suti.community.entity.User;
import com.suti.community.service.FollowService;
import com.suti.community.service.LikeService;
import com.suti.community.service.UserService;
import com.suti.community.util.CommunityConstant;
import com.suti.community.util.CommunityUtil;
import com.suti.community.util.HostHolder;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    LikeService likeService;

    @Autowired
    FollowService followService;

    @Value("${community.path.domain}")
    String domain;

    @Value("${community.path.upload}")
    String uploadPath;

    @Value("${server.servlet.context-path}")
    String contextPath;

    @LoginRequired
    @RequestMapping(path = "/setting",method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path = "/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage==null || headerImage.isEmpty()){
            model.addAttribute("error","您还没有选择图片！");
            return "/site/setting";
        }

        //需要更改文件名，以防冲突导致覆盖
        String filename = headerImage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf('.'));
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error","文件的格式不正确！");
            return "/site/setting";
        }
        //生成随机文件名
        filename = CommunityUtil.generateUUID()+suffix;
        //确定文件存放的路径 （本地服务器）
        File dest = new File(uploadPath+"/"+filename);
        try {
            //存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败："+e.getMessage());
            throw new RuntimeException("上传文件失败，服务器出现异常！"+e);
        }

        //更新当前用户的头像路径（Web访问路径）
        User user = hostHolder.getUser();
        String headUrl = domain+contextPath+"/user/header/"+filename;
        userService.updateHeaderUrl(user.getId(),headUrl);

        return "redirect:/index";
    }

    //获取头像 并用response输出流输出
    @RequestMapping(path = "/header/{filename}",method = RequestMethod.GET)
    public void getHeader(@PathVariable("filename") String filename, HttpServletResponse response){
        //服务器存放路径
        filename = uploadPath+"/"+filename;
        //文件后缀
        String suffix = filename.substring(filename.lastIndexOf('.'));
        //响应图片
        response.setContentType("image/"+suffix);
        try {
            FileInputStream fis =new FileInputStream(filename);
            OutputStream os = response.getOutputStream();
            byte[] buffer = new byte[1024];
            //游标
            int b=0;
            while((b= fis.read(buffer))!=-1){
                os.write(buffer,0,b);
            }

        } catch (FileNotFoundException e) {
            logger.error("读取头像失败："+e.getMessage());
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error("读取头像失败："+e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //修改密码
    @LoginRequired
    @RequestMapping(path = "/modify",method = RequestMethod.POST)
    public String updatePassword(Model model, String oldPassword, String newPassword, String checkNewPassword) {
        if(oldPassword == null) {
            model.addAttribute("oldPasswordError","原始密码不能为空");
            return "/site/setting";
        }
        User user = hostHolder.getUser();
        if(!user.getPassword().equals(CommunityUtil.md5(oldPassword+user.getSalt()))) {
            model.addAttribute("oldPasswordError","原始密码不正确");
            return "/site/setting";
        }
        if(newPassword == null) {
            model.addAttribute("newPasswordError","新密码不能为空");
            return "/site/setting";
        }
        if(!newPassword.equals(checkNewPassword)) {
            model.addAttribute("checkNewPasswordError","两次密码不一致");
            return "/site/setting";
        }
        newPassword = CommunityUtil.md5(newPassword + user.getSalt());
        userService.updatePassword(user.getId(),newPassword);
        return "redirect:/logout";
    }

    //个人主页
    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在!");
        }

        // 用户
        model.addAttribute("user", user);
        // 点赞数量
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);

         //关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);
        // 粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);
        // 是否已关注
        boolean hasFollowed = false;
        if (hostHolder.getUser() != null) {
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);

        return "/site/profile";
    }

}
