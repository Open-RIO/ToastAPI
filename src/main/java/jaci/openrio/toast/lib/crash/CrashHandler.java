package jaci.openrio.toast.lib.crash;

import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.core.ToastBootstrap;
import jaci.openrio.toast.lib.log.SplitStream;
import jaci.openrio.toast.lib.log.SysLogProxy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Handles crashes when the robot encounters an uncaught-exception. This simply adds details to the Logger
 * and reports the StackTrace to a separate file under toast/crash/. Classes implementing
 * {@link jaci.openrio.toast.lib.crash.CrashInfoProvider} are able to add custom data to the log
 *
 * @author Jaci
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    static ArrayList<CrashInfoProvider> providers;
    static File crashDir;
    static DateFormat dateFormat;
    static CrashHandler instance;

    /**
     * Initialize the handler. This is handled by Toast.
     */
    public static void init() {
        try {
            providers = new ArrayList<CrashInfoProvider>();
            crashDir = new File(ToastBootstrap.toastHome, "crash");
            crashDir.mkdirs();
        } catch (Exception e) {}
        dateFormat = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss");
        instance = new CrashHandler();
        Thread.setDefaultUncaughtExceptionHandler(instance);
        Thread.currentThread().setUncaughtExceptionHandler(instance);

        providers.add(new CrashInfoToast());
    }

    /**
     * Register a provider for the Crash Handler
     */
    public static void registerProvider(CrashInfoProvider prov) {
        providers.add(prov);
    }

    /**
     * Handle an uncaught exception
     */
    public static void handle(Throwable t) {
        try {
            String fn = "crash-" + dateFormat.format(new Date());
            File file = new File(crashDir, fn + ".txt");
            SplitStream split = new SplitStream(System.err, new FileOutputStream(file));
            PrintStream out = new PrintStream(split);

            out.println("**** CRASH LOG ****");
            out.println("Your robot has crashed. Following is a crash log and more details.");
            out.println("This log has been saved to: " + file.getCanonicalPath());
            for (CrashInfoProvider provider : providers) {
                String info = provider.getCrashInfoPre(t);
                if (info != null)
                    out.println("\t" + provider.getCrashInfoPre(t) + "\n");
            }
            t.printStackTrace(out);
            out.println();
            out.println("Crash Information: ");
            for (CrashInfoProvider provider : providers) {
                out.println("\t" + provider.getName() + ": ");
                out.println("\t" + provider.getCrashInfo(t));
            }
            out.println();
            out.println("*******************");

            out.flush();
            out.close();

            File recentLog = SysLogProxy.recentOut;
            File cpFile = new File(crashDir, fn + "-FULL.txt");

            Files.copy(recentLog.toPath(), cpFile.toPath());

            Toast.getToast().shutdownSafely();
        } catch (Exception e) {
        }
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        handle(e);
    }
}
