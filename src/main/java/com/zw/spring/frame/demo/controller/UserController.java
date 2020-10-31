package com.zw.spring.frame.demo.controller;

import com.zw.spring.frame.annotation.ZAutowired;
import com.zw.spring.frame.annotation.ZController;
import com.zw.spring.frame.annotation.ZRequestMapping;
import com.zw.spring.frame.annotation.ZRequestParam;
import com.zw.spring.frame.demo.service.api.IUserService;
import com.zw.spring.frame.webmvc.ZModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ZController
@ZRequestMapping("user")
public class UserController {

    @ZAutowired
    private IUserService userService;

    @ZRequestMapping("/query")
    public ZModelAndView query(HttpServletRequest request,
                               HttpServletResponse response,
                               @ZRequestParam("name") String name){
        String result = userService.query(name);
        System.out.println(result);
        return out(response,result);
    }

    @ZRequestMapping("/add*.json")
    //http://localhost:8080/spring/user/addUser.json?name=zw&name=ss&addr=henan
    public ZModelAndView add(HttpServletRequest request,
                               HttpServletResponse response,
                               @ZRequestParam("name") String name,
                             @ZRequestParam("addr") String addr) {
        String result = userService.add(name,addr);
        System.out.println(result);
        return out(response, result);
    }
    private ZModelAndView out( HttpServletResponse response,String param){
        try {
            response.getWriter().write(param);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
