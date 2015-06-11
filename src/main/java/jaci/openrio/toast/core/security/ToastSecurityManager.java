package jaci.openrio.toast.core.security;

import jaci.openrio.toast.core.ToastBootstrap;

import java.io.FilePermission;
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

    public static void init() {
        if (SecurityPolicy.get() != SecurityPolicy.NONE) {
            INSTANCE = new ToastSecurityManager();
            System.setSecurityManager(INSTANCE);
        }
    }

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

    /** FILE PERMISSIONS **/
    /* Disclaimer: By default, we block access to any files outside of the Toast Home directory (/home/lvuser/toast). This is
     * so modules don't maliciously delete, write or execute anything that may be sensitive to users. Keep in mind, this is only
     * for delete, write and execute permissions, and reading is still permitted. This is changed at the user's choice in the config */

    private Pattern fileDeniedAction = Pattern.compile(".*(delete|execute|write).*");
    private Pattern exceptionFiles = Pattern.compile(".*(AppData\\\\Local\\\\Temp\\\\).*");
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
    public void h_Socket(SocketPermission perm) {
        if (perm.getActions().contains("listen")) {
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
    private boolean portExceptionCheck(SocketPermission permission, String p) {
        SocketPermission impliee = new SocketPermission("*:" + p, "listen");
        return impliee.implies(permission);
    }
    private boolean isPortException(SocketPermission perm) {
        for (String p : exceptionPorts) if (portExceptionCheck(perm, p)) return true;
        return false;
    }

}
