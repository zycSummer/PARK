package com.jet.cloud.deepmind.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * @author yhy
 * @create 2019-11-22 15:11
 */
@Target(ElementType.METHOD)
public @interface QueryMethodLog {


    String content() default "";
    }
