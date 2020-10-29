package com.zw.spring.frame.webmvc;

import java.io.File;

public class ZViewResolver {
    private String viewName;
    private File templateFile;

    public ZViewResolver(String viewName, File templateFile) {
        this.viewName = viewName;
        this.templateFile = templateFile;
    }

    public String viewResolver(ZModelAndView mv){
        return null;
    }

}
