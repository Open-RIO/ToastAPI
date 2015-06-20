/**
 * Converts a HashMap to a JavaScript object that can be sent through to JSON.stringify() or other methods.
 * This implementation supports Map<>, List<>, Object[] and Object types
 */
var hash_to_object = function(hash) {
    var object = {};
    for (var obj in hash) {
        if (java.util.Map.class.isAssignableFrom(hash[obj].class)) {
            object[obj] = hash_to_object(hash[obj]);
        } else if (hash[obj].class.isArray() || java.util.List.class.isAssignableFrom(hash[obj].class)) {
            var arr = [];
            for (var objin in hash[obj])
                arr.push(hash[obj][objin]);
            object[obj] = arr;
        } else
            object[obj] = hash[obj];
    }
    return object;
};