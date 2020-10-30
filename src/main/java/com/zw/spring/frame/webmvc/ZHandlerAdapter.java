package com.zw.spring.frame.webmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;

public class ZHandlerAdapter {

    private Map<String,Integer> paramMapping;

    public ZHandlerAdapter(Map<String, Integer> paramMapping) {
        this.paramMapping = paramMapping;
    }

    public ZModelAndView handler(HttpServletRequest request,
                                 HttpServletResponse response,
                                 ZHandlerMapping handler) throws Exception{

        // 1 获取方法形参列表
        Class<?>[] parameterTypes = handler.getMethod().getParameterTypes();

        // 2 获取自定义命名参数所在位置
        Map<String,String[]> parameterMap = request.getParameterMap();
        // 3 构建实参列表
        Object[] paramValues = new Object[parameterTypes.length];
        for (Map.Entry<String,String[]>  param: parameterMap.entrySet()){
            String value = Arrays.toString(param.getValue())
                    .replaceAll("\\[|\\]", "")
                    // 替换所有空格
                    .replaceAll("\\s", "");
            if (!this.paramMapping.containsKey(param.getKey())){continue;}
            Integer index = this.paramMapping.get(param.getKey());
            // 对参数进行转换
            paramValues[index] = caseStringValue(value,parameterTypes[index]);
        }

        if (this.paramMapping.containsKey(HttpServletRequest.class.getName())){
            Integer reqIndex = this.paramMapping.get(HttpServletRequest.class.getName());
            paramValues[reqIndex] = request;
        }
        if (this.paramMapping.containsKey(HttpServletResponse.class.getName())){
            Integer resIndex = this.paramMapping.get(HttpServletResponse.class.getName());
            paramValues[resIndex] = response;
        }

        // 4 从 handler中获取 controller method 然后进行调用
        Object result = handler.getMethod().invoke(handler.getController(), paramValues);
        if (null == result){
            return null;
        }
        if (handler.getMethod().getReturnType() == ZModelAndView.class){
            return (ZModelAndView)result;
        } else {
            return null;
        }

    }

    private Object caseStringValue(String value,Class clazz){
        if (clazz == String.class){
            return value;
        }
        if (clazz == Integer.class){
            return Integer.valueOf(value);
        }
        if (clazz == int.class){
            return Integer.valueOf(value);
        }
        return null;
    }
}
