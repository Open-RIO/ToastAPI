package jaci.openrio.toast.core.loader;

import jaci.openrio.toast.lib.module.ToastModule;

public class ToastModuleContainer {

    Class<? extends ToastModule> moduleClass;
    ToastModule moduleInstance;

    String name = "{undefined}";
    String version = "{undefined}";

    public ToastModuleContainer(Class<? extends ToastModule> moduleClass) {
        this.moduleClass = moduleClass;
    }

    public void construct() throws IllegalAccessException, InstantiationException {
        moduleInstance = moduleClass.newInstance();
        name = moduleInstance.getModuleName();
        version = moduleInstance.getModuleVersion();
    }

    public String toString() {
        return String.format("ToastModuleContainer[name:%s, version:%s, class:%s]", name, version, moduleClass);
    }

}
