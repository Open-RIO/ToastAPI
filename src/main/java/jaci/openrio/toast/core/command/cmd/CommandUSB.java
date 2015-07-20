package jaci.openrio.toast.core.command.cmd;

import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.core.ToastBootstrap;
import jaci.openrio.toast.core.command.AbstractCommand;
import jaci.openrio.toast.core.command.IHelpable;
import jaci.openrio.toast.core.command.UsageException;
import jaci.openrio.toast.core.io.usb.MassStorageDevice;
import jaci.openrio.toast.core.io.usb.USBMassStorage;
import jaci.openrio.toast.lib.module.ModuleConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * This command is used for USB Mass Storage devices. This will be responsible for generating missing autorun files,
 * dumping Toast Local data to a USB Drive and loading a backup from a USB drive.
 *
 * command_name: 'usb'
 * args:
 *      generate  Generates a toast_autorun.conf file on every invalid USB drive connected
 *      dump  Dumps the local Toast files to a USB device.
 *          [drive_name]  Specify a USB drive to dump to. If not set, it will dump to all drives.
 *      load  Copy all Toast files from USB drives to the local Toast directory (reload backup)
 *          [drive_name]  Specify a USB drive to load from. If not set, it will load from all drives.
 *
 * @author Jaci
 */
public class CommandUSB extends AbstractCommand implements IHelpable {
    /**
     * Get the command name
     * e.g. 'cmd' for a command such as 'cmd <your args>
     */
    @Override
    public String getCommandName() {
        return "usb";
    }

    /**
     * Invoke the command if the name matches the one to be triggered
     * @param argLength The amount of arguments in the 'args' param
     * @param args The arguments the command was invoked with. This can be empty if
     *             none were provided. Keep in mind this does NOT include the Command Name.
     *             Args are separated by spaces
     * @param command The full command message
     */
    @Override
    public void invokeCommand(int argLength, String[] args, String command) {
        if (argLength == 1 || argLength == 2) {
            boolean specified = argLength == 2;

            if (args[0].equals("generate")) {
                int count = 0;
                for (File file : USBMassStorage.invalidDrives) {
                    File conf = new File(file, "toast_autorun.conf");
                    ModuleConfig config = new ModuleConfig(conf);
                    count++;
                }
                USBMassStorage.init();

                if (count != 0)
                    Toast.log().info("Successfully Generated toast_autorun.conf Files in " + count + " invalid drive(s).");
            } else if (args[0].equals("dump")) {
                for (MassStorageDevice drive : USBMassStorage.connectedDevices) {
                    long time = System.currentTimeMillis();
                    File root = new File(drive.dump_directory, "dump_" + time);
                    try {
                        if (!specified || drive.drive_name.equalsIgnoreCase(args[1])) {
                            copyDirectory(ToastBootstrap.toastHome, root);
                            Toast.log().info("Data Dump Successful: dump_" + time + " on drive: " + drive.drive_name);
                        }
                    } catch (IOException e) {
                        Toast.log().error("Could not Dump Toast Files on drive: " + drive.drive_name);
                        Toast.log().exception(e);
                    }
                }
            } else if (args[0].equals("load")) {
                for (MassStorageDevice drive : USBMassStorage.connectedDevices) {
                    try {
                        if (!specified || drive.drive_name.equalsIgnoreCase(args[1])) {
                            copyDirectory(drive.toast_directory, ToastBootstrap.toastHome);
                            Toast.log().info("Data Load Successful From Drive: " + drive.drive_name);
                        }
                    } catch (IOException e) {
                        Toast.log().error("Could not Load Toast Files from drive: " + drive.drive_name);
                        Toast.log().exception(e);
                    }
                }
            } else
                usage();
        } else {
            usage();
        }
    }

    /**
     * Throw a new exception if the Usage of the command is not valid
     */
    private void usage() {
        throw new UsageException("usb <generate|dump|load> [drive_name]");
    }

    /**
     * Copy an entire directory from one location to another. This is used in File Dumping.
     */
    private void copyDirectory(File source, File dest) throws IOException {
        for (File f : source.listFiles()) {
            File sourceFile = new File(source, f.getName());
            File destFile = new File(dest, f.getName());
            if (f.isDirectory()) {
                destFile.mkdirs();
                copyDirectory(sourceFile, destFile);
            } else
                copyFile(sourceFile, destFile);
        }
    }

    /**
     * Copy a Single file from one location to another. This is called from the {@link #copyDirectory(File, File)} method
     */
    private void copyFile(File source, File dest) throws IOException {
        FileInputStream inputStream = new FileInputStream(source);
        FileOutputStream outputStream = new FileOutputStream(dest);
        int lengthStream;
        byte[] buff = new byte[1024];
        while ((lengthStream = inputStream.read(buff)) > 0) {
            outputStream.write(buff, 0, lengthStream);
        }
        outputStream.close();
        inputStream.close();
    }

    /**
     * Returns a help message to display with the 'help' command
     */
    @Override
    public String getHelp() {
        return "Used for dealing with USB Mass Storage devices connected to the RoboRIO. Args: [generate, dump, load]\n" +
                "\tgenerate: Will generate a toast_autorun.conf file on the USB Device\n" +
                "\tdump: Dump all Toast local files to the USB Device\n" +
                "\tload: Copy all Toast files from the USB Device to the local RoboRIO";
    }
}
