package edu.wpi.first.wpilibj.communication;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import edu.wpi.first.wpilibj.hal.JNIWrapper;
import jaci.openrio.toast.core.loader.simulation.SimulationData;
import jaci.openrio.toast.lib.state.RobotState;

public class FRCNetworkCommunicationsLibrary extends JNIWrapper {
	public static interface tModuleType {
		public static final int kModuleType_Unknown = 0x00;
		public static final int kModuleType_Analog = 0x01;
		public static final int kModuleType_Digital = 0x02;
		public static final int kModuleType_Solenoid = 0x03;
	};
	public static interface tTargetClass {
		public static final int kTargetClass_Unknown = 0x00;
		public static final int kTargetClass_FRC1 = 0x10;
		public static final int kTargetClass_FRC2 = 0x20;
		public static final int kTargetClass_FRC2_Analog = (int)FRCNetworkCommunicationsLibrary.tTargetClass.kTargetClass_FRC2 | (int)FRCNetworkCommunicationsLibrary.tModuleType.kModuleType_Analog;
		public static final int kTargetClass_FRC2_Digital = (int)FRCNetworkCommunicationsLibrary.tTargetClass.kTargetClass_FRC2 | (int)FRCNetworkCommunicationsLibrary.tModuleType.kModuleType_Digital;
		public static final int kTargetClass_FRC2_Solenoid = (int)FRCNetworkCommunicationsLibrary.tTargetClass.kTargetClass_FRC2 | (int)FRCNetworkCommunicationsLibrary.tModuleType.kModuleType_Solenoid;
		public static final int kTargetClass_FamilyMask = 0xF0;
		public static final int kTargetClass_ModuleMask = 0x0F;
	};
	public static interface tResourceType {
		public static final int kResourceType_Controller = 0;
		public static final int kResourceType_Module = 1;
		public static final int kResourceType_Language = 2;
		public static final int kResourceType_CANPlugin = 3;
		public static final int kResourceType_Accelerometer = 4;
		public static final int kResourceType_ADXL345 = 5;
		public static final int kResourceType_AnalogChannel = 6;
		public static final int kResourceType_AnalogTrigger = 7;
		public static final int kResourceType_AnalogTriggerOutput = 8;
		public static final int kResourceType_CANJaguar = 9;
		public static final int kResourceType_Compressor = 10;
		public static final int kResourceType_Counter = 11;
		public static final int kResourceType_Dashboard = 12;
		public static final int kResourceType_DigitalInput = 13;
		public static final int kResourceType_DigitalOutput = 14;
		public static final int kResourceType_DriverStationCIO = 15;
		public static final int kResourceType_DriverStationEIO = 16;
		public static final int kResourceType_DriverStationLCD = 17;
		public static final int kResourceType_Encoder = 18;
		public static final int kResourceType_GearTooth = 19;
		public static final int kResourceType_Gyro = 20;
		public static final int kResourceType_I2C = 21;
		public static final int kResourceType_Framework = 22;
		public static final int kResourceType_Jaguar = 23;
		public static final int kResourceType_Joystick = 24;
		public static final int kResourceType_Kinect = 25;
		public static final int kResourceType_KinectStick = 26;
		public static final int kResourceType_PIDController = 27;
		public static final int kResourceType_Preferences = 28;
		public static final int kResourceType_PWM = 29;
		public static final int kResourceType_Relay = 30;
		public static final int kResourceType_RobotDrive = 31;
		public static final int kResourceType_SerialPort = 32;
		public static final int kResourceType_Servo = 33;
		public static final int kResourceType_Solenoid = 34;
		public static final int kResourceType_SPI = 35;
		public static final int kResourceType_Task = 36;
		public static final int kResourceType_Ultrasonic = 37;
		public static final int kResourceType_Victor = 38;
		public static final int kResourceType_Button = 39;
		public static final int kResourceType_Command = 40;
		public static final int kResourceType_AxisCamera = 41;
		public static final int kResourceType_PCVideoServer = 42;
		public static final int kResourceType_SmartDashboard = 43;
		public static final int kResourceType_Talon = 44;
		public static final int kResourceType_HiTechnicColorSensor = 45;
		public static final int kResourceType_HiTechnicAccel = 46;
		public static final int kResourceType_HiTechnicCompass = 47;
		public static final int kResourceType_SRF08 = 48;
	};
	public static interface tInstances {
		public static final int kLanguage_LabVIEW = 1;
		public static final int kLanguage_CPlusPlus = 2;
		public static final int kLanguage_Java = 3;
		public static final int kLanguage_Python = 4;
		public static final int kCANPlugin_BlackJagBridge = 1;
		public static final int kCANPlugin_2CAN = 2;
		public static final int kFramework_Iterative = 1;
		public static final int kFramework_Simple = 2;
		public static final int kRobotDrive_ArcadeStandard = 1;
		public static final int kRobotDrive_ArcadeButtonSpin = 2;
		public static final int kRobotDrive_ArcadeRatioCurve = 3;
		public static final int kRobotDrive_Tank = 4;
		public static final int kRobotDrive_MecanumPolar = 5;
		public static final int kRobotDrive_MecanumCartesian = 6;
		public static final int kDriverStationCIO_Analog = 1;
		public static final int kDriverStationCIO_DigitalIn = 2;
		public static final int kDriverStationCIO_DigitalOut = 3;
		public static final int kDriverStationEIO_Acceleration = 1;
		public static final int kDriverStationEIO_AnalogIn = 2;
		public static final int kDriverStationEIO_AnalogOut = 3;
		public static final int kDriverStationEIO_Button = 4;
		public static final int kDriverStationEIO_LED = 5;
		public static final int kDriverStationEIO_DigitalIn = 6;
		public static final int kDriverStationEIO_DigitalOut = 7;
		public static final int kDriverStationEIO_FixedDigitalOut = 8;
		public static final int kDriverStationEIO_PWM = 9;
		public static final int kDriverStationEIO_Encoder = 10;
		public static final int kDriverStationEIO_TouchSlider = 11;
		public static final int kADXL345_SPI = 1;
		public static final int kADXL345_I2C = 2;
		public static final int kCommand_Scheduler = 1;
		public static final int kSmartDashboard_Instance = 1;
	};
	public static final int kFRC_NetworkCommunication_DynamicType_DSEnhancedIO_Input = 17;
	public static final int kFRC_NetworkCommunication_DynamicType_Kinect_Vertices1 = 21;
	public static final int SYS_STATUS_DATA_SIZE = 44;
	public static final int kFRC_NetworkCommunication_DynamicType_Kinect_Custom = 25;
	public static final int kFRC_NetworkCommunication_DynamicType_Kinect_Vertices2 = 23;
	public static final int kFRC_NetworkCommunication_DynamicType_Kinect_Header = 19;
	public static final int kFRC_NetworkCommunication_DynamicType_Kinect_Joystick = 24;
	public static final int IO_CONFIG_DATA_SIZE = 32;
	public static final int kMaxModuleNumber = 2;
	public static final int kFRC_NetworkCommunication_DynamicType_DSEnhancedIO_Output = 18;
	public static final int kFRC_NetworkCommunication_DynamicType_Kinect_Extra2 = 22;
	public static final int kFRC_NetworkCommunication_DynamicType_Kinect_Extra1 = 20;
	public static final int USER_DS_LCD_DATA_SIZE = 128;
	public static final int kUsageReporting_version = 1;
	public static final int USER_STATUS_DATA_SIZE = (984 - 32 - 44);

	public static int report(int resource, byte instanceNumber, byte context, String feature) {
		return 0;
	}

	public static int FRCNetworkCommunicationUsageReportingReport(byte resource, byte instanceNumber, byte context, String feature) {
		//TODO: Report
		return 0;
	}

	public static void getFPGAHardwareVersion(ShortBuffer fpgaVersion, IntBuffer fpgaRevision) {}

	public static void setNewDataSem(ByteBuffer mutexId) {}

	public static void FRCNetworkCommunicationGetVersionString(ByteBuffer version) {}
	public static void FRCNetworkCommunicationObserveUserProgramStarting() {}
	public static void FRCNetworkCommunicationObserveUserProgramDisabled() {}
	public static void FRCNetworkCommunicationObserveUserProgramAutonomous() {}
	public static void FRCNetworkCommunicationObserveUserProgramTeleop() {}
	public static void FRCNetworkCommunicationObserveUserProgramTest() {}
	public static void FRCNetworkCommunicationReserve() {}

	private static int NativeHALGetControlWord() {
		//TODO return robot state
		return 0;
	}

	public static HALControlWord HALGetControlWord() {
		RobotState s = SimulationData.currentState;
		return new HALControlWord(s != RobotState.DISABLED, s == RobotState.AUTONOMOUS, s == RobotState.TEST,
				false, false, true);
	}

	private static int NativeHALGetAllianceStation() {
		return 0;
	}

	public static HALAllianceStationID HALGetAllianceStation() {
		switch(NativeHALGetAllianceStation()) {
		case 0:
			return HALAllianceStationID.Red1;
		case 1:
			return HALAllianceStationID.Red2;
		case 2:
			return HALAllianceStationID.Red3;
		case 3:
			return HALAllianceStationID.Blue1;
		case 4:
			return HALAllianceStationID.Blue2;
		case 5:
			return HALAllianceStationID.Blue3;
		default:
			return null;
		}
	}

	public static int kMaxJoystickAxes = 12;
	public static int kMaxJoystickPOVs = 12;
	public static short[] HALGetJoystickAxes(byte joystickNum) {
		return new short[]{0, 0, 0};
	}
	public static short[] HALGetJoystickPOVs(byte joystickNum) {
		return new short[]{0, 0, 0};
	}
	public static int HALGetJoystickButtons(byte joystickNum, ByteBuffer count) {
		return 12;
	}
	public static int HALSetJoystickOutputs(byte joystickNum, int outputs, short leftRumble, short rightRumble) {
		return 0;
	}
	public static float HALGetMatchTime() {
		return 120;
	}
	public static boolean HALGetSystemActive(IntBuffer status) {
		return true;
	}
	public static boolean HALGetBrownedOut(IntBuffer status) {
		return false;
	}
	
	public static int HALSetErrorData(String error) {
		//TODO report error
		return 0;
	}
}
