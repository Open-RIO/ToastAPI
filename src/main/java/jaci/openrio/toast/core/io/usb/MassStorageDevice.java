package jaci.openrio.toast.core.io.usb;

import jaci.openrio.toast.lib.module.ModuleConfig;

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
     * The ModuleConfig file for the Drive. This is the toast_autorun.conf file required in the drive
     */
    public ModuleConfig config;

    /**
     * The drive name. This is human-friendly and also the ID for the drive
     */
    public String drive_name;

    /**
     * The directory that Toast files should be stored in on the drive
     */
    public File toast_directory;

    /**
     * The directory that USB Dumps are kept in
     */
    public File dump_directory;
    public boolean override_modules;
    public boolean concurrent_modules;

    public MassStorageDevice(File path, ModuleConfig autorun, String name) {
        this.drivePath = path;
        this.config = autorun;
        this.drive_name = name;
        this.toast_directory = new File(path, autorun.getString("toast.directory", "toast"));
        this.dump_directory = new File(path, autorun.getString("toast.dumps", "toast_usb_dumps"));
        this.toast_directory.mkdirs();
        this.override_modules = autorun.getBoolean("toast.override_modules", false);
        this.concurrent_modules = autorun.getBoolean("toast.concurrent_modules", true);
    }

}
