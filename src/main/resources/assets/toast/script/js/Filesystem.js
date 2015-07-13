jimport("jaci.openrio.toast.core.script.js.proxy.FileProxy", "__JFile");

var __withWriter = function(fl, cb) {
    var jio = $("java.io");
    var bw = new jio["BufferedWriter"](new jio["FileWriter"](fl));
    cb(bw);
    bw.close();
};

var __withReader = function(fl, cb) {
    var jio = $("java.io");
    var br = new jio["BufferedReader"](new jio["FileReader"](fl));
    cb(br);
    br.close();
};

var __FileExtension = Java.extend(__JFile);

function File(path) {
    var fl = new __FileExtension(path, {
        withWriter: function(cb) {
            __withWriter(fl, cb);
        },
        withReader: function(cb) {
            __withReader(fl, cb);
        }
    });
    return fl;
}