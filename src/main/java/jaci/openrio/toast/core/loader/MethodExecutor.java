package jaci.openrio.toast.core.loader;

import jaci.openrio.toast.core.loader.annotation.Priority;

import java.lang.reflect.Method;
import java.util.*;

/**
 * The execution service that will execute methods with annotations based on the annotation values.
 *
 * @author Jaci
 */
public class MethodExecutor {

    List<Object> objects;
    HashMap<MethodIdentifier, List<MethodContainer>> callStacks;

    boolean changed = false;
    public MethodExecutor(Object... objects) {
        this.objects = Arrays.asList(objects);
        callStacks = new HashMap<>();
    }

    public void addObject(Object obj) {
        objects.add(obj);
        changed = true;
    }

    public void call(String method) {
        call(method, new Class[0]);
    }

    MethodIdentifier parse(String method, Class[] argTypes) {
        MethodIdentifier id = new MethodIdentifier(method, argTypes);
        if (changed) {
            callStacks.clear();
            changed = false;
        }
        if (!callStacks.containsKey(id)) {
            Vector<MethodContainer> callStack = new Vector<>();
            for (Object o : objects) {
                try {
                    if (o == null) continue;
                    Method m = o.getClass().getMethod(method, argTypes);
                    if (m != null) {
                        Priority[] annotations = m.getAnnotationsByType(Priority.class);
                        double pri;
                        if (annotations != null && annotations.length > 0) {
                            Priority priority = annotations[0];
                            pri = priority.level();
                        } else {
                            pri = 0;
                        }
                        callStack.add(new MethodContainer(o, m, pri));
                    }
                } catch (NoSuchMethodException e) {
                }
            }

            Collections.sort(callStack);
            callStacks.put(id, callStack);
        }
        return id;
    }

    public void call(String method, Class[] argTypes, Object... args) {
        MethodIdentifier id = parse(method, argTypes);
        for (MethodContainer container : callStacks.get(id)) {
            try {
                container.method.invoke(container.obj);
            } catch (Exception e) {
            }
        }
    }

    public static class MethodContainer implements Comparable<MethodContainer> {

        Method method;
        Object obj;
        double priority;

        public MethodContainer(Object obj, Method m, double priority) {
            this.priority = priority;
            this.method = m;
            this.obj = obj;
        }

        @Override
        public int compareTo(MethodContainer o) {
            return priority > o.priority ? -1 : priority == o.priority ? 0 : 1;
        }

    }

    public static class MethodIdentifier {

        Class[] args;
        String method;

        public MethodIdentifier(String m, Class... args) {
            this.method = m;
            this.args = args;
        }

        public boolean equals(MethodIdentifier o) {
            return o.method.equals(method) && Arrays.equals(args, o.args);
        }

    }

}
