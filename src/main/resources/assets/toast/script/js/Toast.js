Logger = Java.type("jaci.openrio.toast.lib.log.Logger");
var __js_logger = new Logger("JavaScript", 1 | 2 | 4); delete Logger;
JSEngine = Java.type("jaci.openrio.toast.core.script.js.JSEngine");

var _toast_vars = {};
_toast_vars["ticker"] = [];
_toast_vars["transition"] = [];
_toast_vars["heartbeat"] = [];

var $ = function(arg) {
    return Packages[arg];               //Java.type will only get classes, not packages, but Packages[] does both... for some reason (?)
};

var loadsys = function(file) {
    $("jaci.openrio.toast.core.script.js.JavaScript").loadSystemLib(file);
};

var jimport = function() {
    var jclasstype = $(arguments[0]);
    eval((arguments.length == 1 ? jclasstype.class.simpleName : arguments[1]) + " = jclasstype");
};

var t_$ = function(arg) { };

t_$.tick = function(md) {
    for (var obj in _toast_vars.ticker) {
        var val = _toast_vars.ticker[obj];
        if (val.mode == md)
            val.func();
    }
};

t_$.trans = function(md) {
    for (var obj in _toast_vars.transition) {
        var val = _toast_vars.transition[obj];
        if (val.mode == md)
            val.func();
    }
};

t_$.heartbeat = function(skip) {
    for (var obj in _toast_vars.heartbeat) {
        var cb = _toast_vars.heartbeat[obj];
        if (cb.length == 0)
            cb();
        else if (cb.length == 1)
            cb(skip);
    }
};

$.exit = function() {
    __toast.shutdownSafely();
};

$.crash = function() {
    __toast.shutdownCrash();
};

$.log = function(message) {
    __js_logger.info(message);
};

$.log = function(message, level) {
    __js_logger[level](message);
};

$.auto = function(callback) {
    _toast_vars.ticker.push({mode: 'autonomous', func: callback});
};

$.onauto = function(callback) {
    _toast_vars.transition.push({mode: 'autonomous', func: callback});
};

$.teleop = function(callback) {
    _toast_vars.ticker.push({mode: 'teleop', func: callback});
};

$.onteleop = function(callback) {
    _toast_vars.transition.push({mode: 'teleop', func: callback});
};

$.disabled = function(callback) {
    _toast_vars.ticker.push({mode: 'disabled', func: callback});
};

$.ondisabled = function(callback) {
    _toast_vars.transition.push({mode: 'disabled', func: callback});
};

$.test = function(callback) {
    _toast_vars.ticker.push({mode: 'test', func: callback});
};

$.ontest = function(callback) {
    _toast_vars.transition.push({mode: 'test', func: callback});
};

$.heartbeat = function(callback) {
    JSEngine.hb();
    _toast_vars.heartbeat.push(callback);
};

loadsys("Motor.js");