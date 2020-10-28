package com.zw.spring.demo.service.impl;

import com.zw.spring.demo.annotation.ZwService;
import com.zw.spring.demo.service.api.UserService;

@ZwService
public class UserServiceImpl implements UserService {

    public String query(String name, String age) {
        return "hello " + name +", your age is " + age;
    }
}
