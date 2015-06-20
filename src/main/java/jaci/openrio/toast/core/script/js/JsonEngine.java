package jaci.openrio.toast.core.script.js;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * The JSONEngine is used to convert an Object with {@link JSON} annotations to a HashMap which can then be used in the
 * {@link JavaScript} class. Fields or Methods marked with the {@link JSON} annotation will be added to the HashMap and
 * then passed to JS.
 *
 * @author Jaci
 */
public class JsonEngine {

    private static final Set<Class> WRAPPER_TYPES = new HashSet<Class>(Arrays.asList(Boolean.class, Character.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, Void.class, String.class));

    /**
     * Returns true if a value is a Wrapper type (objectified primitive).
     */
    public static boolean isWrapperType(Class clazz) {
        return WRAPPER_TYPES.contains(clazz);
    }

    /**
     * Convert any object to a HashMap of Strings and Objects. This method will take any fields / methods with the
     * {@link JSON} annotation and use their value to insert into the HashMap. Any underlying HashMaps, Arrays, Primitives
     * or Objects will also be recursively added to the HashMap.
     */
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

    /**
     * Convert a single field to a HashMappable value and add it to the Map arg.
     */
    public static void to(Field field, Object o, HashMap<String, Object> map, boolean stat) throws IllegalAccessException, InvocationTargetException {
        if (!field.isAnnotationPresent(JSON.class)) return;
        JSON json = field.getDeclaredAnnotation(JSON.class);
        String name = json.name();
        if (name.equalsIgnoreCase("{DEFAULT}")) name = field.getName();

        if (stat && !Modifier.isStatic(field.getModifiers())) return;           //Make sure statics are only on classes
        if (!stat && Modifier.isStatic(field.getModifiers())) return;

        Object value = field.get(o);
        handleValue(value, name, map);
    }

    /**
     * Invoke and then Convert a single method to a HashMappable value and add it to the Map arg.
     */
    public static void to(Method method, Object o, HashMap<String, Object> map, boolean stat) throws InvocationTargetException, IllegalAccessException {
        if (!method.isAnnotationPresent(JSON.class)) return;
        JSON json = method.getDeclaredAnnotation(JSON.class);
        String name = json.name();
        if (name.equalsIgnoreCase("{DEFAULT}")) name = method.getName();

        if (stat && !Modifier.isStatic(method.getModifiers())) return;          //Make sure statics are only on classes
        if (!stat && Modifier.isStatic(method.getModifiers())) return;
        Object value = method.invoke(o);
        handleValue(value, name, map);
    }

    /**
     * Handle the addition of a value to a map by making sure it is recursively converted if required.
     */
    public static void handleValue(Object value, String name, HashMap<String, Object> map) throws InvocationTargetException, IllegalAccessException {
        if (value.getClass().isPrimitive() || isWrapperType(value.getClass()) || value.getClass().isArray() || List.class.isAssignableFrom(value.getClass())) {
            map.put(name, value);
        } else {
            map.put(name, convert(value));
        }
    }

}
