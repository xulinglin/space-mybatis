package com.space.mybatis.interceptor;

import com.space.mybatis.annotation.FieldBind;

public class MybatisFields {

    private MybatisFields(){}

    public MybatisFields(String key, FieldBind value, String column, Class<? extends MybatisInterceptor> interceptor){
        this.key = key;
        this.value = value;
        this.column = column;
        this.interceptor = interceptor;
    }

    private String key;
    private FieldBind value;
    private String column;
    private Class<? extends MybatisInterceptor> interceptor;

    public String getKey() {
        return key;
    }

    public FieldBind getValue() {
        return value;
    }

    public String getColumn() {
        return column;
    }

    public Class<? extends MybatisInterceptor> getInterceptor() {
        return interceptor;
    }
}
