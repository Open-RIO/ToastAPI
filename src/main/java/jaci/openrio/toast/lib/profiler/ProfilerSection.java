package jaci.openrio.toast.lib.profiler;

import com.grack.nanojson.JsonBuilder;
import com.grack.nanojson.JsonObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * A section for the Profiler. This is used for organising the profiler into easy-to-read sections for user analysis.
 * Profiler Statistics can be viewed in graph form on http://dev.imjac.in/toast/profiler/
 *
 * @author Jaci
 */
public class ProfilerSection {

    String name;
    Profiler profiler;
    ProfilerSection parent;

    HashMap<String, ProfilerSection> sections = new LinkedHashMap<>();
    HashMap<String, ProfilerEntity> entities = new LinkedHashMap<>();

    /**
     * Create a new ProfilerSection, with a profiler parent
     * @param name      The name for the section
     * @param parent    The profiler to use as the parent
     */
    public ProfilerSection(String name, Profiler parent) {
        this.name = name;
        this.profiler = parent;
    }

    /**
     * Set this section as a child of the section provided in the parameters
     * @param section       The desired parent section
     */
    protected ProfilerSection setChild(ProfilerSection section) {
        this.parent = section;
        return this;
    }

    /**
     * The name of the section provided in the constructor
     */
    public String name() {
        return name;
    }

    /**
     * Create a new child section. This will automatically add the section as a child. If this section already exists, no
     * new section will be created.
     * @param name  The desired name for the section. Short n' sweet.
     */
    public ProfilerSection section(String name) {
        if (!sections.containsKey(name)) {
            sections.put(name, new ProfilerSection(name, profiler).setChild(this));
        }
        return sections.get(name);
    }

    /**
     * @return A list of all the sections currently in the section
     */
    public ProfilerSection[] sections() {
        Collection<ProfilerSection> values = sections.values();
        return values.toArray(new ProfilerSection[values.size()]);
    }

    /**
     * Start a new ProfilerEntity under this section. An Entity is what actually measures the time between the starting and
     * stopping of the entity. This will be added to the profiler. Alternatively, you can instantiate a new ProfilerEntity
     * yourself and add it with {@link #pushEntity(ProfilerEntity)}
     */
    public void start(String name) {
        if (!entities.containsKey(name)) {
            entities.put(name, new ProfilerEntity(name));
        }
        entities.get(name).start();
    }

    /**
     * Stop a ProfilerEntity that has been added to the section.
     */
    public void stop(String name) {
        entities.get(name).stop();
    }

    /**
     * Push an Entity to the section. This is like {@link #start(String)} and {@link #stop(String)} methods, but for Entities
     * that you have created yourself.
     */
    public void pushEntity(ProfilerEntity entity) {
        entities.put(entity.name(), entity);
    }

    /**
     * Get a ProfilerEntity that is currently in the section
     */
    public ProfilerEntity entity(String name) {
        if (!entities.containsKey(name)) {
            entities.put(name, new ProfilerEntity(name));
        }
        return entities.get(name);
    }

    /**
     * @return A list of all the entities currently in the section
     */
    public ProfilerEntity[] entities() {
        Collection<ProfilerEntity> values = entities.values();
        return values.toArray(new ProfilerEntity[values.size()]);
    }

    /**
     * Get the total time of the section. This is the sum of all the entities and sections currently in this section.
     * This value is measured in Nanoseconds.
     */
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

    /**
     * Get the total time of the section. This is the sum of all the entities and sections currently in this section.
     * This value is measured in Milliseconds.
     */
    public long totalTimeMS() {
        return totalTime() / 1000000;
    }

    /**
     * Get the total time of the section. This is the sum of all the entities and sections currently in this section.
     * This value is measured in Seconds.
     */
    public long totalTimeS() {
        return totalTimeMS() / 1000;
    }

    /**
     * Convert the section to a JSON Object. All times are stored in Nanoseconds.
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
        for (ProfilerEntity entity : entities()) {
            if (entity != null && entity.name() != null)
                obj.value(entity.name(), entity.getDuration());
        }
        return obj.done();
    }

}
