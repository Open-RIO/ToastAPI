package jaci.openrio.toast.core.script.js;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class JsonEngine {

    private static final Set<Class> WRAPPER_TYPES = new HashSet<Class>(Arrays.asList(Boolean.class, Character.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, Void.class, String.class));
    public static boolean isWrapperType(Class clazz) {
        return WRAPPER_TYPES.contains(clazz);
    }

    public static HashMap<String, Object> convert(Object o) throws IllegalAccessException, InvocationTargetException {
        HashMap<String, Object> map = new HashMap<>();
        Class clazz = o instanceof Class ? (Class) o : o.getClass();
        boolean staticOnly = o instanceof Class;
        for (Field f : clazz.getDeclaredFields()) {
            if (!f.isAccessible()) f.setAccessible(true);
            to(f, o, map, staticOnly);
        }
        for (Method m : clazz.getDeclaredMethods()) {
            if (!m.isAccessible()) m.setAccessible(true);
            to(m, o, map, staticOnly);
        }
        return map;
    }

    public static void to(Field field, Object o, HashMap<String, Object> map, boolean stat) throws IllegalAccessException, InvocationTargetException {
        if (!field.isAnnotationPresent(JSON.class)) return;
        JSON json = field.getDeclaredAnnotation(JSON.class);
        String name = json.name();
        if (name.equalsIgnoreCase("{DEFAULT}")) name = field.getName();

        if (stat && !Modifier.isStatic(field.getModifiers())) return;           //Make sure statics are only on classes
        if (!stat && Modifier.isStatic(field.getModifiers())) return;

        Object value = field.get(o);
        if (value.getClass().isPrimitive() || isWrapperType(value.getClass())) {
            map.put(name, value);
        } else {
            map.put(name, convert(value));
        }
    }

    public static void to(Method method, Object o, HashMap<String, Object> map, boolean stat) throws InvocationTargetException, IllegalAccessException {
        if (!method.isAnnotationPresent(JSON.Method.class)) return;
        JSON.Method json = method.getDeclaredAnnotation(JSON.Method.class);
        String name = json.name();
        if (name.equalsIgnoreCase("{DEFAULT}")) name = method.getName();

        if (stat && !Modifier.isStatic(method.getModifiers())) return;          //Make sure statics are only on classes
        if (!stat && Modifier.isStatic(method.getModifiers())) return;
        Object value = method.invoke(o);
        if (value.getClass().isPrimitive() || isWrapperType(value.getClass())) {
            map.put(name, value);
        } else {
            map.put(name, convert(value));
        }
    }

}
