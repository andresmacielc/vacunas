package com.app.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;

public class Util {

    public static Map<String, String[]> paramsToMap(
            MultivaluedMap<String, String> parameters) {
        Map<String, String[]> map = new HashMap<>();
        Set<String> keys = parameters.keySet();
        for (String key : keys) {
            List<String> list = parameters.get(key);
            String[] a = new String[]{};
            map.put(key, (String[]) list.toArray(a));
        }
        return map;
    }

    public static boolean isAssignableFrom(Class classTarget, Class classSource,
            String attribute) {
        boolean isAssignable = !getType(classTarget, attribute).
                isAssignableFrom(classSource);
        return isAssignable;
    }
    
    public static <T> T valueOf(Class<T> type, String value) {
        try {
            Constructor<T> constructor = type.getConstructor(String.class);
            return constructor.newInstance(value);
        } catch (NoSuchMethodException | SecurityException
                | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException ex) {
            return null;
        }
    }
    
    public static Class getType(Class bean, String attribute) {
        try {
            return bean.getDeclaredField(attribute).getType();
        } catch (NoSuchFieldException | SecurityException ex) {
            throw new RuntimeException(ex);
        }
    }
}
