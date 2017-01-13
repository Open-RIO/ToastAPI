package com.ctre;

import jaci.openrio.toast.core.loader.simulation.srx.SRX_Reg;

import java.util.HashMap;

public class CanTalonJNI {
    public static final int kMotionProfileFlag_ActTraj_IsValid = 1;
    public static final int kMotionProfileFlag_HasUnderrun = 2;
    public static final int kMotionProfileFlag_IsUnderrun = 4;
    public static final int kMotionProfileFlag_ActTraj_IsLast = 8;
    public static final int kMotionProfileFlag_ActTraj_VelOnly = 16;
    public static String ERR_CANSessionMux_InvalidBuffer_MESSAGE = "CAN: Invalid Buffer";
    public static String ERR_CANSessionMux_MessageNotFound_MESSAGE = "CAN: Message not found";
    public static String WARN_CANSessionMux_NoToken_MESSAGE = "CAN: No token";
    public static String ERR_CANSessionMux_NotAllowed_MESSAGE = "CAN: Not allowed";
    public static String ERR_CANSessionMux_NotInitialized_MESSAGE = "CAN: Not initialized";
    public static String CTR_RxTimeout_MESSAGE = "CTRE CAN Receive Timeout";
    public static String CTR_TxTimeout_MESSAGE = "CTRE CAN Transmit Timeout";
    public static String CTR_InvalidParamValue_MESSAGE = "CTRE CAN Invalid Parameter";
    public static String CTR_UnexpectedArbId_MESSAGE = "CTRE Unexpected Arbitration ID (CAN Node ID)";
    public static String CTR_TxFailed_MESSAGE = "CTRE CAN Transmit Error";
    public static String CTR_SigNotUpdated_MESSAGE = "CTRE CAN Signal Not Updated";
    public static final int CTR_RxTimeout = 1;
    public static final int CTR_TxTimeout = 2;
    public static final int CTR_InvalidParamValue = 3;
    public static final int CTR_UnexpectedArbId = 4;
    public static final int CTR_TxFailed = 5;
    public static final int CTR_SigNotUpdated = 6;
    public static final int ERR_CANSessionMux_InvalidBuffer = -44086;
    public static final int ERR_CANSessionMux_MessageNotFound = -44087;
    public static final int WARN_CANSessionMux_NoToken = 44087;
    public static final int ERR_CANSessionMux_NotAllowed = -44088;

    
    // Patch bootstrap stuff
    public static double ltd(long l) { return Double.longBitsToDouble(l); }
    public static long dtl(double d) { return Double.doubleToLongBits(d); }
    // End bootstrap stuff
    
    public CanTalonJNI() {
    }

    public static long new_CanTalonSRX(int deviceNumber, int controlPeriodMs, int enablePeriodMs) {
        SRX_Reg.wrappers.putIfAbsent((long)deviceNumber, new SRX_Reg.SRX_Wrapper(deviceNumber, controlPeriodMs));
        return deviceNumber;
    }

    public static long new_CanTalonSRX(int deviceNumber, int controlPeriodMs) {
        return new_CanTalonSRX(deviceNumber, controlPeriodMs, 0);
    }

    public static long new_CanTalonSRX(int deviceNumber) {
        return new_CanTalonSRX(deviceNumber, 0);
    }
    
    // Don't bother
    public static void delete_CanTalonSRX(long handle) { }

    public static void GetMotionProfileStatus(long handle, CANTalon talon, CANTalon.MotionProfileStatus status) {
        // Don't bother, not used
    }

    public static void Set(long handle, double value) {
        SRX_Reg.wrappers.get(handle).setVBus(value);
    }

    public static void SetParam(long handle, int param, double value) {
        SRX_Reg.wrappers.get(handle).params.put(param, value);
    }

    public static void RequestParam(long handle, int param) {
        //SRX_Reg.wrappers.get(handle).params.get(param);
    }

    public static double GetParamResponse(long handle, int param) {
        return SRX_Reg.wrappers.get(handle).params.get(param);
    }

    public static int GetParamResponseInt32(long handle, int param) {
        return (int)dtl(SRX_Reg.wrappers.get(handle).params.get(param));
    }

    public static void SetPgain(long handle, int slot_id, double value) {
        SRX_Reg.wrappers.get(handle).setGains((int)slot_id, 0, value);
    }

    public static void SetIgain(long handle, int slot_id, double value) {
        SRX_Reg.wrappers.get(handle).setGains((int)slot_id, 1, value);
    }

    public static void SetDgain(long handle, int slot_id, double value) {
        SRX_Reg.wrappers.get(handle).setGains((int)slot_id, 2, value);
    }

    public static void SetFgain(long handle, int slot_id, double value) {
        SRX_Reg.wrappers.get(handle).setGains((int)slot_id, 3, value);
    }

    public static void SetIzone(long var0, int var2, int var3) { }

    public static void SetCloseLoopRampRate(long var0, int var2, int var3) { }

    public static void SetVoltageCompensationRate(long var0, double var2) { }

    public static void SetSensorPosition(long var0, int var2) { }

    public static void SetForwardSoftLimit(long var0, int var2) { }

    public static void SetReverseSoftLimit(long var0, int var2) { }

    public static void SetForwardSoftEnable(long var0, int var2) { }

    public static void SetReverseSoftEnable(long var0, int var2) { }

    public static double GetPgain(long handle, int slot_id) {
        return SRX_Reg.wrappers.get(handle).gains[(int)slot_id][0];
    }

    public static double GetIgain(long handle, int slot_id) {
        return SRX_Reg.wrappers.get(handle).gains[(int)slot_id][1];
    }

    public static double GetDgain(long handle, int slot_id) {
        return SRX_Reg.wrappers.get(handle).gains[(int)slot_id][2];
    }

    public static double GetFgain(long handle, int slot_id) {
        return SRX_Reg.wrappers.get(handle).gains[(int)slot_id][3];
    }

    public static int GetIzone(long handle, int slot_id) {
        return 0;
    }

    public static int GetCloseLoopRampRate(long var0, int var2) { return 0; }

    public static double GetVoltageCompensationRate(long var0) { return 0; }

    public static int GetForwardSoftLimit(long var0) { return 0; }

    public static int GetReverseSoftLimit(long var0) { return 0; }

    public static int GetForwardSoftEnable(long var0) { return 0; }

    public static int GetReverseSoftEnable(long var0) { return 0; }

    public static int GetPulseWidthRiseToFallUs(long var0) { return 0; }

    public static int IsPulseWidthSensorPresent(long var0) { return 0; }

    public static void SetModeSelect(long handle, int mode) {
        SRX_Reg.wrappers.get(handle).setMode(mode);
    }

    public static void SetModeSelect2(long handle, int mode, int var3) {
        SRX_Reg.wrappers.get(handle).setMode(mode);
    }

    public static void SetStatusFrameRate(long var0, int var2, int var3) { }

    public static void ClearStickyFaults(long var0) { }

    public static void ChangeMotionControlFramePeriod(long var0, int var2) { }

    public static void ClearMotionProfileTrajectories(long var0) { }

    public static int GetMotionProfileTopLevelBufferCount(long var0) { return 0; }

    public static boolean IsMotionProfileTopLevelBufferFull(long var0) { return true; }

    public static void PushMotionProfileTrajectory(long var0, int var2, int var3, int var4, int var5, int var6, int var7, int var8) { }

    public static void ProcessMotionProfileBuffer(long var0) { }

    public static int GetFault_OverTemp(long var0) { return 0; }

    public static int GetFault_UnderVoltage(long var0) { return 0; }

    public static int GetFault_ForLim(long var0) { return 0; }

    public static int GetFault_RevLim(long var0) { return 0; }

    public static int GetFault_HardwareFailure(long var0) { return 0; }

    public static int GetFault_ForSoftLim(long var0) { return 0; }

    public static int GetFault_RevSoftLim(long var0) { return 0; }

    public static int GetStckyFault_OverTemp(long var0) { return 0; }

    public static int GetStckyFault_UnderVoltage(long var0) { return 0; }

    public static int GetStckyFault_ForLim(long var0) { return 0; }

    public static int GetStckyFault_RevLim(long var0) { return 0; }

    public static int GetStckyFault_ForSoftLim(long var0) { return 0; }

    public static int GetStckyFault_RevSoftLim(long var0) { return 0; }

    public static int GetAppliedThrottle(long handle) {
        return (int) (SRX_Reg.wrappers.get(handle).pvbus * 1023);
    }

    public static int GetCloseLoopErr(long var0) { return 0; }

    public static int GetFeedbackDeviceSelect(long var0) { return 0; }

    public static int GetModeSelect(long handle) {
        return SRX_Reg.wrappers.get(handle).mode;
    }

    public static int GetLimitSwitchEn(long var0) { return 0; }

    public static int GetLimitSwitchClosedFor(long var0) { return 0; }

    public static int GetLimitSwitchClosedRev(long var0) { return 0; }

    public static int GetClearPosOnIdx(long var0) { return 0; }

    public static int GetClearPosOnLimR(long var0) { return 0; }

    public static int GetClearPosOnLimF(long var0) { return 0; }

    public static int GetSensorPosition(long var0) { return 0; }

    public static int GetSensorVelocity(long var0) { return 0; }

    public static double GetCurrent(long var0) {
        return 10;  // I guess 10A is nominal?
    }

    public static int GetBrakeIsEnabled(long var0) { return 0;  }

    public static int GetEncPosition(long var0) { return 0;  }

    public static int GetEncVel(long var0) { return 0;  }

    public static int GetEncIndexRiseEvents(long var0) { return 0;  }

    public static int GetQuadApin(long var0) { return 0;  }

    public static int GetQuadBpin(long var0) { return 0;  }

    public static int GetQuadIdxpin(long var0) { return 0;  }

    public static int GetAnalogInWithOv(long var0) { return 0;  }

    public static int GetAnalogInVel(long var0) { return 0;  }

    public static double GetTemp(long var0) {
        return 30.0;        // 30C nominal?
    }

    public static double GetBatteryV(long var0) {
        return 12;        // 12V nominal
    }

    public static int GetResetCount(long var0) { return 0; }

    public static int GetResetFlags(long var0) { return 0; }

    public static int GetFirmVers(long var0) { return 0; }

    public static int GetPulseWidthPosition(long var0) { return 0; }

    public static int GetPulseWidthVelocity(long var0) { return 0; }

    public static int GetPulseWidthRiseToRiseUs(long var0) { return 0; }

    public static int GetActTraj_IsValid(long var0) { return 0; }

    public static int GetActTraj_ProfileSlotSelect(long var0) { return 0; }

    public static int GetActTraj_VelOnly(long var0) { return 0; }

    public static int GetActTraj_IsLast(long var0) { return 0; }

    public static int GetOutputType(long var0) { return 0; }

    public static int GetHasUnderrun(long var0) { return 0; }

    public static int GetIsUnderrun(long var0) { return 0; }

    public static int GetNextID(long var0) { return 0; }

    public static int GetBufferIsFull(long var0) { return 0; }

    public static int GetCount(long var0) { return 0; }

    public static int GetActTraj_Velocity(long var0) { return 0; }

    public static int GetActTraj_Position(long var0) { return 0; }

    public static void SetDemand(long handle, int demand) {
        SRX_Reg.wrappers.get(handle).setDemand(demand);
    }

    public static void SetOverrideLimitSwitchEn(long var0, int var2) { }

    public static void SetFeedbackDeviceSelect(long var0, int var2) { }

    public static void SetRevMotDuringCloseLoopEn(long var0, int var2) { }

    public static void SetOverrideBrakeType(long var0, int var2) { }

    public static void SetProfileSlotSelect(long var0, int var2) { }

    public static void SetRampThrottle(long var0, int var2) { }

    public static void SetRevFeedbackSensor(long var0, int var2) { }

    public static void SetCurrentLimEnable(long var0, boolean var2) { }

    public static int GetLastError(long var0) { return 0; }

    public static enum param_t {
        eProfileParamSlot0_P(1),
        eProfileParamSlot0_I(2),
        eProfileParamSlot0_D(3),
        eProfileParamSlot0_F(4),
        eProfileParamSlot0_IZone(5),
        eProfileParamSlot0_CloseLoopRampRate(6),
        eProfileParamSlot1_P(11),
        eProfileParamSlot1_I(12),
        eProfileParamSlot1_D(13),
        eProfileParamSlot1_F(14),
        eProfileParamSlot1_IZone(15),
        eProfileParamSlot1_CloseLoopRampRate(16),
        eProfileParamSoftLimitForThreshold(21),
        eProfileParamSoftLimitRevThreshold(22),
        eProfileParamSoftLimitForEnable(23),
        eProfileParamSoftLimitRevEnable(24),
        eOnBoot_BrakeMode(31),
        eOnBoot_LimitSwitch_Forward_NormallyClosed(32),
        eOnBoot_LimitSwitch_Reverse_NormallyClosed(33),
        eOnBoot_LimitSwitch_Forward_Disable(34),
        eOnBoot_LimitSwitch_Reverse_Disable(35),
        eFault_OverTemp(41),
        eFault_UnderVoltage(42),
        eFault_ForLim(43),
        eFault_RevLim(44),
        eFault_HardwareFailure(45),
        eFault_ForSoftLim(46),
        eFault_RevSoftLim(47),
        eStckyFault_OverTemp(48),
        eStckyFault_UnderVoltage(49),
        eStckyFault_ForLim(50),
        eStckyFault_RevLim(51),
        eStckyFault_ForSoftLim(52),
        eStckyFault_RevSoftLim(53),
        eAppliedThrottle(61),
        eCloseLoopErr(62),
        eFeedbackDeviceSelect(63),
        eRevMotDuringCloseLoopEn(64),
        eModeSelect(65),
        eProfileSlotSelect(66),
        eRampThrottle(67),
        eRevFeedbackSensor(68),
        eLimitSwitchEn(69),
        eLimitSwitchClosedFor(70),
        eLimitSwitchClosedRev(71),
        eSensorPosition(73),
        eSensorVelocity(74),
        eCurrent(75),
        eBrakeIsEnabled(76),
        eEncPosition(77),
        eEncVel(78),
        eEncIndexRiseEvents(79),
        eQuadApin(80),
        eQuadBpin(81),
        eQuadIdxpin(82),
        eAnalogInWithOv(83),
        eAnalogInVel(84),
        eTemp(85),
        eBatteryV(86),
        eResetCount(87),
        eResetFlags(88),
        eFirmVers(89),
        eSettingsChanged(90),
        eQuadFilterEn(91),
        ePidIaccum(93),
        eStatus1FrameRate(94),
        eStatus2FrameRate(95),
        eStatus3FrameRate(96),
        eStatus4FrameRate(97),
        eStatus6FrameRate(98),
        eStatus7FrameRate(99),
        eClearPositionOnIdx(100),
        ePeakPosOutput(104),
        eNominalPosOutput(105),
        ePeakNegOutput(106),
        eNominalNegOutput(107),
        eQuadIdxPolarity(108),
        eStatus8FrameRate(109),
        eAllowPosOverflow(110),
        eProfileParamSlot0_AllowableClosedLoopErr(111),
        eNumberPotTurns(112),
        eNumberEncoderCPR(113),
        ePwdPosition(114),
        eAinPosition(115),
        eProfileParamVcompRate(116),
        eProfileParamSlot1_AllowableClosedLoopErr(117),
        eStatus9FrameRate(118),
        eMotionProfileHasUnderrunErr(119),
        eReserved120(120),
        eLegacyControlMode(121),
        eMotMag_Accel(122),
        eMotMag_VelCruise(123),
        eStatus10FrameRate(124),
        eCurrentLimThreshold(125),
        eBldcStatus1FrameRate(129),
        eBldcStatus2FrameRate(130),
        eBldcStatus3FrameRate(131),
        eCustomParam0(137),
        eCustomParam1(138),
        ePersStorageSaving(139),
        eClearPositionOnLimitF(144),
        eClearPositionOnLimitR(145),
        eMotionMeas_YawOffset(160),
        eMotionMeas_CompassOffset(161),
        eMotionMeas_BetaGain(162),
        eMotionMeas_Reserved163(163),
        eMotionMeas_GyroNoMotionCal(164),
        eMotionMeas_EnterCalibration(165),
        eMotionMeas_FusedHeadingOffset(166);

        public final int value;

        private param_t(int value) {
            this.value = value;
        }
    }
}
