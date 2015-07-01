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

var postProcess = function(str) {
    var reg = /\$\{([^\{^\}]*)\}/g;
    return str.replace(reg, function(full_match, words) {
        return eval(words);
    });
};