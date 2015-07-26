jimport("jaci.openrio.toast.core.script.js.proxy.FileProxy", "__JFile");

$.withWriter = function(os, cb) {
    var jio = $("java.io");
    var bw = new jio["BufferedWriter"](new jio["OutputStreamWriter"](os));
    cb(bw);
    bw.close();
};

$.withOut = function(os, cb) {
    var jio = $("java.io");
    var out = new jio["DataOutputStream"](os);
    cb(out);
    out.close();
};

$.withReader = function(is, cb) {
    var jio = $("java.io");
    var br = new jio["BufferedReader"](new jio["InputStreamReader"](is));
    cb(br);
    br.close();
};

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
        },
        readFully: function() {
            var str = "";
            __withReader(fl, function(br) {
                var line = "";
                while ((line = br.readLine()) != null)
                    str += line + "\n";
            });
            return str;
        }
    });
    return fl;
}

$.file = function(root, target) {
    return new File(new java.io.File(new java.io.File(root.replace(/file:(\\|\/)?/, "")).getParentFile(), target));
};