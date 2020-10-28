package com.zw.spring.demo.annotation;

import java.lang.annotation.*;

/**
 * @author 镜中水月
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZwRequestMapping {
    String value() default "";
}
