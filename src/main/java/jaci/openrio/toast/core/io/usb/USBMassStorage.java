package jaci.openrio.toast.core.io.usb;

import jaci.openrio.toast.core.ToastBootstrap;
import jaci.openrio.toast.core.loader.groovy.GroovyLoader;
import jaci.openrio.toast.core.loader.groovy.GroovyPreferences;
import jaci.openrio.toast.lib.log.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

    public static void init() {
        connectedDevices = new ArrayList<>();
        invalidDrives = new ArrayList<>();
        if (ToastBootstrap.isSimulation)
            knownSymLinks = new String[] {"usb_U", "usb_V", "usb_W", "usb_X"};
        logger = new Logger("Toast|Mass-Storage", Logger.ATTR_DEFAULT);
        for (String s : knownSymLinks) {
            crawlSym(new File(s));
        }
    }

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

            GroovyPreferences pref = new GroovyPreferences(configuration);
            String drive_name = pref.getString("toast.device_name");
            MassStorageDevice device = new MassStorageDevice(canon, pref, drive_name);
            connectedDevices.add(device);
            if (device.override_modules && !device.concurrent_modules) override = true;
            logger.info("USB Mass Storage Device Detected -- Valid! " + canon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void load() {
        for (MassStorageDevice device : connectedDevices) {
            File groovy = new File(device.toast_directory, "groovy");
            groovy.mkdirs();

            GroovyLoader.search(groovy);
        }
    }

    public static boolean overridingModules() {
        return override;
    }

}
