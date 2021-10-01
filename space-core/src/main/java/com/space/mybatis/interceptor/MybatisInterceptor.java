package com.space.mybatis.interceptor;

import com.space.mybatis.interceptor.MybatisFieldInterceptor;

import java.util.Collection;

/**
 * @author xulinglin
 */
public abstract class MybatisInterceptor<T> {

    public T execution(Collection<?> coll){
        T t = null;
        try {
            MybatisFieldInterceptor.threadLocal.set(this);
            t = get(coll);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            MybatisFieldInterceptor.threadLocal.remove();
        }
        return t;
    }

    protected abstract T get(Collection<?> coll);
}
