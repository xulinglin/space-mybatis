package com.space.mybatis.annotation;

import com.space.mybatis.interceptor.MybatisInterceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author xulinglin
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface FieldBind {

    String column();

    Class<? extends MybatisInterceptor> interceptor();
}
