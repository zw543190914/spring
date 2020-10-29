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
import java.util.HashMap;
import java.util.Map;

@ZController
@ZRequestMapping("/")
public class IndexController {

    @ZAutowired
    private IUserService userService;

    @ZRequestMapping("/index.html")
    public ZModelAndView index(HttpServletRequest request,
                               HttpServletResponse response,
                               @ZRequestParam("name") String name){
        String result = userService.query(name);
        System.out.println(result);
        Map<String,Object> model = new HashMap<String, Object>();
        return new ZModelAndView("index.html",model);
    }

}
