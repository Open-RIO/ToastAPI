package jaci.openrio.toast.core.command.cmd;

import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.core.ToastBootstrap;
import jaci.openrio.toast.core.command.AbstractCommand;
import jaci.openrio.toast.core.command.IHelpable;
import jaci.openrio.toast.core.command.UsageException;
import jaci.openrio.toast.core.io.usb.MassStorageDevice;
import jaci.openrio.toast.core.io.usb.USBMassStorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

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
    @Override
    public String getCommandName() {
        return "usb";
    }

    @Override
    public void invokeCommand(int argLength, String[] args, String command) {
        if (argLength == 1 || argLength == 2) {
            boolean specified = argLength == 2;

            if (args[0].equals("generate")) {
                int count = 0;
                for (File file : USBMassStorage.invalidDrives) {
                    File conf = new File(file, "toast_autorun.conf");
                    try {
                        conf.createNewFile();
                        Files.copy(CommandUSB.class.getResourceAsStream("/assets/toast/autorun_default.conf"), conf.getCanonicalFile().toPath(), StandardCopyOption.REPLACE_EXISTING);
                        count++;
                    } catch (IOException e) {
                        Toast.log().error("Could not generate toast_autorun.conf file in: " + file);
                        Toast.log().exception(e);
                    }
                }
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

    private void usage() {
        //Toast.log().warn("Usage: usb <generate|dump|load> [drive_name]");
        throw new UsageException("usb <generate|dump|load> [drive_name]");
    }

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

    @Override
    public String getHelp() {
        return "Used for dealing with USB Mass Storage devices connected to the RoboRIO. Args: [generate, dump, load]\n" +
                "\tgenerate: Will generate a toast_autorun.conf file on the USB Device\n" +
                "\tdump: Dump all Toast local files to the USB Device\n" +
                "\tload: Copy all Toast files from the USB Device to the local RoboRIO";
    }
}
