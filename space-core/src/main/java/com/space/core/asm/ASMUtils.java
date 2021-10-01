package com.space.core.asm;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.space.core.bean.Tool;

/**
 * @author xulinglin
 */
public class ASMUtils {

    private static Map<Class<?>, MethodAccess> methodMap = new ConcurrentHashMap<Class<?>, MethodAccess>();
    public static Map<Class<?>, Map<String, Integer>> methodIndexOfGet = new ConcurrentHashMap<>();
    public static Map<Class<?>, Map<String, Integer>> methodIndexOfSet = new ConcurrentHashMap<>();
    private static Map<Class<?>, Map<String, String>> methodIndexOfType = new ConcurrentHashMap<>();
    private static Map<String, String> nameCache = new ConcurrentHashMap<>();

    /**
     * @param dest 目标类
     * @param orgi 原始类
     */
    public static void copyProperties(Object dest, Object orgi) {
        MethodAccess destMethodAccess = methodAccessFactory(dest);
        MethodAccess orgiMethodAccess = methodAccessFactory(orgi);
        Map<String, Integer> get = methodIndexOfGet.get(orgi.getClass());
        Map<String, Integer> set = methodIndexOfSet.get(dest.getClass());
        Map<String, String> oritypemap = methodIndexOfType.get(orgi.getClass());
        Map<String, String> desctypemap = methodIndexOfType.get(dest.getClass());

        List<String> sameField = null;
        if (get.size() < set.size()) {
            sameField = new ArrayList<>(get.keySet());
            sameField.retainAll(set.keySet());
        } else {
            sameField = new ArrayList<>(set.keySet());
            sameField.retainAll(get.keySet());
        }

        for (String field : sameField) {
            Integer setIndex = set.get(field);
            Integer getIndex = get.get(field);
            String oritype = oritypemap.get(field);
            String desttype = desctypemap.get(field);
            Object value = orgiMethodAccess.invoke(orgi, getIndex);

            if (oritype.equalsIgnoreCase(desttype)) {
                if (value == null) {
                    continue;
                }

                destMethodAccess.invoke(dest, setIndex, value);
            }
        }
    }

    // double check
    public static MethodAccess methodAccessFactory(Object obj) {
        MethodAccess descMethodAccess = methodMap.get(obj.getClass());
        if (descMethodAccess == null) {
            synchronized (obj.getClass()) {
                descMethodAccess = methodMap.get(obj.getClass());
                if (descMethodAccess != null) {
                    return descMethodAccess;
                }
                Class<?> c = obj.getClass();
                MethodAccess methodAccess = MethodAccess.get(c);
                Field[] fields = getAllFields(c);
                Map<String, Integer> indexofget = new HashMap<>();
                Map<String, Integer> indexofset = new HashMap<>();
                Map<String, String> indexoftype = new HashMap<>();
                for (Field field : fields) {
                    if (Modifier.isPrivate(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) { // 私有非静态
                        String name = field.getName();
                        String fieldName = captureName(name); // 获取属性名称
                        try {
                            int getIndex = methodAccess.getIndex("get" + fieldName); // 获取get方法的下标
                            indexofget.put(fieldName, getIndex);
                        } catch (java.lang.IllegalArgumentException ex) {
                            try {
                                // 布尔型属性
                                int getIndex = methodAccess.getIndex("is" + fieldName);
                                indexofget.put(fieldName, getIndex);
                            }catch (Exception e){
                                continue;
                            }
                        }

                        int setIndex = methodAccess.getIndex("set" + fieldName); // 获取set方法的下标
                        indexofset.put(fieldName, setIndex);
                        indexoftype.put(fieldName, field.getType().getName());
                    }
                }
                methodIndexOfGet.put(c, indexofget);
                methodIndexOfSet.put(c, indexofset);
                methodIndexOfType.put(c, indexoftype);
                methodMap.put(c, methodAccess);
                return methodAccess;
            }
        }
        return descMethodAccess;
    }

    private static Field[] getAllFields(final Class<?> cls) {
        final List<Field> allFieldsList = getAllFieldsList(cls);
        return allFieldsList.toArray(new Field[allFieldsList.size()]);
    }

    private static List<Field> getAllFieldsList(final Class<?> cls) {
        final List<Field> allFields = new ArrayList<Field>();
        Class<?> currentClass = cls;
        while (currentClass != null) {
            final Field[] declaredFields = currentClass.getDeclaredFields();
            for (final Field field : declaredFields) {
                allFields.add(field);
            }
            currentClass = currentClass.getSuperclass();
        }
        return allFields;
    }

    public static String captureName(String name) {
        String value = nameCache.get(name);
        if(Tool.isNotBlank(value)){
            return value;
        }
        char[] cs = name.toCharArray();
        cs[0] -= 32;
        String name1 = String.valueOf(cs);
        nameCache.put(name,name1);
        return name1;
    }
}