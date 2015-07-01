var isJSON = function(str) {
    try {
        JSON.parse(str);
    } catch (e) {
        return false;
    }
    return true;
};

var deepmerge = function(obj1, obj2) {
    for (var object2 in obj2) {
        var value = obj2[object2];
        if (object2 in obj1) {
            if (typeof value == "object") {
                deepmerge(obj1[object2], value);
            } else {
                obj1[object2] = value;
            }
        } else {
            obj1[object2] = value;
        }
    }
    return obj1;
};

var merge = function(defaults, config) {
    var defobj = {};
    var confobj = {};
    for (var attr in defaults) {
        if (defaults.hasOwnProperty(attr))
            unpack(defobj, attr, defaults[attr]);
    }
    for (attr in config) {
        if (config.hasOwnProperty(attr))
            unpack(confobj, attr, config[attr]);
    }

    return deepmerge(defobj, confobj);
};

var unpack = function(obj, str, value) {
    var split = str.split(".");
    var lobj = obj;
    for (var current in split) {
        if (split.hasOwnProperty(current)) {
            var curv = split[current];      //Real objects have curvs
            if (!(curv in lobj && typeof lobj[curv] == "object")) {
                lobj[curv] = {};
            }
            if (current == split.length - 1) {
                lobj[curv] = value;
            }
            lobj = lobj[curv];
        }
    }
};

var parse = function(str_defaults, str_config) {
    return merge(
        isJSON(str_defaults) ? JSON.parse(str_defaults) : {},
        isJSON(str_config) ? JSON.parse(str_config) : {});
};

var toJSON = function(obj) {
    return JSON.stringify(obj, null, 4);
};

//var postProcess = function(str) {
//    var reg = /\$\{([^\{^\}]*)\}/g;
//    return str.replace(reg, function(full_match, words) {
//        return eval(words);
//    });
//};

var postProcess = function(str) {
    var build = [];
    for (var i = 0; i < str.length; i++) {
        var char = str[i];
        var prevchar = i === 0 ? "" : str[i - 1];
        if (char == "$" && str.length-1 != i) {
            if (prevchar == "\\") {build.pop(); build.push(char); continue;}
            var nchar = str[++i];
            if (nchar == "{") {
                var q = false, bl = 1;
                var jsbuild = "";
                while (bl > 0 && i < str.length) {
                    var newchar = str[++i];
                    switch (newchar) {
                        case "{": if (!q) bl++; jsbuild += newchar; break;
                        case "}": if (!q) bl--; if (bl!==0) jsbuild += newchar; break;
                        case "\"": q = !q; jsbuild += newchar; break;
                        default: jsbuild += newchar; break;
                    }
                }
                build.push(eval(jsbuild));
            }
        } else {
            build.push(char);
        }
    }
    return build.join("");
};