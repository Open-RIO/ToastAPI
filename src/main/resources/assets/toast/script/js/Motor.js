var _imports = {};
jimport("edu.wpi.first.wpilibj.SpeedController", "_imports['SpeedController']");
jimport("edu.wpi.first.wpilibj.RobotDrive", "_imports['RobotDrive']");
jimport("jaci.openrio.toast.lib.registry.Registrar", "_imports['Registrar']");
var _motor_types = {Talon: "talon", TalonSRX: "talonSRX", CANTalon: "canTalon", Victor: "victor", Jaguar: "jaguar", CANJaguar: "canJaguar", VictorSP: "victorSP"};

$.drive = function() {
    if (arguments.length == 2) {
        return new _imports['RobotDrive'](arguments[0], arguments[1]);
    } else if (arguments.length == 4) {
        return new _imports['RobotDrive'](arguments[0], arguments[1], arguments[2], arguments[3]);
    } else throw new Error("Invalid arguments -- Expecting 2 || 4");
};

$.motor = function() {
    var type = "Talon";
    var port = 0;
    if (arguments.length == 1) {
        port = arguments[0];
    } else if (arguments.length == 2) {
        type = arguments[0];
        port = arguments[1];
    } else throw new Error("Invalid arguments -- Expecting 1..2");
    return _imports['Registrar'][_motor_types[type]](port);
};