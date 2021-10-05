package com.space.core;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.space.core.annotation.ExecutedBind;
import com.space.core.annotation.FieldBind;
import com.space.core.asm.ASMUtils;
import com.space.core.bean.SpringUtils;
import com.space.core.bean.Tools;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author xulinglin
 */
public class FieldInterceptor {

    public static Logger log = Logger.getLogger(FieldInterceptor.class.getName());

    private static Map<String, Map<String, FieldBinds>> fieldCache = new ConcurrentHashMap<>();
    private static Map<Class<?>, Boolean> executedBindCache = new ConcurrentHashMap<>();
    protected static ThreadLocal<Interceptor> threadLocal = new ThreadLocal<>();

    public static Object setFieldValue(List result){
        return setFieldValue(result,Boolean.FALSE);
    }

    protected static Object setFieldValue(List result,boolean bool){
        try {
            Interceptor mybatisInterceptor = threadLocal.get();
            if (null == mybatisInterceptor && Tools.isNotNull(result)) {
                Class<?> clazz = result.get(Tools.zero).getClass();
                String className = clazz.getName();
                if(instanceofType(className))
                    return result;
                Map<String, FieldBinds> fieldBindMap = fieldBindMap(className, clazz);
                if(isMapNotNull(fieldBindMap)){
                    Map<? extends Class<? extends Interceptor>, List<FieldBinds>> collectGroupingBy=
                            fieldBindMap.values().stream().collect(Collectors.groupingBy(FieldBinds::getInterceptor));
                    Iterator<? extends Map.Entry<? extends Class<? extends Interceptor>, List<FieldBinds>>> iteratorGroupingBy  = collectGroupingBy.entrySet().iterator();
                    while (iteratorGroupingBy.hasNext()){
                        Map.Entry<? extends Class<? extends Interceptor>, List<FieldBinds>> next = iteratorGroupingBy.next();
                        Class<? extends Interceptor> key = next.getKey();
                        List<FieldBinds> value = next.getValue().stream().filter(fieldBinds -> fieldBinds.isMybatis() == bool).collect(Collectors.toList());
                        if(Tools.isNotNull(value)){
                            Interceptor bean = SpringUtils.getBean(key);
                            List<Object> list = new CopyOnWriteArrayList<>();
                            result.parallelStream().forEach(o ->{
                                MethodAccess methodAccess = ASMUtils.methodAccessFactory(o);
                                value.parallelStream().forEach(v ->{
                                    Integer get = v.getGet();
                                    Object invoke = methodAccess.invoke(o, get);
                                    list.add(invoke);
                                });
                            });
                            List<Object> executionList = list.stream().distinct().collect(Collectors.toList());
                            if(Tools.isNotNull(executionList)){
                                Map execution = bean.execution(executionList);
                                if(isMapNotNull(execution)){
                                    result.parallelStream().forEach(o ->{
                                        MethodAccess methodAccess = ASMUtils.methodAccessFactory(o);
                                        value.parallelStream().forEach(v ->{
                                            Integer get = v.getGet();
                                            Integer set = v.getSet();
                                            Object invoke = methodAccess.invoke(o, get);
                                            Object o1 = execution.get(invoke);
                                            if(null != o1) {
                                                try {
                                                    methodAccess.invoke(o, set, o1);
                                                } catch (Exception e) {
                                                    log.severe("match 'name index set：" + set + ",get " + o1 + "' does not exist,From this " + className);
                                                }
                                            }
                                        });
                                    });
                                }
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

    private static boolean isExecutedBind(Class<?> clazz){
        Boolean bool = executedBindCache.get(clazz);
        if(null == bool){
            bool = clazz.getClass().isAnnotationPresent(ExecutedBind.class);
            executedBindCache.put(clazz,bool);
        }
        return bool;
    }

    public static boolean instanceofType(String className){
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

    private static Map<String, FieldBinds> fieldBindMap(String className,Class<?> clazz){
        Map<String, FieldBinds> fieldBindMap = null;
        if(null == (fieldBindMap = fieldCache.get(className))){
            fieldBindMap = new HashMap<>();
            Field[] fields = ASMUtils.getAllFields(clazz);
            Map<String,Field> map = new HashMap<>();
            for (Field field : fields){ map.put(field.getName(),field); }
            for (Field field : fields) {
                FieldBind fieldBind = field.getAnnotation(FieldBind.class);
                if (null == fieldBind)
                    continue;
                String name = field.getName();
                String column = fieldBind.column();
                if(map.containsKey(column)){
                    Map<String, Integer> methodIndexOfSet = ASMUtils.methodIndexOfSet.get(clazz);
                    Map<String, Integer> methodIndexOfGet = ASMUtils.methodIndexOfGet.get(clazz);
                    FieldBinds fieldBinds = new FieldBinds(name, fieldBind.mybatis(), column, fieldBind.interceptor(),
                            methodIndexOfSet.get(ASMUtils.captureName(name)), methodIndexOfGet.get(ASMUtils.captureName(column)));
                    fieldBindMap.put(field.getName(),fieldBinds);
                }else{
                    log.severe("FieldInterceptor Configuration property. '"+name+":"+column+"' does not exist,From this "+className);
                }
            }
            fieldCache.put(className,fieldBindMap);
        }
        return fieldBindMap;
    }


    public static Interceptor get(){
        return threadLocal.get();
    }

    private static boolean isMapNotNull(Map map){
        return null != map && Tools.zero < map.size();
    }
}
