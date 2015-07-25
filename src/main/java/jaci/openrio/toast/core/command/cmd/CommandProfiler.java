package jaci.openrio.toast.core.command.cmd;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jaci.openrio.toast.core.command.AbstractCommand;
import jaci.openrio.toast.lib.log.Logger;
import jaci.openrio.toast.lib.math.MathHelper;
import jaci.openrio.toast.lib.profiler.Profiler;
import jaci.openrio.toast.lib.profiler.ProfilerEntity;
import jaci.openrio.toast.lib.profiler.ProfilerSection;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class CommandProfiler extends AbstractCommand {

    @Override
    public String getCommandName() {
        return "profiler";
    }

    @Override
    public void invokeCommand(int argLength, String[] args, String command) {
        Profiler inst = Profiler.INSTANCE;
        Logger log = inst.log();

        List<String> actualArgs = new LinkedList<>();
        List<String> switches = new LinkedList<>();
        for (String a : args) {
            if (a.startsWith("-")) switches.add(a);
            else actualArgs.add(a);
        }

        if (switches.contains("--json") || switches.contains("-j")) {
            Gson gson_engine = new GsonBuilder().setPrettyPrinting().create();
            String json = gson_engine.toJson(inst.toJSON());
            System.out.println(json);
            return;
        }

        if (switches.contains("--export") || switches.contains("-e")) {
            if (switches.contains("-o"))
                inst.export();
            else {
                if (actualArgs.size() > 0) {
                    inst.export(actualArgs.get(0));
                } else
                    inst.export(new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss").format(new Date()));
            }
            return;
        }
        log.info("<-- MAIN PROFILER DUMP -->");
        if (actualArgs.size() == 0) {
            for (ProfilerSection section : inst.sections()) {
                printSection(section, 0, log);
            }
        } else {
            ProfilerSection lastSec = null;
            for (String arg : actualArgs) {
                if (lastSec == null) {
                    lastSec = inst.section(arg);
                } else {
                    lastSec = lastSec.section(arg);
                }
            }
            printSection(lastSec, 0, log);
        }
        log.info("<-- END PROFILER DUMP -->");
    }

    public void printSection(ProfilerSection section, int level, Logger log) {
        String ind = "";
        for (int i = 0; i < level; i++) { ind += " "; }
        log.info(ind + "> " + section.name());
        for (ProfilerSection sec : section.sections()) {
            printSection(sec, level + 1, log);
        }
        for (ProfilerEntity ent : section.entities()) {
            log.info(ind + "\t- " + ent.name() + ": " + MathHelper.round(ent.getDurationMS(), 3) + "ms");
        }
    }
}
