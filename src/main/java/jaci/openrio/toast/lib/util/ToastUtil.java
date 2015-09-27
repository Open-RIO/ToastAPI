package jaci.openrio.toast.lib.util;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

public class ToastUtil {

    public static boolean contains(String[] arr, String target) {
        for (String a : arr)
            if (a.equals(target)) return true;
        return false;
    }

    public static List<String> findCommonPkgs(List<String> packages) {
        List<String[]> common = new Vector<String[]>();
        common.add(packages.get(0).split("\\."));

        for (String pack : packages) {
            String[] spl = pack.split("\\.");
            boolean fnd = false;
            for (int i = 0; i < common.size(); i++) {
                String[] com = common.get(i);
                if (com[0].equals(spl[0])) {
                    common.set(i, Arrays.stream(spl)
                            .filter(b -> contains(com, b))
                            .toArray(String[]::new));
                    fnd = true;
                }
            }
            if (!fnd) common.add(spl);
        }

        return common.stream()
                .map(b -> String.join(".", b)).collect(Collectors.toList());
    }


}
