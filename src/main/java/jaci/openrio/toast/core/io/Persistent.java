package jaci.openrio.toast.core.io;

import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;
import com.grack.nanojson.JsonWriter;
import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.core.ToastBootstrap;
import jaci.openrio.toast.lib.util.JSONUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * The Persistent Class is designed to enable Persistent Data Storage. This loads data from file upon creation and can
 * be saved (in JSON format) at your discretion. This can be used for diagnostics, persistent settings and anything
 * stored by code. This is the equivalent of the ModuleConfig class, but values are set by code instead of by user.
 *
 * @author Jaci
 */
public class Persistent {

    File theFile;

    boolean dirty;
    HashMap<String, Object> entryMap = new HashMap<>();

    public Persistent(File targetFile) {
        targetFile.getParentFile().mkdirs();
        boolean isNew = !targetFile.exists();
        try {
            if (isNew) targetFile.createNewFile();
        } catch (IOException e) {
            Toast.log().error("Could not create Persistent File: " + targetFile);
            Toast.log().exception(e);
        }
        this.theFile = targetFile;
        if (!isNew) load();
    }

    public Persistent(String name) {
        this(new File(ToastBootstrap.toastHome, "system/persistent/" + name + ".dat"));
    }

    public void clear() {
        entryMap.clear();
    }

    public boolean isDirty() {
        return dirty;
    }

    public void save() {
        try {
            JsonObject obj = JSONUtil.hashToJson(entryMap);
            FileOutputStream fos = new FileOutputStream(theFile);
            JsonWriter.indent("\t").on(fos).value(obj).done();
            fos.close();
        } catch (IOException e) {
            Toast.log().error("Could not save Persistent File: " + theFile);
            Toast.log().exception(e);
        }
    }

    public void load() {
        try {
            FileInputStream fis = new FileInputStream(theFile);
            JsonObject obj = JsonParser.object().from(fis);
            fis.close();
            entryMap = JSONUtil.jsonToHash(obj);
        } catch (IOException | JsonParserException e) {
            Toast.log().error("Could not load Persistent File: " + theFile);
            Toast.log().exception(e);
        }
    }

    private void updateValue(String name, Object value) {
        dirty = true;
        entryMap.put(name, value);
    }

    public void setNumber(String key, Number value) {
        updateValue(key, value);
    }

    public void setString(String key, String value) {
        updateValue(key, value);
    }

    public void setBoolean(String key, boolean bool) {
        updateValue(key, bool);
    }

    public void remove(String key) {
        entryMap.remove(key);
    }

    public HashMap<String, Object> getMap() {
        return entryMap;
    }

    public String getString(String key) {
        return (String) entryMap.get(key);
    }

    public boolean getBoolean(String key) {
        return (boolean) entryMap.get(key);
    }

    public Number getNumber(String key) {
        return (Number) entryMap.get(key);
    }

    public double getDouble(String key) {
        return getNumber(key).doubleValue();
    }

    public float getFloat(String key) {
        return getNumber(key).floatValue();
    }

    public int getInteger(String key) {
        return getNumber(key).intValue();
    }

    public short getShort(String key) {
        return getNumber(key).shortValue();
    }

    public long getLong(String key) {
        return getNumber(key).longValue();
    }

    public byte getByte(String key) {
        return getNumber(key).byteValue();
    }

    public boolean valueExists(String key) {
        return entryMap.containsKey(key);
    }

}
