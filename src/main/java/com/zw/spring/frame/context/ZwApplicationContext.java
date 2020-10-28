package com.zw.spring.frame.context;

import com.alibaba.fastjson.JSON;
import com.zw.spring.frame.annotation.ZAutowired;
import com.zw.spring.frame.annotation.ZController;
import com.zw.spring.frame.annotation.ZService;
import com.zw.spring.frame.beans.BeanDefinition;
import com.zw.spring.frame.beans.BeanPostProcessor;
import com.zw.spring.frame.beans.BeanWrapper;
import com.zw.spring.frame.context.support.BeanDefinitionReader;
import com.zw.spring.frame.core.BeanFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ZwApplicationContext implements BeanFactory {

    private String[] configLocation;

    private BeanDefinitionReader reader;

    // 保存配置信息
    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();

    // 保证 注册式单例
    private Map<String, Object> beanCachMap = new ConcurrentHashMap<String, Object>();

    // 存储所有被代理的对象
    private Map<String, BeanWrapper> beanWrapperMap = new ConcurrentHashMap<String, BeanWrapper>();


    public ZwApplicationContext(String... configLocation) {
        this.configLocation = configLocation;
        refresh();
    }

    public void refresh(){

        // 定位
        this.reader = new BeanDefinitionReader(configLocation);
        // 加载
        List<String> beanDefinitions = reader.loadBeanDefinitions();
        // 注册
        doRegister(beanDefinitions);

        // 依赖注入 （lazy-init = false）
        doAutowired();

    }

    private void doAutowired() {
        for (Map.Entry<String,BeanDefinition> beanDefinitionEntry : this.beanDefinitionMap.entrySet()){
            String beanName = beanDefinitionEntry.getKey();
            if (!beanDefinitionEntry.getValue().isLazyInit()){
                Object bean = getBean(beanName);
            }
        }
    }

    /**
     * 将 beanDefinitions 注册到IOC beanDefinitionMap
     * @param beanDefinitions
     */
    private void doRegister(List<String> beanDefinitions) {

        // beanName 1:默认类名搜字母小写
        // 2 接口注入
        // 3 自定义名称
        try {
            for (String className : beanDefinitions) {
                Class<?> beanClass = Class.forName(className);

                if (beanClass.isInterface()){
                    continue;
                }

                BeanDefinition beanDefinition = reader.registerBean(className);

                if (null != beanDefinition){
                    this.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(),beanDefinition);
                }

                Class<?>[] interfaces = beanClass.getInterfaces();
                for (Class<?> anInterface : interfaces) {
                    // 多个实现类 只能覆盖(spring 会报错)---可以指定自定义名称
                    this.beanDefinitionMap.put(anInterface.getName(),beanDefinition);
                }

            }

            System.out.println("beanDefinitionMap == " + JSON.toJSONString(beanDefinitionMap));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取 BeanDefinition 信息，创建实例
     * spring不会创建原始对象，会使用 BeanWrapper 进行包装
     * 包装器模式： 1 保留原来 OOP 关系， 对其增强
     */
    @Override
    public Object getBean(String beanName){
        BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
        String beanClassName = beanDefinition.getBeanClassName();

        // 生成 通知事件
        BeanPostProcessor beanPostProcessor = new BeanPostProcessor();

        Object instance = instanceBean(beanDefinition);

        // 实例初始化之前
        beanPostProcessor.postProcessBeforeInitialization(instance,beanName);

        if (null == instance){
            return null;
        }
        BeanWrapper beanWrapper = new BeanWrapper(instance);
        this.beanWrapperMap.put(beanClassName,beanWrapper);

        // 实例初始化之后
        beanPostProcessor.postProcessAfterInitialization(instance,beanName);

        populateBean(beanName,instance);

        // 给自己留有可操作空间
        return this.beanWrapperMap.get(beanClassName).getWrapperInstance();
    }

    public void populateBean(String beanName, Object instance){
        Class<?> clazz = instance.getClass();
        // 只注入 带注解的
        if (!(clazz.isAnnotationPresent(ZController.class) || clazz.isAnnotationPresent(ZService.class))){
            return;
        }

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(ZAutowired.class)){
                continue;
            }

            ZAutowired autowired = field.getAnnotation(ZAutowired.class);
            String autowiredBeanName = autowired.value().trim();
            if ("".equals(autowiredBeanName)){
                // 按照类型装配
                autowiredBeanName = field.getType().getName();
            }
            field.setAccessible(true);
            try {
                System.out.println("==== " + instance + " , " + autowiredBeanName + " , " + this.beanWrapperMap.get(autowiredBeanName));

                field.set(instance,this.beanWrapperMap.get(autowiredBeanName).getWrapperClass());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

    }

    private Object instanceBean(BeanDefinition beanDefinition){
        Object instance = null;
        String beanClassName = beanDefinition.getBeanClassName();

        try {
            if (this.beanCachMap.containsKey(beanClassName)){
                instance = this.beanCachMap.get(beanClassName);
            } else {
                Class<?> clazz = Class.forName(beanClassName);
                instance = clazz.newInstance();
                this.beanCachMap.put(beanClassName,instance);
            }
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ZwApplicationContext() {
    }
}
