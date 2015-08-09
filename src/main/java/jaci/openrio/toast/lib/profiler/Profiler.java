package jaci.openrio.toast.lib.profiler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import jaci.openrio.toast.core.ToastBootstrap;
import jaci.openrio.toast.lib.log.Logger;

import java.io.File;
import java.io.FileWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * A Time Cop. This keeps track of the amount of time everything takes to complete during initialization.
 *
 * @author Jaci
 */
public class Profiler {

    public static final Profiler INSTANCE = new Profiler("Main");

    String name;
    Logger log;

    HashMap<String, ProfilerSection> sections = new LinkedHashMap<>();

    public Profiler(String name) {
        this.name = name;
    }

    public ProfilerSection section(String name) {
        if (!sections.containsKey(name)) {
            sections.put(name, new ProfilerSection(name, this));
        }
        return sections.get(name);
    }

    public ProfilerSection[] sections() {
        Collection<ProfilerSection> values = sections.values();
        return values.toArray(new ProfilerSection[values.size()]);
    }

    public String name() {
        return name;
    }

    private void newlog() {
        if (name.equals("Main"))
            this.log = new Logger("Profiler", Logger.ATTR_DEFAULT);
        else
            this.log = new Logger("Profiler|" + name, Logger.ATTR_DEFAULT);
    }

    public Logger log() {
        if (log == null) newlog();
        return log;
    }

    public void export() {
        export(null);
    }

    public void export(String s) {
        Gson gson_engine = new GsonBuilder().setPrettyPrinting().create();
        String json = gson_engine.toJson(toJSON());
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

    public JsonObject toJSON() {
        JsonObject obj = new JsonObject();
        for (ProfilerSection section : sections()) {
            obj.add(section.name(), section.toJSON());
        }
        return obj;
    }

}
