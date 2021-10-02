package com.space.core;

import java.util.Collection;
import java.util.Map;

/**
 * @author xulinglin
 */
public abstract class Interceptor<K,V> {

    protected Map<K,V> execution(Collection<?> coll){
        Map<K,V> t = null;
        try {
            FieldInterceptor.threadLocal.set(this);
            t = get(coll);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            FieldInterceptor.threadLocal.remove();
        }
        return t;
    }

    protected abstract Map<K,V> get(Collection<?> coll);
}
