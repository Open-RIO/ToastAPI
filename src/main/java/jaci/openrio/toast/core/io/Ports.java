package jaci.openrio.toast.core.io;

import edu.wpi.first.wpilibj.*;

public enum Ports {
    DIO_0(0, Type.DIO),
    DIO_1(1, Type.DIO),
    DIO_2(2, Type.DIO),
    DIO_3(3, Type.DIO),
    DIO_4(4, Type.DIO),
    DIO_5(5, Type.DIO),
    DIO_6(6, Type.DIO),
    DIO_7(7, Type.DIO),
    DIO_8(8, Type.DIO),
    DIO_9(9, Type.DIO),

    PWM_0(0, Type.PWM),
    PWM_1(1, Type.PWM),
    PWM_2(2, Type.PWM),
    PWM_3(3, Type.PWM),
    PWM_4(4, Type.PWM),
    PWM_5(5, Type.PWM),
    PWM_6(6, Type.PWM),
    PWM_7(7, Type.PWM),
    PWM_8(8, Type.PWM),
    PWM_9(9, Type.PWM),

    ANALOG_0(0, Type.ANALOG),
    ANALOG_1(1, Type.ANALOG),
    ANALOG_2(2, Type.ANALOG),
    ANALOG_3(3, Type.ANALOG),
    ;

    int pid;
    Type portType;

    AnalogInput aio;
    DigitalInput dii;
    DigitalOutput dio;
    PWM pwm;

    Ports(int id, Type t) {
        this.pid = id;
        this.portType = t;
    }

    public Type getType() {
        return portType;
    }

    /* DIO */
    
    public static enum Type {
        DIO,
        PWM,
        ANALOG;
    }

}
