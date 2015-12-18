package jaci.openrio.toast.lib.util;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Utilities related to JSON operations. Due to the switching from Google GSON to Nanojson, some utilities are no longer available,
 * and as such, we're implementing them here.
 *
 * @author Jaci
 */
public class JSONUtil {

    /**
     * Convert a JSON Object to a Java Object. This converts JSON Objects to String-Object HashMaps and
     * JSON Arrays to standard Object[] arrays. This method will recurse deeply.
     * @return The Java Version of a JSON Object
     */
    public static Object jsonToJava(Object obj) {
        if (obj instanceof JsonObject) {
            return jsonToHash((JsonObject) obj);
        } else if (obj instanceof JsonArray) {
            JsonArray arr = (JsonArray) obj;
            Object[] objs = new Object[arr.size()];
            for (int i = 0; i < arr.size(); i++) {
                objs[i] = jsonToJava(arr.get(i));
            }
            return objs;
        }
        return obj;
    }

    /**
     * Convert a JSON Object to a String - Object HashMap. This function will recurse deeply.
     */
    public static HashMap<String, Object> jsonToHash(JsonObject object) {
        HashMap<String, Object> hash = new HashMap<>();
        for (Map.Entry<String, Object> entry : object.entrySet()) {
            String key = entry.getKey();
            Object el = entry.getValue();
            hash.put(key, jsonToJava(el));
        }
        return hash;
    }

    /**
     * Convert a String - Object HashMap to a JSON Object. This function will recurse deeply.
     */
    public static JsonObject hashToJson(HashMap<String, Object> hash) {
        return new JsonObject(hash);        // That was easier than expected
    }
}
