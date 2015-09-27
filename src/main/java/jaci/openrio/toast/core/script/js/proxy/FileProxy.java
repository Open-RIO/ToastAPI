package jaci.openrio.toast.core.script.js.proxy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * A proxy class for the {@link java.io.File} class into JavaScript. This provides methods relating to reading and writing
 * to the file without creating a new reader/writer instance.
 *
 * @author Jaci
 */
public class FileProxy extends File {

    /**
     * Create a new FileProxy object, with the given Filename.
     */
    public FileProxy(String pathname) {
        super(pathname);
    }

    /**
     * Call the callback with the writer object as an argument
     */
    public void withWriter(Object cb) { }

    /**
     * Call the callback with the reader object as an argument
     */
    public void withReader(Object cb) { }

    /**
     * Read the file fully and return the contents.
     */
    public String readFully() { return ""; }

    /**
     * Read the file fully and return the contents as a byte array.
     */
    public byte[] readBytes() {
        try {
            return Files.readAllBytes(Paths.get(this.toString()));
        } catch (IOException e) { }
        return new byte[] {};
    }

}
