package com.zw.spring.service.impl;

import com.zw.spring.annotation.ZwService;
import com.zw.spring.service.api.UserService;

@ZwService
public class UserServiceImpl implements UserService {

    public String query(String name, String age) {
        return "hello " + name +", your age is " + age;
    }
}
