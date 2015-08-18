package jaci.openrio.toast.lib.profiler;

import com.grack.nanojson.JsonBuilder;
import com.grack.nanojson.JsonObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ProfilerSection {

    String name;
    Profiler profiler;
    ProfilerSection parent;

    HashMap<String, ProfilerSection> sections = new LinkedHashMap<>();
    HashMap<String, ProfilerEntity> entities = new LinkedHashMap<>();

    public ProfilerSection(String name, Profiler parent) {
        this.name = name;
        this.profiler = parent;
    }

    protected ProfilerSection setChild(ProfilerSection section) {
        this.parent = section;
        return this;
    }

    public String name() {
        return name;
    }

    public ProfilerSection section(String name) {
        if (!sections.containsKey(name)) {
            sections.put(name, new ProfilerSection(name, profiler).setChild(this));
        }
        return sections.get(name);
    }

    public ProfilerSection[] sections() {
        Collection<ProfilerSection> values = sections.values();
        return values.toArray(new ProfilerSection[values.size()]);
    }

    public void start(String name) {
        if (!entities.containsKey(name)) {
            entities.put(name, new ProfilerEntity(name));
        }
        entities.get(name).start();
    }

    public void stop(String name) {
        entities.get(name).stop();
    }

    public void pushEntity(ProfilerEntity entity) {
        entities.put(entity.name(), entity);
    }

    public ProfilerEntity entity(String name) {
        if (!entities.containsKey(name)) {
            entities.put(name, new ProfilerEntity(name));
        }
        return entities.get(name);
    }

    public ProfilerEntity[] entities() {
        Collection<ProfilerEntity> values = entities.values();
        return values.toArray(new ProfilerEntity[values.size()]);
    }

    public long totalTime() {
        long time = 0;
        for (ProfilerSection sec : sections()) {
            time += sec.totalTime();
        }
        for (ProfilerEntity ent : entities()) {
            time += ent.getDuration();
        }
        return time;
    }

    public long totalTimeMS() {
        return totalTime() / 1000000;
    }

    public long totalTimeS() {
        return totalTimeMS() / 1000;
    }

    public JsonObject toJSON() {
        JsonBuilder<JsonObject> obj = JsonObject.builder();

        for (ProfilerSection section : sections()) {
            obj.value(section.name(), section.toJSON());
        }
        for (ProfilerEntity entity : entities()) {
            obj.value(entity.name(), entity.getDuration());
        }
        return obj.done();
    }

}
