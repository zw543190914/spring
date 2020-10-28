package com.zw.spring.frame.beans;

/**
 * 用于事件监听
 */
public class BeanPostProcessor {

    public Object postProcessBeforeInitialization(Object bean,String beanName){

        return bean;
    }

    public Object postProcessAfterInitialization(Object bean,String beanName){

        return bean;
    }

}
