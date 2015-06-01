package toast.examples;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import jaci.openrio.toast.lib.module.ToastStateModule;
import jaci.openrio.toast.lib.state.RobotState;

public class DrivingModule extends ToastStateModule {

    static RobotDrive robot_drive;
    static Joystick joystick;

    @Override
    public String getModuleName() {
        return "Driving_Toast_Module";
    }

    @Override
    public String getModuleVersion() {
        return "1.0.0";         // Recommended to follow the standard Toast Versioning System (major.minor.build-prebuild)
    }

    @Override
    public void prestart() {
        robot_drive = new RobotDrive(0, 1, 2, 3);               // You can replace these channels for anything you want, or even your own motor controller
        joystick = new Joystick(0);
    }

    @Override
    public void start() { }

    @Override
    public void tickState(RobotState state) {
        if (state == RobotState.TELEOP) {
            robot_drive.tankDrive(joystick.getZ(GenericHID.Hand.kLeft), joystick.getZ(GenericHID.Hand.kRight));         // Drives the robot using the left and right triggers
        } else if (state == RobotState.AUTONOMOUS)
            robot_drive.stopMotor();                                                                                    // Feed the WatchDog
    }
}
