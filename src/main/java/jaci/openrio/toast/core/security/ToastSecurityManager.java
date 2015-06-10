package jaci.openrio.toast.core.security;

import jaci.openrio.toast.core.ToastBootstrap;

import java.io.FilePermission;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Permission;
import java.util.regex.Pattern;

public class ToastSecurityManager extends SecurityManager {

    public static ToastSecurityManager INSTANCE;

    public static void init() {
        INSTANCE = new ToastSecurityManager();
        System.setSecurityManager(INSTANCE);
    }

    @Override
    public void checkPermission(Permission perm) {
        super.checkPermission(perm);
        if (perm instanceof FilePermission) {
            FilePermission fp = (FilePermission) perm;
            h_File(fp);
        }
    }

    private Pattern fileDeniedAction = Pattern.compile(".*(delete|execute|write).*");
    public void h_File(FilePermission perm) {
        String path = perm.getName();
        if (fileDeniedAction.matcher(perm.getActions()).matches()) {
            Path p = Paths.get(path);
            if (!p.startsWith(ToastBootstrap.toastHome.toPath())) {
                SecurityPolicy.get().trigger(perm, "File Operation Denied, outside of Toast Scope: " + perm.getName());
            }
        }
    }

}
