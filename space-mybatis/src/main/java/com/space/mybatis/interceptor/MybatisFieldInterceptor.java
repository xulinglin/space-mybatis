package com.space.mybatis.interceptor;

import cn.hutool.core.util.ReflectUtil;
import com.esotericsoftware.reflectasm.MethodAccess;
import com.space.mybatis.annotation.FieldBind;
import com.space.mybatis.asm.ASMUtils;
import com.space.mybatis.bean.SpringUtils;
import com.space.mybatis.bean.Tool;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.*;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @author xulinglin
 */
@Component
@Intercepts({ @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = { Statement.class }) })
public class MybatisFieldInterceptor implements Interceptor {

    private static Map<String,Map<String, FieldBind>> fieldCache = new ConcurrentHashMap<>();

    protected static ThreadLocal<MybatisInterceptor> threadLocal = new ThreadLocal<>();

    public Object intercept(Invocation invocation) throws Throwable {
        return setFieldValue(invocation.proceed());
    }

    public static Object fieldBindValue(List list){
        return setFieldValue(list);
    }

    protected static Object setFieldValue(Object result){
        try {
            MybatisInterceptor mybatisInterceptor = threadLocal.get();
            if (null == mybatisInterceptor && result instanceof ArrayList) {
                List resultList = (ArrayList) result;
                if(Tool.isNotNull(resultList)){
                    Object o = resultList.get(Tool.zero);
                    Class<?> aClass = o.getClass();
                    String className = aClass.getName();
                    if(instanceofType(className))
                        return result;
                    Map<String, FieldBind> fieldBindMap = fieldBindMap(className, aClass);
                    if(null != fieldBindMap && Tool.zero < fieldBindMap.size()){
                        Iterator<Map.Entry<String, FieldBind>> entryIterator = fieldBindMap.entrySet().iterator();
                        /**
                         * 多个字段为相同 interceptor 进行合并查询
                         */
                        Map<Class<? extends MybatisInterceptor>,List<MybatisFields>> map = new ConcurrentHashMap<>();
                        while (entryIterator.hasNext()){
                            Map.Entry<String, FieldBind> next = entryIterator.next();
                            String key = next.getKey();
                            FieldBind value = next.getValue();
                            String column = value.column();
                            Class<? extends MybatisInterceptor> interceptor = value.interceptor();
                            if(Tool.isNotBlank(column) && null != interceptor){
                                List<MybatisFields> list = map.get(interceptor);
                                if(Tool.isNull(list)){
                                    list = new CopyOnWriteArrayList<>();
                                    map.put(interceptor,list);
                                }
                                list.add(new MybatisFields(key,value,column,interceptor));
                            }
                        }
                        Iterator<Map.Entry<Class<? extends MybatisInterceptor>, List<MybatisFields>>> iterator = map.entrySet().iterator();
                        while (iterator.hasNext()){
                            Map.Entry<Class<? extends MybatisInterceptor>, List<MybatisFields>> next = iterator.next();
                            List<MybatisFields> value = next.getValue();
                            List coll = new CopyOnWriteArrayList<>();
                            Class<? extends MybatisInterceptor> interceptor = value.get(Tool.zero).getInterceptor();
                            resultList.parallelStream().forEach(o1->{
                                MethodAccess methodAccess = ASMUtils.methodAccessFactory(o1);
                                value.parallelStream().forEach(v->{
                                    String column = v.getColumn();
                                    String name = ASMUtils.captureName(column);
                                    Map<String, Integer> methodIndexOfGet = ASMUtils.methodIndexOfGet.get(aClass);
                                    Integer get = methodIndexOfGet.get(name);
                                    Object invoke = methodAccess.invoke(o1, get);
                                    if(null != invoke){
                                        coll.add(invoke);
                                    }
                                });
                            });
                            MybatisInterceptor bean = SpringUtils.getBean(interceptor);
                            Object collect = coll.stream().distinct().collect(Collectors.toList());
                            Map mapValue = (Map) bean.execution((List)collect);
                            if(null != mapValue && Tool.zero < mapValue.size()){
                                resultList.forEach(o1->{
                                    MethodAccess methodAccess = ASMUtils.methodAccessFactory(o1);
                                    value.forEach(v->{
                                        String column = v.getColumn();
                                        String key = v.getKey();
                                        String name = ASMUtils.captureName(column);
                                        Map<String, Integer> methodIndexOfGet = ASMUtils.methodIndexOfGet.get(aClass);
                                        Integer get = methodIndexOfGet.get(name);
                                        Object invoke = methodAccess.invoke(o1, get);
                                        Object o2 = mapValue.get(invoke);
                                        if(null != o2){
                                            Map<String, Integer> methodIndexOfSet = ASMUtils.methodIndexOfSet.get(aClass);
                                            String key1 = ASMUtils.captureName(key);
                                            Integer set = methodIndexOfSet.get(key1);
                                            methodAccess.invoke(o1, set,o2);
                                        }
                                    });
                                });
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    protected static boolean instanceofType(String className){
        if(className.equals("java.lang.String") ||
           className.equals("java.lang.Integer") ||
           className.equals("java.lang.Long") ||
           className.equals("java.lang.Short") ||
           className.equals("java.lang.Float") ||
           className.equals("java.lang.Double") ||
           className.equals("java.lang.Byte") ||
           className.equals("java.util.List") ||
           className.equals("java.util.Map")
        ){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    protected static Map<String, FieldBind> fieldBindMap(String className,Class<?> aClass){
        Map<String, FieldBind> fieldBindMap = null;
        if(null == (fieldBindMap = fieldCache.get(className))){
            fieldBindMap = new HashMap<>();
            Field[] fields = ReflectUtil.getFields(aClass);
            for (Field field : fields) {
                FieldBind fieldBind = field.getAnnotation(FieldBind.class);
                if (null == fieldBind)
                    continue;
                fieldBindMap.put(field.getName(),fieldBind);
            }
            fieldCache.put(className,fieldBindMap);
        }
        return fieldBindMap;
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
 
