package com.zw.spring.frame.webmvc.servlet;

import com.zw.spring.frame.annotation.ZController;
import com.zw.spring.frame.annotation.ZRequestMapping;
import com.zw.spring.frame.annotation.ZRequestParam;
import com.zw.spring.frame.aop.ZwAopProxyUtils;
import com.zw.spring.frame.context.ZwApplicationContext;
import com.zw.spring.frame.webmvc.ZHandlerAdapter;
import com.zw.spring.frame.webmvc.ZHandlerMapping;
import com.zw.spring.frame.webmvc.ZModelAndView;
import com.zw.spring.frame.webmvc.ZViewResolver;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DispatcherServlet extends HttpServlet {

    private final String LOCATION = "contextConfigLocation";

    //private Map<String, ZHandlerMapping> handlerMapping = new HashMap<String, ZHandlerMapping>();
    // spring 不用map 使用list
    private List<ZHandlerMapping> handlerMappings = new ArrayList<ZHandlerMapping>();


    private Map<ZHandlerMapping, ZHandlerAdapter> handlerAdapters = new HashMap<ZHandlerMapping, ZHandlerAdapter>();

    private List<ZViewResolver> viewResolvers = new ArrayList<ZViewResolver>();

    @Override
    public void init(ServletConfig config) {
        // 相当于把 ioc init
        ZwApplicationContext applicationContext = new ZwApplicationContext(config.getInitParameter(LOCATION));

        initStrategies(applicationContext);
    }

    protected void initStrategies(ZwApplicationContext context) {
        // 文件上传
        initMultipartResolver(context);
        // 本地化解析
        initThemeResolver(context);

        // 处理器映射成  controller的requestMapping和method 的对应关系
        initHandlerMappings(context);

        // 处理器适配器 动态匹配method参数，包括类转换，动态赋值
        initHandlerAdapters(context);

        initHandlerExceptionErsolvers(context);
        initResquestToViewNameTranslator(context);

        // 通过 viewResolver 解析逻辑视图到具体视图  实现动态模板解析
        initViewResolvers(context);

        // flash 映射处理器
        initFlashMapManager(context);

    }

    private void initFlashMapManager(ZwApplicationContext context) {
    }

    private void initResquestToViewNameTranslator(ZwApplicationContext context) {
    }

    private void initHandlerExceptionErsolvers(ZwApplicationContext context) {
    }

    private void initThemeResolver(ZwApplicationContext context) {
    }

    private void initMultipartResolver(ZwApplicationContext context) {
    }


    private void initViewResolvers(ZwApplicationContext context) {
        // 链接为.html 返回modelAndView，否则通过response写出去
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        File templateRootDir = new File(templateRootPath);
        for (File template : templateRootDir.listFiles()) {
            this.viewResolvers.add(new ZViewResolver(template.getName(), template));
        }

    }


    /**
     * 动态匹配method参数，包括类转换，动态赋值
     */
    private void initHandlerAdapters(ZwApplicationContext context) {
        // 将 参数名称或者类型 按顺序保存
        for (ZHandlerMapping handlerMapping : this.handlerMappings) {
            //一个方法 一个列表
            Map<String, Integer> paramMapping = new HashMap<String, Integer>();
            Annotation[][] pa = handlerMapping.getMethod().getParameterAnnotations();
            // 只处理了 requestParam 注解的。
            for (int i = 0; i < pa.length; i++) {
                for (Annotation annotation : pa[i]) {
                    if (annotation instanceof ZRequestParam) {
                        ZRequestParam requestParam = (ZRequestParam) annotation;
                        String paramName = requestParam.value();
                        if (!"".equals(paramName.trim())) {
                            paramMapping.put(paramName, i);
                        }
                    }
                }
            }

            // 处理 非 命名参数，只处理 request,response
            Class<?>[] paramTypes = handlerMapping.getMethod().getParameterTypes();
            for (int i = 0; i < paramTypes.length; i++) {
                Class<?> type = paramTypes[i];
                if (type == HttpServletRequest.class || type == HttpServletResponse.class) {
                    paramMapping.put(type.getName(), i);
                }
            }

            handlerAdapters.put(handlerMapping, new ZHandlerAdapter(paramMapping));
        }
    }

    /**
     * controller的requestMapping和method 的对应关系
     */
    private void initHandlerMappings(ZwApplicationContext context) {
        // Map<String,Mapping>
        // 取出所有实例
        String[] beanNames = context.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            Object proxy = context.getBean(beanName);
            Object controller = null;
            try {
                controller = ZwAopProxyUtils.getTargetObject(proxy);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            Class<?> clazz = controller.getClass();
            // 不是所有的都需要
            if (!clazz.isAnnotationPresent(ZController.class)) {
                continue;
            }
            String baseUrl = "";
            if (clazz.isAnnotationPresent(ZRequestMapping.class)) {
                ZRequestMapping requestMapping = clazz.getAnnotation(ZRequestMapping.class);
                baseUrl = requestMapping.value();
            }

            // 扫描所有public 方法
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (!method.isAnnotationPresent(ZRequestMapping.class)) {
                    continue;
                }
                ZRequestMapping requestMapping = method.getAnnotation(ZRequestMapping.class);
                String regex = ("/" + baseUrl + requestMapping.value().replaceAll("\\*",".*")).replaceAll("/+", "/");
                Pattern pattern = Pattern.compile(regex);
                this.handlerMappings.add(new ZHandlerMapping(controller, method, pattern));
                System.out.println("Mapping:" + regex + "," + method);
            }

        }
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            doDispatch(request, response);
        } catch (Exception e) {
            response.getWriter().write("500 Exception,Detalis:\r\n" +
                    Arrays.toString(e.getStackTrace())
                            .replaceAll("\\[|\\]", "")
                            .replaceAll("\\s", "\r\n"));
            return;
        }
    }

    private void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {


        // 根据url获取handler
        ZHandlerMapping handler = getHandler(request);
        if (null == handler){
            response.getWriter().write("404 Not Fount");
            return;
        }
        ZHandlerAdapter ha = getHandlerAdapter(handler);

        ZModelAndView mv = ha.handler(request, response, handler);


        processDispatcherResult(request,response, mv);


    }

    private void processDispatcherResult(HttpServletRequest request,
                                         HttpServletResponse response,
                                         ZModelAndView mv) throws Exception{
        // 调用 viewResolver的resolveView()
        if (null == mv){
            return;
        }
        if (this.viewResolvers.isEmpty()){
            return;
        }
        for (ZViewResolver viewResolver : this.viewResolvers) {
            if (!mv.getViewName().equals(viewResolver.getViewName())){
                continue;
            }
            String out = viewResolver.viewResolver(mv);
            if (null != out){
                response.getWriter().write(out);
                break;
            }
        }
    }

    private ZHandlerAdapter getHandlerAdapter(ZHandlerMapping handler) {
        if (this.handlerAdapters.isEmpty()){
            return null;
        }
        return handlerAdapters.get(handler);
    }

    /**
     * 根据url获取handler
     */
    private ZHandlerMapping getHandler(HttpServletRequest request) {
        if (this.handlerMappings.isEmpty()){
            return null;
        }
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        uri = uri.replaceAll(contextPath, "").replaceAll("/+", "/");
        for (ZHandlerMapping handler : handlerMappings) {
            Matcher matcher = handler.getPattern().matcher(uri);
            if (!matcher.matches()){
                continue;
            }
            return handler;
        }
        return null;
    }
}

