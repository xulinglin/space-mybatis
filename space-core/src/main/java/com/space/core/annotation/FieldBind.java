package com.space.core.annotation;

import com.space.core.Interceptor;

import java.lang.annotation.*;

/**
 * @author xulinglin
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface FieldBind{

    String column();

    Class<? extends Interceptor> interceptor();

    boolean mybatis() default false;

}
