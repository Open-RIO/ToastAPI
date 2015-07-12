var find_common_pkg = function(pkglist) {
    var common = [pkglist[0].split(/\./)];
    pkglist.forEach(function(pkg) {
        var spl = pkg.split(/\./);
        var fnd = false;
        common.forEach(function(com, ind) {
            if (spl[0] === com[0]) {
                common[ind] = spl.filter(function(n) {
                    return com.indexOf(n) != -1;
                });
                fnd = true;
            }
        });
        if (!fnd) common.push(spl);
    });

    var strjoin = [];
    common.forEach(function(arg) {
        strjoin.push(arg.join("."));
    });
    return strjoin;
};

var arr_to_vector = function(arr) {
    var vec = $('java.util.Vector');
    var vector = new vec();
    arr.forEach(function(thing) {
        vector.add(thing);
    });
    return vector;
};