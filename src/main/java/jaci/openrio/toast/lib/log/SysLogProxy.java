package jaci.openrio.toast.lib.log;

import jaci.openrio.toast.core.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

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

            fo.createNewFile();
            fe.createNewFile();

            fileOut = new FileOutputStream(fo + "/recent.txt");
            fileErr = new FileOutputStream(fe + "/recentErr.txt");

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
