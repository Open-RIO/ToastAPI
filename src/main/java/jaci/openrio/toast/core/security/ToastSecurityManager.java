package jaci.openrio.toast.core.security;

import jaci.openrio.toast.core.ToastBootstrap;

import java.io.FilePermission;
import java.nio.file.Files;
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
        }
    }

    /** FILE PERMISSIONS **/

    private Pattern fileDeniedAction = Pattern.compile(".*(delete|execute|write).*");
    private Pattern exceptionFiles = Pattern.compile(".*(AppData\\\\Local\\\\Temp\\\\).*");
    public void h_File(FilePermission perm) {
        String path = perm.getName();
        if (fileDeniedAction.matcher(perm.getActions()).matches()) {
            try {
                Path p = Paths.get(path);
                if (!p.toAbsolutePath().startsWith(ToastBootstrap.toastHome.getAbsoluteFile().toPath()) && !isException(p.toAbsolutePath().toString())) {
                    SecurityPolicy.get().trigger(perm, "File Operation Violation, outside of Toast Scope: " + perm.getName());
                }
            } catch (InvalidPathException e) {
                if (!path.startsWith(ToastBootstrap.toastHome.getAbsolutePath()) && !isException(path)) {
                    SecurityPolicy.get().trigger(perm, "File Operation Violation, outside of Toast Scope: " + perm.getName());
                }
            }
        }
    }
    private boolean isException(String filepath) {
        String tmpdir = System.getProperty("java.io.tmpdir");
        if (tmpdir != null && filepath.startsWith(tmpdir)) return true;
        return exceptionFiles.matcher(filepath).matches();
    }

}
