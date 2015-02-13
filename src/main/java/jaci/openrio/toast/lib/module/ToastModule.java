package jaci.openrio.toast.lib.module;

public abstract class ToastModule {

    public abstract String getModuleName();
    public abstract String getModuleVersion();

    public abstract void prestart();
    public abstract void start();

    public void onConstruct() { }
}
