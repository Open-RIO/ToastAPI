package jaci.openrio.toast.lib.module;

public abstract class ToastModule implements Comparable<ToastModule> {

    public abstract String getModuleName();
    public abstract String getModuleVersion();

    public String[] dependsOn() {
        return new String[0];
    }

    public abstract void prestart();
    public abstract void start();

    public int compareTo(ToastModule o) {
        String modName = o.getModuleName();
        String modVers = o.getModuleVersion();

        for (String depend : dependsOn()) {
            if (depend.contains("@")) {
                String[] split = depend.split("@");
                if (modName.equalsIgnoreCase(split[0]) && modVers.equalsIgnoreCase(split[1])) {
                    return -1;
                }
            } else {
                if (modName.equalsIgnoreCase(depend)) {
                    return -1;
                }
            }
        }

        return 0;
    }

}
