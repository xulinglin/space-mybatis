package com.space.core;


import com.space.core.annotation.FieldBind;

/**
 * @author xulinglin
 */
public class FieldBinds {

    private FieldBinds(){}

    protected FieldBinds(String key, boolean mybatis, String column, Class<? extends Interceptor> interceptor, Integer set, Integer get){
        this.key = key;
        this.mybatis = mybatis;
        this.column = column;
        this.interceptor = interceptor;
        this.set = set;
        this.get = get;
    }

    private String key;
    private boolean mybatis;
    private String column;
    private Class<? extends Interceptor> interceptor;
    private Integer set;
    private Integer get;

    public String getKey() {
        return key;
    }

    public boolean isMybatis() {
        return mybatis;
    }

    public String getColumn() {
        return column;
    }

    public Class<? extends Interceptor> getInterceptor() {
        return interceptor;
    }

    public Integer getSet() { return set; }

    public Integer getGet() { return get; }
}
