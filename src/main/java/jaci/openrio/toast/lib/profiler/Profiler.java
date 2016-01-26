package jaci.openrio.toast.lib.profiler;

import com.grack.nanojson.JsonBuilder;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonWriter;
import jaci.openrio.toast.core.ToastBootstrap;
import jaci.openrio.toast.lib.log.Logger;

import java.io.File;
import java.io.FileWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * A Time Cop. This keeps track of the amount of time everything takes to complete during initialization. This is
 * used to test optimizations and help you track down what's taking your robot so long to start up.
 *
 * @author Jaci
 */
public class Profiler {

    public static final Profiler INSTANCE = new Profiler("Main");

    String name;
    Logger log;

    HashMap<String, ProfilerSection> sections = new LinkedHashMap<>();

    /**
     * Create a new Profiler
     *
     * @param name   The name of the profiler. This is used in saving the profiler log to file.
     */
    public Profiler(String name) {
        this.name = name;
    }

    /**
     * Get (or create) a profiler section. This is the subset of the profiler action everything fits under, and is
     * shown in the root of the profiler tree.
     * @param name The name of the section. Short n' sweet
     */
    public ProfilerSection section(String name) {
        if (!sections.containsKey(name)) {
            ProfilerSection sec = new ProfilerSection(name, this);
            sections.put(name, sec);
            return sec;
        }
        return sections.get(name);
    }

    /**
     * @return A list of all the sections currently in the profiler
     */
    public ProfilerSection[] sections() {
        Collection<ProfilerSection> values = sections.values();
        return values.toArray(new ProfilerSection[values.size()]);
    }

    /**
     * The name of the profiler, set in the Constructor.
     */
    public String name() {
        return name;
    }

    /**
     * Export the profiler log to file, using the name of the profiler as the filename.
     * Saved in standard JSON pretty print format
     */
    public void export() {
        export(null);
    }

    /**
     * Export the profiler log to file. Filename format goes #profilername#-#param1#.json
     * Saved in standard JSON pretty print format.
     */
    public void export(String s) {
        String json = JsonWriter.indent("\t").string().value(toJSON()).done();
        try {
            File dir = new File(ToastBootstrap.toastHome, "system/profiler");
            dir.mkdirs();
            File exp = new File(dir, name + ".json");
            if (s == null) {
                if (exp.exists()) {
                    File last = new File(dir.getAbsoluteFile(), name + "_old.json");
                    if (last.exists())
                        last.delete();
                    exp.renameTo(last);
                    exp = new File(dir, name + ".json");
                }
            } else {
                exp = new File(dir, name + "-" + s + ".json");
                if (exp.exists()) exp.delete();
            }
            FileWriter writer = new FileWriter(exp);
            writer.write(json);
            writer.close();
        } catch (Exception e) {
            log.error("Exception whilst exporting profiler log: " + e);
            log.exception(e);
        }
    }

    /**
     * Convert the profiler to a JSON Object. All times are stored in Nanoseconds.
     */
    public JsonObject toJSON() {
        JsonBuilder<JsonObject> obj = JsonObject.builder();

        for (ProfilerSection section : sections()) {
            if (section != null) {
                JsonObject object = section.toJSON();

                if (section.name() != null && object != null)
                    obj.value(section.name(), object);
            }
        }
        return obj.done();
    }

}
