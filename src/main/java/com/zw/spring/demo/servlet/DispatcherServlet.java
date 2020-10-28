package com.zw.spring.demo.servlet;

import com.zw.spring.demo.annotation.ZwAutowired;
import com.zw.spring.demo.annotation.ZwController;
import com.zw.spring.demo.annotation.ZwService;
import com.zw.spring.demo.controller.UserController;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DispatcherServlet extends HttpServlet {

    private Properties contextConfig = new Properties();


    Map<String,Object> beanMap = new ConcurrentHashMap<String, Object>();

    List<String> classNames = new ArrayList<String>();

    @Override
    public void init(ServletConfig config){
        // 定位
        doLoadConfig(config.getInitParameter("contextConfigLocation"));
        // 加载
        doScanner(contextConfig.getProperty("scanPackage"));
        // 注册
        doRegistry();
        // 自动依赖注入
        // spring 在 getBean 时候注入
        doAutowired();
        // 验证注入
        UserController userController = (UserController) beanMap.get("userController");
        userController.query(null,null,"zw","12");
        // springmvc handlerMapping
        // 将url 和 method 关联
        initHandlerMapping();
    }

    private void initHandlerMapping() {

    }

    private void doAutowired() {
        if (beanMap.isEmpty()){
            return;
        }
        for (Map.Entry<String,Object> entry : beanMap.entrySet()){
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for (Field field : fields) {
                if (!field.isAnnotationPresent(ZwAutowired.class)){
                    continue;
                }
                ZwAutowired autowired = field.getAnnotation(ZwAutowired.class);
                String beanName = autowired.value().trim();
                if ("".equals(beanName)){
                    beanName = field.getType().getName();
                }
                field.setAccessible(true);
                try {
                    field.set(entry.getValue(),beanMap.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doRegistry() {
        if (classNames.isEmpty()){
            return;
        }
        try {
            for (String className : classNames) {
                Class<?> clazz = Class.forName(className);
                // 在spring 中使用多个 parse方法处理
                if (clazz.isAnnotationPresent(ZwController.class)){

                    String beanName = lowerFirstCase(clazz.getSimpleName());
                    // 在spring中，put BeanDefinition
                    beanMap.put(beanName,clazz.newInstance());

                } else if (clazz.isAnnotationPresent(ZwService.class)){
                    // 默认首字母小写，优先自定义
                    // 如果是接口，按照接口类型注入
                    // 在spring 中使用多个方法处理 autowiredByName autowiredByType
                    ZwService service = clazz.getAnnotation(ZwService.class);
                    String beanName = service.value();
                    if ("".equals(beanName.trim())){
                        beanName = lowerFirstCase(clazz.getSimpleName());
                    }
                    Object instance = clazz.newInstance();
                    beanMap.put(beanName,instance);
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> anInterface : interfaces) {
                        beanMap.put(anInterface.getName(),instance);
                    }

                } else {
                    continue;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    private void doScanner(String packageName) {
        URL url = this.getClass().getClassLoader().getResource("/" + packageName.replaceAll("\\.", "/"));
        File classFile = new File(url.getFile());
        for (File file : classFile.listFiles()){
            if (file.isDirectory()){
                doScanner(packageName + "." + file.getName());
            } else {
                classNames.add(packageName + "." + file.getName().replace(".class",""));
            }
        }

    }

    private void doLoadConfig(String location) {
        // classpath:application.properties
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(location.replace("classpath:",""));
        try {
            contextConfig.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }


    private String lowerFirstCase(String str){
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
