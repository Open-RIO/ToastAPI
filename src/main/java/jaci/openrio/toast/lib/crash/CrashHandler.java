package jaci.openrio.toast.lib.crash;

import jaci.openrio.toast.lib.log.SplitStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    static ArrayList<CrashInfoProvider> providers;
    static File crashDir;
    static DateFormat dateFormat;

    public static void init() {
        try {
            providers = new ArrayList<CrashInfoProvider>();
            crashDir = new File("toast/crash");
            crashDir.mkdirs();
        } catch (Exception e) {}
        dateFormat = new SimpleDateFormat("dd/MM/yy-hh:mm:ss");
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler());
    }

    public static void registerProvider(CrashInfoProvider prov) {
        providers.add(prov);
    }

    public static void handle(Throwable t) {
        try {
            File file = new File(crashDir, "crash-log-" + dateFormat.format(new Date()));
            SplitStream split = new SplitStream(System.err, new FileOutputStream(file));
            PrintStream out = new PrintStream(split);

            out.println("**** CRASH LOG ****");
            out.println("Your robot has crashed. Following is a crash log and more details.");
            out.println("This log has been saved to: " + file.getCanonicalPath());
            out.println();
            t.printStackTrace(out);
            out.println();
            out.println("Crash Information: ");
            for (CrashInfoProvider provider : providers) {
                out.println("\t" + provider.getName() + ": ");
                out.println("\t" + provider.getCrashInfo(t));
            }
            out.println();
            out.println("*******************");

            out.close();

            //TODO: Shutdown safely
        } catch (Exception e) {}
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        handle(e);
    }
}
