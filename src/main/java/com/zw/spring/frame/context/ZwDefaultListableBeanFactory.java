package com.zw.spring.frame.context;

import com.zw.spring.frame.beans.BeanDefinition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ZwDefaultListableBeanFactory extends ZwAbstractApplicationContext{

    // 保存配置信息
    protected Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();


    @Override
    protected void refreshBeanFactory() {

    }

    @Override
    protected void onRefresh(){

    }
}
