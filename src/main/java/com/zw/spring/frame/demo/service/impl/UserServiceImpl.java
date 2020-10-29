package com.zw.spring.frame.demo.service.impl;

import com.zw.spring.frame.annotation.ZService;
import com.zw.spring.frame.demo.service.api.IUserService;

@ZService
public class UserServiceImpl implements IUserService {

    @Override
    public String query(String name){
        return "hello :: " +name;
    }
}
