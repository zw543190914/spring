package com.zw.spring.frame.servlet;

import com.zw.spring.frame.context.ZwApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class DispatcherServlet extends HttpServlet {

    private final String LOCATION = "contextConfigLocation";

    @Override
    public void init(ServletConfig config) {
        ZwApplicationContext applicationContext = new ZwApplicationContext(config.getInitParameter(LOCATION));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}

