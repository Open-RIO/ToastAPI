package jaci.openrio.toast.core.io.usb;

import jaci.openrio.toast.core.ToastBootstrap;
import jaci.openrio.toast.lib.log.Logger;
import jaci.openrio.toast.lib.module.ModuleConfig;

import java.io.File;
import java.util.List;
import java.util.Vector;

/**
 * The class responsible for managing attached Mass Storage devices. This class will
 * make sure the drives are reachable at Runtime, although this state may change during
 * robot function. It's your responsibility to handle any exceptions found during
 * access of this drive, as the connection state may be volatile.
 *
 * @author Jaci
 */
public class USBMassStorage {

    private static String[] knownSymLinks = new String[] {"/U", "/V", "/W", "/X"};

    public static List<MassStorageDevice> connectedDevices;
    public static List<File> invalidDrives;

    static Logger logger;

    static boolean override = false;

    /**
     * Initializes the Logger, File System and required values. This is called in the ToastBootstrap, do
     * not touch this method yourself.
     */
    public static void init() {
        connectedDevices = new Vector<>();
        invalidDrives = new Vector<>();
        if (ToastBootstrap.isSimulation)
            knownSymLinks = new String[] {"usb_U", "usb_V", "usb_W", "usb_X"};
        logger = new Logger("Toast|Mass-Storage", Logger.ATTR_DEFAULT);
        for (String s : knownSymLinks) {
            crawlSym(new File(s));
        }
    }

    /**
     * Crawls the given directory for the toast_autorun.conf file. If the file is found, the USB storage device
     * is configured using it. If it is not found, the user is warned and the device marked as 'Invalid'. Running
     * 'usb generate' will validate any invalid drives by generating this file. A restart will be required after runnning
     * the generate command.
     */
    public static void crawlSym(File file) {
        if (!file.exists())
            return;
        try {
            File canon = file.getCanonicalFile();
            if (!canon.exists()) return;

            File configuration = new File(canon, "toast_autorun.conf");
            if (!configuration.exists()) {
                invalidDrives.add(canon);
                logger.warn("Invalid USB Mass Storage Device Detected -- No toast_autorun.conf file: " + canon);
                logger.warn("Run 'usb generate' to create a new toast_autorun.conf file on any invalid drives");
                return;
            }

            ModuleConfig pref = new ModuleConfig(configuration);
            String drive_name = pref.getString("toast.device_name", "Team_####_USB_Device").replace(" ", "_");
            MassStorageDevice device = new MassStorageDevice(canon, pref, drive_name);
            connectedDevices.add(device);
            if (device.override_modules && !device.concurrent_modules) override = true;
            logger.info("USB Mass Storage Device Detected -- Valid! " + canon + " (" + device.drive_name + ")");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns true if a file path is inside of a USB Mass Storage device.
     */
    public static boolean isUSB(String filepath) {
        for (String s : knownSymLinks) {
            if (filepath.startsWith(new File(s).getAbsolutePath())) return true;
        }
        return false;
    }

    /**
     * Returns true if ANY drives are marked to override modules or files on the Toast Local System. Only 1 drive is required
     * to do the overriding procedure, so make sure to check this method if using any File System components where a USB
     * Drive is capable of accessing.
     */
    public static boolean overridingModules() {
        return override;
    }

}
