package com.zw.spring.frame.aop;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 获取配置文件，进行封装
 */
public class ZwAopConfig {

    // 需要增强的方法作为key,需要增强的内容为value
    private Map<Method,ZwAspect> points = new HashMap<Method, ZwAspect>();

    /**
     * @param target 需要增强的方法-切入的方法点
     * @param aspect 增强类的实例
     * @param points 前置和后置的增强方法
     */
    public void put(Method target,Object aspect,Method[] points){
        this.points.put(target,new ZwAspect(aspect,points));
    }

    public ZwAspect get(Method method){
        return this.points.get(method);
    }

    public boolean contains(Method method){
        return this.points.containsKey(method);
    }

    /**
     * 对增强代码的封装
     */
    public class ZwAspect{
        // 增强方法的类名-LogAspect
        private Object aspect;
        // 增强的两个方法 -before(),after()
        private Method[] points;

        public ZwAspect(Object aspect, Method[] points) {
            this.aspect = aspect;
            this.points = points;
        }

        public Object getAspect() {
            return aspect;
        }

        public Method[] getPoints() {
            return points;
        }
    }
}
