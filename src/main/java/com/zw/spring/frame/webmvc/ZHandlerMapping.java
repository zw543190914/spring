package com.zw.spring.frame.webmvc;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class ZHandlerMapping {

    private Object controller;
    private Method method;
    // url 支持正则
    private Pattern pattern;

    public ZHandlerMapping(Object controller, Method method, Pattern pattern) {
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;
    }

    public ZHandlerMapping() {
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }
}
