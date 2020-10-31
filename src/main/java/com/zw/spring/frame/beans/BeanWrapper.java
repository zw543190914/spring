package com.zw.spring.frame.beans;

import com.zw.spring.frame.aop.ZwAopConfig;
import com.zw.spring.frame.aop.ZwAopProxy;
import com.zw.spring.frame.core.FactoryBean;

public class BeanWrapper extends FactoryBean {

    private ZwAopProxy aopProxy = new ZwAopProxy();

    // 使用到观察者模式，支持 事件响应 监听
    private BeanPostProcessor beanPostProcessor;

    private Object wrapperInstance;
    // 原始类
    private Object originalInstance;

    public BeanWrapper(Object instance) {
        // 加入动态代理
        this.wrapperInstance = aopProxy.getProxy(instance);
        this.originalInstance = instance;
    }

    public Object getWrapperInstance() {
        return wrapperInstance;
    }

    public BeanPostProcessor getBeanPostProcessor() {
        return beanPostProcessor;
    }

    public void setBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        this.beanPostProcessor = beanPostProcessor;
    }

    // 返回 代理后的对象
    //  $Proxy0
    public Class<?> getWrapperClass() {
        return this.wrapperInstance.getClass();
    }


    public void setAopConfig(ZwAopConfig config){
        aopProxy.setAopConfig(config);
    }

    public Object getOriginalInstance() {
        return originalInstance;
    }


}
