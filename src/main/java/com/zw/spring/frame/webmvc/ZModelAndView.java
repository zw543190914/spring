package com.zw.spring.frame.webmvc;

import java.util.Map;

public class ZModelAndView {

    private String viewName;
    private Map<String,Object> model;

    public ZModelAndView(String viewName, Map<String, Object> model) {
        this.viewName = viewName;
        this.model = model;
    }
}
