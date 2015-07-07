var _imports = {};
jimport("edu.wpi.first.wpilibj.SpeedController", "_imports['SpeedController']");
jimport("edu.wpi.first.wpilibj.RobotDrive", "_imports['RobotDrive']");
var _motor_types = {Talon: "Talon", TalonSRX: "CANTalon", Victor: "Victor", Jaguar: "Jaguar", CANJaguar: "CANJaguar"};
_imports['motors'] = {};

for (var curtype in _motor_types) {
    if (_motor_types.hasOwnProperty(curtype))
        jimport("edu.wpi.first.wpilibj." + _motor_types[curtype], "_imports['motors']['" + curtype + "']");
}

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
        port = arguments[0];
        type = arguments[1];
    } else throw new Error("Invalid arguments -- Expecting 1..2");
    return new _imports['motors'][type](port);
};