package jaci.openrio.toast.core.loader;

import jaci.openrio.toast.core.loader.annotation.Priority;
import jaci.openrio.toast.lib.module.ToastModule;
import jaci.openrio.toast.lib.profiler.ProfilerSection;

import java.lang.reflect.InvocationTargetException;
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

    /**
     * Add an object to the MethodExecutor. This means the Object's methods will be triggered
     * when the MethodExecutor is called.
     */
    public void addObject(Object obj) {
        objects.add(obj);
        changed = true;
    }

    /**
     * Call the specified method on all listening objects. This is assuming the object takes no Arguments for the
     * method defined.
     * @param method    The name of the method to execute.
     */
    public void call(String method) throws InvocationTargetException {
        call(method, new Class[0]);
    }

    ProfilerSection section;

    /**
     * Set the next Profiler Section. This is for Robot Loading dispatch
     */
    public void profile(ProfilerSection section) {
        this.section = section;
    }

    /**
     * Parse the callstack from the registered objects. This caches the callstack so reflection doesn't look up the
     * Object's definition each time.
     */
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

    /**
     * Call the specified method on all listening objects.
     * @param method    The name of the method to invoke and execute
     * @param argTypes  The types of args the method uses.
     * @param args      The arg values to pass to the newly invoked method
     */
    public void call(String method, Class[] argTypes, Object... args) throws InvocationTargetException {
        MethodIdentifier id = parse(method, argTypes);
        for (MethodContainer container : callStacks.get(id)) {
            ToastModule mod = null;
            if (section != null && container.obj instanceof ToastModule) {
                mod = (ToastModule) container.obj;
                section.section(mod.getModuleName()).start(method);
            }
            try {
                container.method.invoke(container.obj);
            } catch (IllegalAccessException e) { }
            if (mod != null)
                section.section(mod.getModuleName()).stop(method);
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

        /**
         * Compare the priority of this method to another
         */
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

        /**
         * Returns true if the two Method Identifiers match each other, keeping in mind method name
         * and argument types.
         */
        public boolean equals(MethodIdentifier o) {
            return o.method.equals(method) && Arrays.equals(args, o.args);
        }

    }

}
