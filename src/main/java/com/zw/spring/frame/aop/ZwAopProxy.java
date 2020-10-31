package com.zw.spring.frame.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 默认使用JDK动态代理
 */
public class ZwAopProxy implements InvocationHandler {

    private Object target;

    private ZwAopConfig aopConfig;


    public void setAopConfig(ZwAopConfig aopConfig) {
        this.aopConfig = aopConfig;
    }

    // 传入原生对象
    public Object getProxy(Object instance){
        this.target = instance;
        Class<?> clazz = instance.getClass();
        return Proxy.newProxyInstance(clazz.getClassLoader(),clazz.getInterfaces(),this);
    }

    /**
     * 对 application 中指定内容进行增强
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Method m = this.target.getClass().getMethod(method.getName(), method.getParameterTypes());
        // 前置
        if (aopConfig.contains(m)){
            ZwAopConfig.ZwAspect aspect = aopConfig.get(m);
            aspect.getPoints()[0].invoke(aspect.getAspect());
        }

        // 反射执行方法
        Object result = method.invoke(this.target, args);

        // 后置
        if (aopConfig.contains(m)){
            ZwAopConfig.ZwAspect aspect = aopConfig.get(m);
            aspect.getPoints()[1].invoke(aspect.getAspect());
        }
        return result;
    }
}
