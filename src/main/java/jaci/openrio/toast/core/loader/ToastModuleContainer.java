package jaci.openrio.toast.core.loader;

import jaci.openrio.toast.lib.module.IToastModule;

public class ToastModuleContainer {

    Class<? extends IToastModule> moduleClass;

    public ToastModuleContainer(Class<? extends IToastModule> moduleClass) {
        this.moduleClass = moduleClass;
    }

}
