package jaci.openrio.toast.core.loader.module;

import jaci.openrio.toast.lib.util.ToastUtil;

import java.io.File;
import java.util.List;
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
    List<String> classMembers = new Vector<>();
    boolean coreplugin;
    String pluginClass;
    boolean bypass;
    String bypassClass;
    boolean isWrapper;
    String wrapperClazz;

    /**
     * Set the file (.jar) of the module candidate.
     */
    public void setFile(File file) {
        moduleFile = file;
    }

    /**
     * Set true if this candidate is noted as a CorePlugin, and therefore should be
     * loaded early.
     */
    public void setCorePlugin(boolean state, String pluginClass) {
        coreplugin = state;
        this.pluginClass = pluginClass;
    }

    /**
     * Set true if the Toast-Plugin-Class manifest attribute is present, bypassing the
     * searching of a ToastModule file to save CPU Time on larger modules.
     */
    public void setBypass(boolean state, String bypassClass) {
        bypass = state;
        this.bypassClass = bypassClass;
    }

    /**
     * Set this to a RobotBase wrapper class
     */
    public void setWrapper(boolean state, String clazz) {
        isWrapper = state;
        wrapperClazz = clazz;
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

    /**
     * Get the class name of the Core Plugin instance.
     */
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

    /**
     * Get the class name of the Bypass class (the ToastModule class)
     */
    public String getBypassClass() {
        return bypassClass;
    }

    /**
     * Returns true if this is a RobotBase wrapper class
     */
    public boolean isWrapper() {
        return isWrapper;
    }

    /**
     * Get the RobotBase class this is acting as a wrapper of.
     */
    public String getWrapperClass() {
        return wrapperClazz;
    }

    /**
     * Get the file (.jar) of the Module Candidate
     */
    public File getModuleFile() {
        return moduleFile;
    }

    /**
     * Add a class entry to the candidate. This is a list of all the .class files a module contains
     * to aid in culprit detection and class loading. This is not called if it is a bypass.
     */
    public void addClassEntry(String classEntry) {
        classMembers.add(classEntry.substring(0, classEntry.lastIndexOf('.')).replace('/', '.').replace('\\', '.'));
    }

    /**
     * Get the list of class entries this module contains. This fetches the greatest common package(s)
     */
    public String[] getClassEntries() {
        return classMembers.toArray(new String[0]);
    }

    /**
     * Cleanup the ClassMembers array to free up some RAM for particularly large modules.
     */
    public void freeMemory() {
        if (classMembers.size() > 0) {
            classMembers = ToastUtil.findCommonPkgs(classMembers);
        }
    }

}
