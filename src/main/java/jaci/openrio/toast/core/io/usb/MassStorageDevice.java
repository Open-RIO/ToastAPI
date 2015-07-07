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
    public int config_priority;
    public int filesystem_priority;
    public boolean accept_logs;

    public MassStorageDevice(File path, ModuleConfig autorun, String name) {
        this.drivePath = path;
        this.config = autorun;
        this.drive_name = name;
        this.toast_directory = new File(path, autorun.getString("toast.directory", "toast"));
        this.dump_directory = new File(path, autorun.getString("toast.dumps", "toast_usb_dumps"));
        this.toast_directory.mkdirs();
        this.override_modules = autorun.getBoolean("toast.override_modules", false);
        this.concurrent_modules = autorun.getBoolean("toast.concurrent_modules", true);
        this.config_priority = autorun.getInt("toast.priority.config", 0);
        this.filesystem_priority = autorun.getInt("toast.priority.filesystem", 0);
        this.accept_logs = autorun.getBoolean("toast.accept_logs", true);
    }

    /**
     * Convert the Mass Storage Device to a String, containing name and any other relevant details.
     */
    public String toString() {
        return "MassStorageDevice[name: " + drive_name + ", path: " + drivePath + "]";
    }

}
