package com.zw.spring.frame.webmvc;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZViewResolver {
    private String viewName;
    private File templateFile;

    public ZViewResolver(String viewName, File templateFile) {
        this.viewName = viewName;
        this.templateFile = templateFile;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public File getTemplateFile() {
        return templateFile;
    }

    public void setTemplateFile(File templateFile) {
        this.templateFile = templateFile;
    }

    public String viewResolver(ZModelAndView mv)throws Exception{
        StringBuilder sb = new StringBuilder();
        //“r” 以只读方式来打开指定文件夹
        RandomAccessFile ra = new RandomAccessFile(this.templateFile, "r");
        String line = null;
        while (null !=(line = ra.readLine())){
            Matcher matcher = matcher(line);
            while (matcher.find()){
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    // 把 $ 中间字符串取出来
                    String param = matcher.group(i);
                    Object paramValue = mv.getModel().get(param);
                    if (null == paramValue){continue;}
                    line = line.replaceAll("\\$\\{" + param + "}", paramValue.toString());

                }
            }
            sb.append(line);

        }
        return sb.toString();
    }

    private Matcher matcher(String str){
        Pattern pattern = Pattern.compile("\\$\\{(.*?)\\}", Pattern.CASE_INSENSITIVE);
        return pattern.matcher(str);
    }
}
