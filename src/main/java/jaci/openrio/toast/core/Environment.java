package jaci.openrio.toast.core;

public class Environment {

    public static boolean isVerification() {
        return ToastBootstrap.isVerification;
    }

    public static boolean isSimulation() {
        return ToastBootstrap.isSimulation;
    }

    public static boolean isEmbedded() {
        return !isSimulation();
    }

    public static boolean isCompetition() {
        return Toast.getToast().station().isFMSAttached();
    }

    public static String getEnvironmentalType() {
        if (isVerification())
            return "Verification";
        else if (isSimulation())
            return "Simulation";
        else if (isEmbedded())
            return "Embedded";
        return "Unknown";
    }

    public static String getOS_Architecture() {
        return System.getProperty("os.arch");
    }

    public static String getOS_Name() {
        return System.getProperty("os.name");
    }

    public static String getOS_Version() {
        return System.getProperty("os.version");
    }

    public static String getJava_vendor() {
        return System.getProperty("java.vendor");
    }

    public static String getJava_version() {
        return System.getProperty("java.version");
    }

    public static String getJava_home() {
        return System.getProperty("java.home");
    }

}
