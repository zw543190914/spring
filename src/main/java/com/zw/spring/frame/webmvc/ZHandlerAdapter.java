package com.zw.spring.frame.webmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class ZHandlerAdapter {

    private Map<String,Integer> paramMapping;

    public ZHandlerAdapter(Map<String, Integer> paramMapping) {
        this.paramMapping = paramMapping;
    }

    public ZModelAndView handler(HttpServletRequest request, HttpServletResponse response, ZHandlerMapping handler) {
        return null;
    }
}
