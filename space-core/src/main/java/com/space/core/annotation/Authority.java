package com.space.core.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据权限调用,必须在 spring bean管理下
 * 改该方法注解在声明时,当前方法线程内执行的SQL动态添加权限字段进行操作
 * @author xulinglin
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Authority {
}
