package jaci.openrio.toast.lib.crash;

public interface CrashInfoProvider {

    public String getName();

    public String getCrashInfo(Throwable t);

}
