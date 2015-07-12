package jaci.openrio.toast.core.io;

import jaci.openrio.toast.core.ToastBootstrap;
import jaci.openrio.toast.core.io.usb.MassStorageDevice;
import jaci.openrio.toast.core.io.usb.USBMassStorage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides shortcuts / "The Right Way To Handle The Filesystem". This handles switching between the values in the
 * toast_autorun.conf file to ensure everything is loaded in the correct way. This is done through a callback system,
 * or simply by returning an array of valid file paths. This should be used for ANY filesystem operations.
 *
 * @author Jaci
 */
public class Storage {

    /**
     * A callback-type interface for USB loading of modules. This listens to the concurrent and override module
     * properties in the toast_autorun.conf file.
     *
     * @param resource The resource on the USB drive or local FS. This does File concatenation for you, adding the resource to the root
     *                 Toast filepath of the drive or local file system. If you want the toast/ directory, a simple empty
     *                 string or "." will do. Do not make this null, you will crash and burn.
     */
    public static void USB_Module(String resource, StorageCallback cb) {
        if (!USBMassStorage.overridingModules()) {
            cb.call(new File(ToastBootstrap.toastHome, resource), false, null);
        }
        for (MassStorageDevice device : USBMassStorage.connectedDevices) {
            if (device.concurrent_modules || device.override_modules) {
                cb.call(new File(device.toast_directory, resource), true, device);
            }
        }
    }

    /**
     * Same as {@link #USB_Module(String, jaci.openrio.toast.core.io.Storage.StorageCallback)}, but instead returns an array
     * of valid files instead of a callback
     */
    public static File[] USB_Module(String resource) {
        List<File> fl = new ArrayList<>();

        if (!USBMassStorage.overridingModules()) {
            fl.add(new File(ToastBootstrap.toastHome, resource));
        }
        for (MassStorageDevice device : USBMassStorage.connectedDevices) {
            if (device.concurrent_modules || device.override_modules) {
                fl.add(new File(device.toast_directory, resource));
            }
        }
        return fl.toArray(new File[0]);
    }


    /**
     * Get the USB Drive with the highest filesystem priority. If no drives are plugged in (or have a negative value), the local
     * filesystem will be used instead. Use this for reading from files.
     *
     * @param resource The resource on the USB drive or local FS. This does File concatenation for you, adding the resource to the root
     *                 Toast filepath of the drive or local file system. If you want the toast/ directory, a simple empty
     *                 string or "." will do. Do not make this null, you will crash and burn.
     */
    public static void highestPriority(String resource, StorageCallback cb) {
        if (USBMassStorage.filesystem_highest == null) {
            cb.call(new File(ToastBootstrap.toastHome, resource), false, null);
        } else {
            cb.call(new File(USBMassStorage.filesystem_highest.toast_directory, resource), true, USBMassStorage.filesystem_highest);
        }
    }

    /**
     * Same as {@link #highestPriority(String, StorageCallback)}, but instead returns the File to
     * use instead of a callback.
     */
    public static File highestPriority(String resource) {
        return (USBMassStorage.filesystem_highest == null) ?
                new File(ToastBootstrap.toastHome, resource) :
                new File(USBMassStorage.filesystem_highest.toast_directory, resource);
    }

    /**
     * The callback interface to use for USB drive or local FS storage. This is called for each valid filesystem
     */
    public static interface StorageCallback {

        /**
         * Do your desired file operation on this filesystem.
         * @param file      The file to use. This is the filesystem's path concatenated with your resource.
         * @param isUSB     Is this filesystem a USB Mass Storage device? Returns false for local RoboRIO storage.
         * @param device    The USB Mass Storage device. This will be null for local storage.
         */
        public void call(File file, boolean isUSB, MassStorageDevice device);

    }

}
