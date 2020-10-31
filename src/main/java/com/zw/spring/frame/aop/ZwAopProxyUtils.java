package com.zw.spring.frame.aop;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


public class ZwAopProxyUtils {

    //通过代理对象，找到原始对象
    public static Object getTargetObject(Object proxy) throws Exception{
        // 如果不是代理对象直接返回
        if (!isAopProxy(proxy)){
            return proxy;
        }
        return getProxyTargetObject(proxy);
    }

    private static boolean isAopProxy(Object object){
        return Proxy.isProxyClass(object.getClass());
    }

    private static Object getProxyTargetObject(Object proxy) throws Exception{
        // 代理对象有一个 h 字段
        Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
        h.setAccessible(true);
        ZwAopProxy aopProxy = (ZwAopProxy)h.get(proxy);
        Field target = aopProxy.getClass().getDeclaredField("target");
        target.setAccessible(true);
        return target.get(aopProxy);

    }

}
