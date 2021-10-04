package com.space.core;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.space.core.asm.ASMUtils;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.springframework.stereotype.Component;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

/**
 * @author xulinglin
 */
@Component
@Intercepts({
        @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = { Statement.class })
})
public class MybatisFieldInterceptor implements org.apache.ibatis.plugin.Interceptor {

    public Object intercept(Invocation invocation) throws Throwable {
       Object result = invocation.proceed();;
        if (result instanceof ArrayList) {
            return FieldInterceptor.setFieldValue((ArrayList) result,Boolean.TRUE);
        }
        return result;
    }

    public Object plugin(Object target) {
        if (target instanceof ResultSetHandler) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    public void setProperties(Properties properties) {

    }
}
 
