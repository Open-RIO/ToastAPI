package jaci.openrio.toast.lib.util;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class JSONUtil {

    public static Object jsonToJava(Object obj) {
        if (obj instanceof JsonObject) {
            return gsonToHash((JsonObject) obj);
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

    public static HashMap<String, Object> gsonToHash(JsonObject object) {
        HashMap<String, Object> hash = new HashMap<>();
        for (Map.Entry<String, Object> entry : object.entrySet()) {
            String key = entry.getKey();
            Object el = entry.getValue();
            hash.put(key, jsonToJava(el));
        }
        return hash;
    }

}
