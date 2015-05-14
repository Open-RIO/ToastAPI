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
    boolean coreplugin;
    String pluginClass;
    boolean bypass;
    String bypassClass;

    public void setFile(File file) {
        moduleFile = file;
    }

    public void setCorePlugin(boolean state, String pluginClass) {
        coreplugin = state;
        this.pluginClass = pluginClass;
    }

    public void setBypass(boolean state, String bypassClass) {
        bypass = state;
        this.bypassClass = bypassClass;
    }

    /**
     * Is this Candidate a CorePlugin?
     * CorePlugins are loaded at the same time that Toast is, meaning they are loaded
     * before WPILib. This is for modules that need to be instantiated early, and are
     * identified by the 'Toast-Core-Plugin-Class' entry in the MANIFEST.MF file of the
     * module.
     */
    public boolean isCorePlugin() {
        return coreplugin;
    }

    public String getCorePluginClass() {
        return pluginClass;
    }

    /**
     * Does this module bypass class searching? If the 'Toast-Plugin-Class' entry exists in
     * the MANIFEST.MF file of this module, the RobotLoader will skip searching for the
     * ToastModule class and jump directly to the one specified in this entry. This makes loading
     * times shorter for larger modules, but the user has to make sure the Module main file
     * is constant.
     */
    public boolean isBypass() {
        return bypass;
    }

    public String getBypassClass() {
        return bypassClass;
    }

    public File getModuleFile() {
        return moduleFile;
    }

    public void addClassEntry(String classEntry) {
        classMembers.add(classEntry.substring(0, classEntry.lastIndexOf('.')).replace('/', '.').replace('\\', '.'));
    }

    public String[] getClassEntries() {
        return classMembers.toArray(new String[0]);
    }

}
