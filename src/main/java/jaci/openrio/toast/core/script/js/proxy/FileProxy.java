package jaci.openrio.toast.core.script.js.proxy;

import java.io.File;

public class FileProxy extends File {

    public FileProxy(String pathname) {
        super(pathname);
    }

    public void withWriter(Object cb) { }
    public void withReader(Object cb) { }

}
