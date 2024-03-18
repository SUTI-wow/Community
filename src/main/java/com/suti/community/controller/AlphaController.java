package com.suti.community.controller;


import com.suti.community.service.AlphaService;
import com.suti.community.util.CommunityUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;


//一个SpringWeb的小栗子
@Controller
@RequestMapping("/alpha")
public class AlphaController {
    @Autowired
    private AlphaService alphaService;


    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello(){
        return "Hello Spring Boot!";
    }

    @RequestMapping("/data")
    @ResponseBody
    public String getData(){
        return alphaService.find();
    }

    //底层的请求和响应的例子
    @RequestMapping("/http")
    public void  http(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //获取请求数据
        //获取请求方法和路径
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        //获取请求头的一些设置
        Enumeration<String> enumeration = request.getHeaderNames();
        while(enumeration.hasMoreElements()){
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            System.out.println(name+":"+value);
        }
        //获得参数
        System.out.println(request.getParameter("code"));

        //返回响应数据
        response.setContentType("text/html;charset=utf-8");
        //输出流 在try后面小括号里会自动在finally中调用close方法
        try(PrintWriter writer = response.getWriter();)
        {
            writer.write("<h1>suti-wow!</h1>");//输出一行小标题
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //简单的封装方法处理请求和响应
    //GET请求
    //需求：查询所有学生，分页显示【当前页数和每页多少数据】 -->路径：/students?current=1&limit=20
    @RequestMapping(path = "/students",method = RequestMethod.GET) //这里method指定了是请求方法GET才执行
    @ResponseBody  //简单的响应 返回字符串直接显示
    //以下方法会在网页路径中的参数?中直接获取到对应的值；也可以利用RequestParam对这个参数进行更详细的设置
    public String getStudents(
            @RequestParam(name = "current",required = false,defaultValue = "1") int current,
            @RequestParam(name = "limit",required = false,defaultValue = "20") int limit){
        System.out.println(current);
        System.out.println(limit);
        return "some students";
    }

    //根据学生id查询一个学生 /student/123  --这个例子中是把参数变为路径的一部分，此时获取请求方法不一样
    @RequestMapping(path = "/student/{id}",method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id){
        System.out.println(id);
        return "a student";
    }

    //浏览器向服务器提交数据的时候
    //POST请求
    @RequestMapping(path = "/student",method = RequestMethod.POST)
    @ResponseBody
    //参数名称对应 会自动从表单传递过来
    public String saveStudent(String name,int age){
        System.out.println(name);
        System.out.println(age);
        return  "success";
    }

    //响应动态HTML数据
    //需求：查询一个老师的信息并响应一个网页
    @RequestMapping(path = "/teacher",method = RequestMethod.GET)
    //model和view的数据都装在mav里
    public ModelAndView getTeacher(){
        ModelAndView mav = new ModelAndView();
        mav.addObject("name","suti");
        mav.addObject("age",23);
        //默认是在templates目录下，view指的也是view.html
        mav.setViewName("/demo/view");
        return mav;
    }

    @RequestMapping(path = "/school",method = RequestMethod.GET)
    //返回的类型为String，指的是返回的view的路径
    //将model装在参数里，将view直接返回
    public String getSchool(Model model){
        model.addAttribute("name","中山大学");
        model.addAttribute("age","100");
        return "/demo/view";
    }

    //响应JSON数据（异步请求）
    @RequestMapping(path = "/emp",method = RequestMethod.GET)
    @ResponseBody //需要增加这个注解，不然以为返回的是html
    public Map<String,Object> getEmp(){
        Map<String,Object> emp = new HashMap<>();
        emp.put("name","suti");
        emp.put("age",25);
        emp.put("salary",25000);
        return emp;
    }

    //响应JSON数据（一组相似数据）
    @RequestMapping(path = "/emps",method = RequestMethod.GET)
    @ResponseBody //需要增加这个注解，不然以为返回的是html
    public List<Map<String,Object>> getEmps(){
        List<Map<String,Object>> list = new ArrayList<>();

        Map<String,Object> emp = new HashMap<>();
        emp.put("name","suti");
        emp.put("age",25);
        emp.put("salary",25000);
        list.add(emp);

        Map<String,Object> emp1 = new HashMap<>();
        emp1.put("name","suki");
        emp1.put("age",24);
        emp1.put("salary",20000);
        list.add(emp1);

        return list;
    }

    // cookie示例
    @RequestMapping(path = "/cookie/set",method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response){
        //创建cookie
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());

        //设置cookie的生效范围
        cookie.setPath("/community/alpha");
        //设置cookie的生存时间
        cookie.setMaxAge(60*10);
        //发送cookie
        response.addCookie(cookie);
        return "set cookie";
    }

    @RequestMapping(path = "/cookie/get",method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("code") String code){
        System.out.println(code);
        return "get cookie";
    }

    // session实例
    @RequestMapping(path = "/session/set",method = RequestMethod.GET)
    @ResponseBody
    //session是不用自己创建的，mvc会自动注入
    public String setSession(HttpSession session){
        session.setAttribute("id",1);
        session.setAttribute("name","judy");
        return "set session";
    }

    @RequestMapping(path = "/session/get",method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session){
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "get session";
    }

    @RequestMapping(path = "/ajax",method = RequestMethod.POST)
    @ResponseBody
    public String testAjax(int age,String name){
        System.out.println(name+age);
        return CommunityUtil.getJSONString(0,"操作成功");
    }

}
