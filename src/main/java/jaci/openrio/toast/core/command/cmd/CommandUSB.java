package jaci.openrio.toast.core.command.cmd;

import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.core.ToastBootstrap;
import jaci.openrio.toast.core.command.AbstractCommand;
import jaci.openrio.toast.core.io.usb.MassStorageDevice;
import jaci.openrio.toast.core.io.usb.USBMassStorage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class CommandUSB extends AbstractCommand {
    @Override
    public String getCommandName() {
        return "usb";
    }

    @Override
    public void invokeCommand(int argLength, String[] args, String command) {
        if (argLength == 1) {
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
                    File root = new File(drive.toast_directory, "usb_dumps/dump_" + time);
                    try {
                        copyDirectory(ToastBootstrap.toastHome, root);
                        Toast.log().info("Data Dump Successful: dump_" + time + " on drive: " + drive.drive_name);
                    } catch (IOException e) {
                        Toast.log().error("Could not Dump Toast Files on drive: " + drive.drive_name);
                        Toast.log().exception(e);
                    }
                }
            }
        } else {
            Toast.log().info("Usage: usb <generate|dump>");
        }
    }

    private void copyDirectory(File source, File dest) throws IOException {
        for (File f : source.listFiles()) {
            if (f.isDirectory())
                copyDirectory(f, dest);
            else {
                Path resolve = dest.toPath().resolve(f.toPath());
                resolve.toFile().getParentFile().mkdirs();
                Files.copy(f.getAbsoluteFile().toPath(), resolve);
            }
        }
    }
}
