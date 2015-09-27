package jaci.openrio.toast.lib.util;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

/**
 * Miscellaneous utilities used by Toast and modules. These Utilities provide common shorthand and utilities
 * for a range of purposes.
 *
 * @author Jaci
 */
public class ToastUtil {

    /**
     * Checks if a given object is included in an Array of objects.
     *
     * @return True if 'target' is an element of 'arr'
     */
    public static boolean contains(Object[] arr, Object target) {
        for (Object a : arr)
            if (a.equals(target)) return true;
        return false;
    }

    /**
     * Will find the greatest-common package(s) of an array of packages or class names.
     * This checks from the root package and works upwards, i.e. 'jaci.openrio.test' and 'jaci.openrio.anothertest'
     * would yield 'jaci.openrio'.
     *
     * When the root package differs in the array, multiple common packages are declared, for example, the package set:
     *  jaci.openrio.test
     *  jaci.openrio.anothertest
     *  something.your.test
     *  something.your.anothertest
     *
     * Will yield the return:
     *  jaci.openrio
     *  something.your
     *
     * This is used in module candidation to check what packages a module owns without keeping a list of all
     * it's classes which can become memory-inefficient for large included libraries such as Apache Commons, Guava or
     * JRuby.
     *
     * @param packages the list of packages to reduce
     * @return  The reduced list of common package(s)
     */
    public static List<String> findCommonPkgs(List<String> packages) {
        List<String[]> common = new Vector<String[]>();

        for (String pack : packages) {
            String[] spl = pack.split("\\.");
            boolean fnd = false;
            for (int i = 0; i < common.size(); i++) {
                String[] com = common.get(i);
                if (com[0].equals(spl[0])) {            // Root package is equal -> continue
                    common.set(i, Arrays.stream(spl)
                            .filter(b -> contains(com, b))
                            .toArray(String[]::new));   // Reduce the current common package to the greatest common package
                    fnd = true;
                }
            }
            if (!fnd) common.add(spl);                  // No root package found, treat as new common package
        }

        return common.stream()
                .map(b -> String.join(".", b))
                .collect(Collectors.toList());  // Convert the String[] values into a single, period delimited package id
    }


}
