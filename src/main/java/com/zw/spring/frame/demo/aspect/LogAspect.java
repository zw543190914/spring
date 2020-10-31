package com.zw.spring.frame.demo.aspect;

public class LogAspect {


    public void before(){
        System.out.println("AOP=====before");
    }

    public void after(){
        System.out.println("AOP=====after");

    }
}
