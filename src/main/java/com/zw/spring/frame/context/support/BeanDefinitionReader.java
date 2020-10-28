package com.zw.spring.frame.context.support;

import com.alibaba.fastjson.JSON;
import com.zw.spring.frame.beans.BeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 对配置文件进行查找 定位 读取 解析
 */
public class BeanDefinitionReader  {

    private Properties config = new Properties();

    private List<String> registerBeanClasses = new ArrayList<String>();

    /**
     * 配置文件中获取自动扫描包名的key
     */
    private final String SCAN_PACKAGE = "scanPackage";

    public BeanDefinitionReader(String ... locations) {
        // classpath:application.properties
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:",""));
        try {
            config.load(in);
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
        doScanner(config.getProperty(SCAN_PACKAGE));
    }


    public List<String> loadBeanDefinitions(){

        return registerBeanClasses;
    }

    /**
     * 每注册一个className 就返回一个BeanDefinition
     * 对配置信息的包装
     */
    public BeanDefinition registerBean(String className){
        if (this.registerBeanClasses.contains(className)){
            BeanDefinition beanDefinition = new BeanDefinition();
            beanDefinition.setBeanClassName(className);
            // 在 IOC 名称，默认首字母小写
            beanDefinition.setFactoryBeanName(lowerFirstCase(className.substring(className.lastIndexOf(".") + 1)));
            return beanDefinition;
        }
        return null;
    }

    public Properties getConfig(){
        return this.config;
    }

    private void doScanner(String packageName) {
        URL url = this.getClass().getClassLoader().getResource("/" + packageName.replaceAll("\\.", "/"));
        File classFile = new File(url.getFile());
        for (File file : classFile.listFiles()){
            if (file.isDirectory()){
                doScanner(packageName + "." + file.getName());
            } else {
                registerBeanClasses.add(packageName + "." + file.getName().replace(".class",""));
            }
        }
        System.out.println("registerBeanClasses == " + JSON.toJSONString(registerBeanClasses));
    }

    private String lowerFirstCase(String str){
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    public BeanDefinitionReader() {
    }
}
