package jaci.openrio.toast.core.script.js.proxy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileProxy extends File {

    public FileProxy(String pathname) {
        super(pathname);
    }

    public void withWriter(Object cb) { }
    public void withReader(Object cb) { }
    public String readFully() { return ""; }

    public byte[] readBytes() {
        try {
            return Files.readAllBytes(Paths.get(this.toString()));
        } catch (IOException e) { }
        return new byte[] {};
    }

}
