package jaci.openrio.toast.lib.log;

import jaci.openrio.toast.core.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
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

    public static void init() {
        try {
            File fo = new File("toast/log");
            File fe = new File("toast/log");
            fo.mkdirs();
            fe.mkdirs();

            File fileO = new File(fo, "recent.txt");
            File fileE = new File(fe, "recentErr.txt");

            if (fileO.exists())
                fileO.delete();

            if (fileE.exists())
                fileE.delete();

            fileOut = new FileOutputStream(fileO);
            fileErr = new FileOutputStream(fileE);

            sysOut = System.out;
            sysErr = System.err;

            outStream = new PrintStream(new SplitStream(fileOut, sysOut));
            errStream = new PrintStream(new SplitStream(fileOut, sysErr, fileErr));

            System.setOut(outStream);
            System.setErr(errStream);
        } catch (Exception e) {
            Toast.log().warn("Could not split System Outputs to File");
            Toast.log().exception(e);
        }

    }

}
