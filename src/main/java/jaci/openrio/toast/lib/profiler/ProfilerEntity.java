package jaci.openrio.toast.lib.profiler;

/**
 * The ProfilerEntity is a class designed to measure the time taken between the .start() and .stop() methods are called,
 * and is designed to be used with the {@link Profiler} and {@link ProfilerSection} classes.
 *
 * @author Jaci
 */
public class ProfilerEntity {

    long start_time;
    long end_time;
    long total_time;
    String name;

    /**
     * A default, empty constructor for those who wish to call {@link #setName(String)} later
     */
    public ProfilerEntity() { }

    /**
     * Set the name of the ProfilerSection. This is used to identify what exactly you are trying to measure.
     */
    public void setName(String n) {
        this.name = n;
    }

    /**
     * Create a new ProfilerEntity with the given name
     */
    public ProfilerEntity(String name) {
        this.name = name;
    }

    /**
     * Get the name of the Entity as set in the constructor or setName() method. May be null.
     */
    public String name() {
        return name;
    }

    /**
     * Start the entity. Call this when you are ready to start measuring. Returns itself for easy chaining.
     */
    public ProfilerEntity start() {
        start_time = System.nanoTime();
        return this;
    }

    /**
     * Stop the entity. Call this when the thing you are measuring is complete. This will be measured in nanoseconds.
     */
    public void stop() {
        end_time = System.nanoTime();
        total_time = end_time - start_time;
    }

    /**
     * Get the duration of the profiler entity. This is measured in nanoseconds.
     */
    public long getDuration() {
        return total_time;
    }

    /**
     * Get the duration of the profiler entity. This is measured in milliseconds.
     */
    public long getDurationMS() {
        return getDuration() / 1000000;
    }

    /**
     * Get the duration of the profiler entity. This is measured in seconds.
     */
    public long getDurationS() {
        return getDurationMS() / 1000;
    }

}
