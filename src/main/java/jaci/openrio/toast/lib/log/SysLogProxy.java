package jaci.openrio.toast.lib.log;

import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.core.ToastBootstrap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * A utility class to attach system.out and system.err into {@link jaci.openrio.toast.lib.log.SplitStream} to
 * a file and to the standard streams
 *
 * @author Jaci
 */
public class SysLogProxy {

    static PrintStream sysOut;
    static PrintStream sysErr;

    public static FileOutputStream fileOut;
    public static FileOutputStream fileErr;
    public static PrintStream outStream;
    public static PrintStream errStream;
    public static File logDir;
    public static File recentOut;
    public static File oldOut;
    public static File recentErr;
    public static SplitStream master;
    public static SplitStream masterError;

    static boolean init = false;

    /**
     * Initialize the proxy if it has not already been started. This splits the System.out and System.err streams
     * to Files in the filesystem.
     */
    public static void init() {
        try {
            if (!init) {
                init = true;
                logDir = new File(ToastBootstrap.toastHome, "log");
                logDir.mkdirs();

                recentOut = new File(logDir, "recent.txt");
                recentErr = new File(logDir, "recentErr.txt");

                if (recentOut.exists()) {
                    oldOut = new File(logDir, "last_session.txt");
                    if (oldOut.exists()) oldOut.delete();
                    recentOut.renameTo(oldOut);
                    recentOut = new File(logDir, "recent.txt");
                    recentOut.delete();
                }

                if (recentErr.exists())
                    recentErr.delete();

                fileOut = new FileOutputStream(recentOut);
                fileErr = new FileOutputStream(recentErr);

                sysOut = System.out;
                sysErr = System.err;

                master = new SplitStream(sysOut, fileOut);
                outStream = new ColorPrint(master);
                masterError = new SplitStream(sysErr, fileOut, fileErr);
                errStream = new ColorPrint(masterError);

                System.setOut(outStream);
                System.setErr(errStream);
            }
        } catch (Exception e) {
            System.err.println("System Log Proxy failed...");
            e.printStackTrace();
        }

    }

}
