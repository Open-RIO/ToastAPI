package jaci.openrio.toast.core.security;

import jaci.openrio.toast.core.ToastBootstrap;

import java.io.FilePermission;
import java.lang.reflect.ReflectPermission;
import java.net.SocketPermission;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Permission;
import java.util.regex.Pattern;

/**
 * The Toast Security Manager. The aim of this class is to notify Users when a Module is attempting to access something that
 * is not recommended. "Not Recommended" means things like deleting/writing/executing files that are not present in the
 * toast/ directory defined by the {@link ToastBootstrap}. Users can define in the Configuration file if they want to be
 * Loose, Strict or just not take notice of these kind of non-recommended behaviour. Loose will only warn the user in the
 * console without blocking the action, Strict will block the action completely, and None will not warn you at all.
 *
 * @author Jaci
 */
public class ToastSecurityManager extends SecurityManager {

    public static ToastSecurityManager INSTANCE;
    private static ThreadGroup group;
    private static ThreadGroup main;

    /**
     * Initialize the security manager. This is done by Toast during runtime and should NEVER be called by a module.
     */
    public static void init() {
        if (SecurityPolicy.get() != SecurityPolicy.NONE) {
            INSTANCE = new ToastSecurityManager();
            System.setSecurityManager(INSTANCE);
            main = Thread.currentThread().getThreadGroup();
            group = new ThreadGroup(Thread.currentThread().getThreadGroup(), "Toasted");
        }
    }

    /**
     * Check a permission. This performs the check statement for the permission type and handles it accordingly
     */
    @Override
    public void checkPermission(Permission perm) {
        if (perm instanceof FilePermission) {
            FilePermission fp = (FilePermission) perm;
            h_File(fp);
        } else if (perm instanceof SocketPermission) {
            SocketPermission sp = (SocketPermission) perm;
            h_Socket(sp);
        }
    }

    /**
     * Returns the location of a given class or package in a callstack, or -1 if it is not present.
     */
    public int existsInCallStack(String packageorclass) {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        for (int i = 0; i < elements.length; i++) {
            if (elements[i].getClassName().contains(packageorclass))
                return i;
        }
        return -1;
    }

    /** FILE PERMISSIONS **/
    /* Disclaimer: By default, we block access to any files outside of the Toast Home directory (/home/lvuser/toast). This is
     * so modules don't maliciously delete, write or execute anything that may be sensitive to users. Keep in mind, this is only
     * for delete, write and execute permissions, and reading is still permitted. This is changed at the user's choice in the config */

    private Pattern fileDeniedAction = Pattern.compile(".*(delete|execute|write).*");
    private Pattern exceptionFiles = Pattern.compile(".*(AppData\\\\Local\\\\Temp\\\\).*");
    /**
     * Handle the given FilePermission, throwing an Exception if it is denied access or logging a warning if required.
     */
    public void h_File(FilePermission perm) {
        String path = perm.getName();
        if (fileDeniedAction.matcher(perm.getActions()).matches()) {
            try {
                Path p = Paths.get(path);
                if (!p.toAbsolutePath().startsWith(ToastBootstrap.toastHome.getAbsoluteFile().toPath()) && !isFileException(p.toAbsolutePath().toString())) {
                    SecurityPolicy.get().trigger(perm, "File Operation Violation, outside of Toast Scope: " + perm.getName());
                }
            } catch (InvalidPathException e) {
                if (!path.startsWith(ToastBootstrap.toastHome.getAbsolutePath()) && !isFileException(path)) {
                    SecurityPolicy.get().trigger(perm, "File Operation Violation, outside of Toast Scope: " + perm.getName());
                }
            }
        }
    }
    /**
     * Returns true if the file is 'excused' from the FilePermission. This is for things like TempFiles or other exceptions
     * outside of the toast/ directory.
     */
    private boolean isFileException(String filepath) {
        String tmpdir = System.getProperty("java.io.tmpdir");
        if (tmpdir != null && filepath.startsWith(tmpdir)) return true;
        return exceptionFiles.matcher(filepath).matches();
    }

    /** SOCKET PERMISSIONS **/
    /* Disclaimer: We don't block the sockets, we just warn the user when it's not on a port that FMS can forward, so they don't pull their hair out at competition
     * when their sockets perms are disallowed */

    private String[] exceptionPorts = new String[] {
            "1735",   //NetworkTables
    };
    /**
     * Handles the given SocketPermission. This serves to warn the user if a socket is using a port that FMS cannot forward, but will never
     * block a socket.
     */
    public void h_Socket(SocketPermission perm) {
        if (Thread.currentThread().getThreadGroup().equals(main) && !isPortException(perm)) {
            throw new ThreadingException("Networking On Main Thread!");
        }
        if (perm.getActions().contains("listen")) {
            if (existsInCallStack("sun.management.jmxremote") != -1) return;            //RMI Profiler Routing

            SocketPermission impliee = new SocketPermission("*:5800-5810", "listen");
            if (!impliee.implies(perm) && !isPortException(perm)) {
                switch (SecurityPolicy.get()) {
                    case NONE:
                        break;
                    default:
                        SecurityPolicy.log().warn("Socket Port Warning, not within 5800...5810 (FMS will not forward it!): " + perm.getName());
                        SecurityPolicy.log().exception(new Throwable());
                        break;
                }
            }
        }
    }

    /**
     * Checks if the port is an exception to the 5800-5810 rule (such as NetworkTables)
     */
    private boolean portExceptionCheck(SocketPermission permission, String p) {
        SocketPermission impliee = new SocketPermission("*:" + p, "listen");
        return impliee.implies(permission);
    }
    /**
     * Does the {@link #portExceptionCheck(SocketPermission, String)} on all exceptionPorts listed in the array.
     */
    private boolean isPortException(SocketPermission perm) {
        for (String p : exceptionPorts) if (portExceptionCheck(perm, p)) return true;
        return false;
    }

    /**
     * Make sure new Threads are created on the ToastThreadPool group, to ensure
     * that Networking and other actions don't occur within the main Thread Group.
     */
    public ThreadGroup getThreadGroup() {
        return group;
    }

    /**
     * Thrown when code violates a rule regarding Multithreading. For example, Networking SHOULD NOT be done on the
     * main Thread, but instead delegated elsewhere. If networking does occur on the main Thread, this exception
     * is thrown and the robot program stopped.
     */
    public static class ThreadingException extends RuntimeException {
        public ThreadingException(String s) {
            super(s);
        }
    }

}
