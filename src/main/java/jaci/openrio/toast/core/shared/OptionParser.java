package jaci.openrio.toast.core.shared;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class OptionParser {

    public static class Handler {
        public String flagS, flagL, desc;
        public Consumer<List<String>> callback;
        public int expected;
    }

    public List<Handler> handlers;

    public OptionParser(boolean exitOnHelp) {
        handlers = new ArrayList<>();
        on("-h", "--help", "Display Help Message", 0, (list) -> {
            System.out.println(helpMessage());
            if (exitOnHelp) System.exit(0);
        });
    }

    public List<String> parse(String[] args) {
        List<String> passable = new ArrayList<>();
        final int[] index = new int[1];         // Needs to be a final array to be modified inside loop
        for (index[0] = 0; index[0] < args.length; index[0]++) {
            int startI = index[0];
            String argument = args[index[0]];
            handlers.stream().forEach(handler -> {
                if (argument.equals(handler.flagL) || argument.equals(handler.flagS)) {
                    int exp = handler.expected;
                    List<String> cbPass = new ArrayList<String>();
                    for (int expIdx = startI + 1; expIdx < (startI + 1 + exp) && expIdx < args.length; expIdx++) {
                        cbPass.add(args[expIdx]);
                    }
                    handler.callback.accept(cbPass);
                    index[0] += exp;
                }
            });
            if (startI == index[0]) passable.add(argument);
        }
        return passable;
    }

    public List<String> parse(String args, String delim) {
        return parse(args.split(delim));
    }

    public List<String> parse(String args) {
        return parse(args, " ");
    }

    public void on(String flagShort, String flagLong, String flagDescription, int expected, Consumer<List<String>> callback) {
        Handler h = new Handler();
        h.flagS = flagShort;
        h.flagL = flagLong;
        h.desc = flagDescription;
        h.callback = callback;
        h.expected = expected;
        handlers.add(h);
    }

    public void on(String flag, String description, int expected, Consumer<List<String>> callback) {
        on(null, flag, description, expected, callback);
    }

    public String helpMessage() {
        List<String> list = new ArrayList<>();

        for (Handler handler : handlers) {
            if (handler.flagS != null) {
                list.add(String.format("\t %-7s %15s \t-- %10s", handler.flagS, handler.flagL, handler.desc));
            } else {
                list.add(String.format("\t %-7s %15s \t-- %10s", "", handler.flagL, handler.desc));
            }
        }

        return String.join("\n", list);
    }

}
