package jaci.openrio.toast.core.loader;

import java.io.File;
import java.util.ArrayList;

public class ToastModuleCandidate {

    File moduleFile;
    ArrayList<String> classMembers = new ArrayList<>();

    public void setFile(File file) {
        moduleFile = file;
    }

    public void addClassEntry(String classEntry) {
        classMembers.add(classEntry.substring(0, classEntry.lastIndexOf('.')).replace('/', '.'));
    }

    public String[] getClassEntries() {
        return classMembers.toArray(new String[0]);
    }

}
