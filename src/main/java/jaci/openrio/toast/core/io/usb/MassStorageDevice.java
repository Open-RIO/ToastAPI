package jaci.openrio.toast.core.io.usb;

import jaci.openrio.toast.core.loader.groovy.GroovyPreferences;

import java.io.File;

/**
 * A container object for a Mass Storage Device (External HDD, Flash Drive, Pen Drive). This holds
 * the absolute, non sym-link directory as well as configuration settings for the autorun file. These should
 * ONLY contain valid Mass Storage Devices.
 *
 * @author Jaci
 */
public class MassStorageDevice {

    /**
     * The absolute directory of the Mass Storage Device
     */
    public File drivePath;

    /**
     * The GroovyPreferences file for the Drive. This is the toast_autorun.conf file required in the drive
     */
    public GroovyPreferences preferences;

    /**
     * The drive name. This is human-friendly and also the ID for the drive
     */
    public String drive_name;

    /**
     * The directory that Toast files should be stored in on the drive
     */
    public File toast_directory;
    public boolean override_modules;
    public boolean concurrent_modules;

    public MassStorageDevice(File path, GroovyPreferences autorun, String name) {
        this.drivePath = path;
        this.preferences = autorun;
        this.drive_name = name;
        this.toast_directory = new File(path, autorun.getString("toast.directory"));
        this.toast_directory.mkdirs();
        this.override_modules = autorun.getBoolean("toast.override_modules");
        this.concurrent_modules = autorun.getBoolean("toast.concurrent_modules");
    }

}
