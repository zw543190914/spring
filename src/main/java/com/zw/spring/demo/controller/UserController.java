package com.zw.spring.demo.controller;


import com.zw.spring.demo.annotation.ZwAutowired;
import com.zw.spring.demo.annotation.ZwController;
import com.zw.spring.demo.annotation.ZwRequestMapping;
import com.zw.spring.demo.annotation.ZwRequestParam;
import com.zw.spring.demo.service.api.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ZwController
@ZwRequestMapping("/user")
public class UserController {

    @ZwAutowired
    private UserService userService;

    @ZwRequestMapping("/query")
    //http://localhost:8080/zw/user/query?name=zz&age=ww
    public void query(HttpServletRequest request, HttpServletResponse response,
                      @ZwRequestParam("name") String name, @ZwRequestParam("age") String age){
        try {
            System.out.println(name);
            System.out.println(age);
            String result = userService.query(name, age);
            System.out.println("query result == " + result);
            PrintWriter writer = response.getWriter();
            writer.write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
