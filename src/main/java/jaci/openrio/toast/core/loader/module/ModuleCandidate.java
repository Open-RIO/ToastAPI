package jaci.openrio.toast.core.loader.module;

import java.io.File;
import java.util.Vector;

/**
 * A Candidate for a Module to be loaded by Toast. These are containers for Modules that are found, but not instantiated yet.
 * Because of this, we can't tell what the Module is, only the Filename, hence the term 'Candidate'. Candidates are identified
 * and stored during initialization of the {@link jaci.openrio.toast.core.loader.RobotLoader} and are used later. All the classes
 * in the candidate are iterated over to see if they are a valid Module, and then they are passed to a
 * {@link ModuleContainer} to be instantiated and thus the loading is finished.
 *
 * @author Jaci
 */
public class ModuleCandidate {

    File moduleFile;
    Vector<String> classMembers = new Vector<>();

    public void setFile(File file) {
        moduleFile = file;
    }

    public File getModuleFile() {
        return moduleFile;
    }

    public void addClassEntry(String classEntry) {
        classMembers.add(classEntry.substring(0, classEntry.lastIndexOf('.')).replace('/', '.'));
    }

    public String[] getClassEntries() {
        return classMembers.toArray(new String[0]);
    }

}
