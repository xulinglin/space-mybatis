package com.space.core.auth;

import com.space.core.FieldInterceptor;
import com.space.core.Interceptor;
import com.space.core.aop.AuthorityAop;
import com.space.core.bean.Tools;
import org.apache.ibatis.executor.CachingExecutor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author xulinglin
 */
@Component
@Intercepts({
        @Signature(type = Executor.class, method = MybatisInterceptorAuthority.QUERY,
        args = {MappedStatement.class, Object.class, RowBounds.class,ResultHandler.class})
})
public class MybatisInterceptorAuthority implements org.apache.ibatis.plugin.Interceptor {

    @Autowired(required = false)
    private AuthorityConfig authConfig;

    public static Logger log = Logger.getLogger(MybatisInterceptorAuthority.class.getName());
    public static final String QUERY = "query";
    private static final int MAPPED_STATEMENT_INDEX = 0;
    private static final int PARAM_OBJ_INDEX = 1;
    private static final String BEFORE_SQL = "select a.* from (";
    private static final String AFTER_SQL = ") a";
    private static final String WHERE = " where a.";
    private static final String IN = " in ";
    private static final String RIGHT  = "(";
    private static final String Left = ")";
    private static final String COMMA = ",";

    private String resetSql(String sql, String column, List<String> param) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(BEFORE_SQL + sql + AFTER_SQL);
        if(Tools.isNotNull(param)){
            sqlBuilder.append(WHERE).
                    append(column).
                    append(IN).
                    append(RIGHT+param.stream().collect(Collectors.joining(COMMA))+Left)
            ;
        }
        return sqlBuilder.toString();
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        resetSql(invocation);
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        if(target instanceof CachingExecutor){
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {

    }

    private void resetSql(Invocation invocation) {
        Interceptor interceptor = FieldInterceptor.get();
        if(null != interceptor || null == AuthorityAop.get() || null == authConfig)
            return;
        if(AuthorityAop.isAdmin()){
            log.info("Admin Intercept sql permissions Skip execution");
            return;
        }
        List<String> list = null;
        AuthorityInterface authorityInterface = authConfig.getAuthorityInterface();
        String column = authConfig.getColumn();
        if(null != authorityInterface){
            list = authorityInterface.get();
        }else{
            return;
        }

        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement)args[MAPPED_STATEMENT_INDEX];
        if(ms == null){
            return;
        }
        Object parameterObject = args[PARAM_OBJ_INDEX];
        // sql语句类型 select、delete、insert、update
        String sqlCommandType = ms.getSqlCommandType().toString();
        log.info("Intercept sql permissions Start ----");
        // id为执行的mapper方法的全路径名，如com.mapper.UserMapper
        log.info("Execute mapper: " + ms.getId());
        log.info("Execute type: "+ sqlCommandType);
        BoundSql boundSql = ms.getBoundSql(parameterObject);
        String origSql = boundSql.getSql();
        log.info("Before the change SQL: "+ origSql);


        String sql = resetSql(origSql,column,list);

        log.info("After the change SQL: "+ sql);
        BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), sql,
                boundSql.getParameterMappings(), boundSql.getParameterObject());
        // 把新的查询放到statement里
        MappedStatement newStatement = newMappedStatement(ms, new BoundSqlSource(newBoundSql));
        for (ParameterMapping mapping : boundSql.getParameterMappings()) {
            String prop = mapping.getProperty();
            if (boundSql.hasAdditionalParameter(prop)) {
                newBoundSql.setAdditionalParameter(prop, boundSql.getAdditionalParameter(prop));
            }
        }
        args[MAPPED_STATEMENT_INDEX] = newStatement;
        log.info("Intercept sql permissions End ----");
    }

    private MappedStatement newMappedStatement (MappedStatement ms, SqlSource newSqlSource) {
        MappedStatement.Builder builder = new
                MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length > MAPPED_STATEMENT_INDEX) {
            builder.keyProperty(ms.getKeyProperties()[MAPPED_STATEMENT_INDEX]);
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());
        return builder.build();
    }

}
