package com.zw.spring.demo.annotation;

import java.lang.annotation.*;

/**
 * @author 镜中水月
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZwRequestParam {
    String value() default "";
}
