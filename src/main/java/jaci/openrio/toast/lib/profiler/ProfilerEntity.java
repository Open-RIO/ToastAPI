package jaci.openrio.toast.lib.profiler;

public class ProfilerEntity {

    long start_time;
    long end_time;
    long total_time;
    String name;

    public ProfilerEntity() { }

    public void setName(String n) {
        this.name = n;
    }

    public ProfilerEntity(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    public ProfilerEntity start() {
        start_time = System.nanoTime();
        return this;
    }

    public void stop() {
        end_time = System.nanoTime();
        total_time = end_time - start_time;
    }

    public long getDuration() {
        return total_time;
    }

    public long getDurationMS() {
        return getDuration() / 1000000;
    }

    public long getDurationS() {
        return getDurationMS() / 1000;
    }

}
