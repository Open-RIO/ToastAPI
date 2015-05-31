package jaci.openrio.toast.core.loader.module;

import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.core.loader.RobotLoader;
import jaci.openrio.toast.core.loader.annotation.Branch;
import jaci.openrio.toast.lib.module.ToastModule;

/**
 * The container object for a Module that has been identified, verified and is ready to be loaded. Toast will store the
 * .class object for a module if it is a subtype of {@link jaci.openrio.toast.lib.module.ToastModule} and will be instantiated
 * during the {@link #construct} phase and then the module is ready to be handed
 * off to Toast to be used.
 * <p/>
 * Keep in mind this container doesn't subclass the {@link jaci.openrio.toast.lib.module.ToastModule} class, but the
 * underlying module instance can be accessed through {@link #getModule}
 *
 * @author Jaci
 */
public class ModuleContainer {

    Class<? extends ToastModule> moduleClass;
    ToastModule moduleInstance;

    String name = "{undefined}";
    String version = "{undefined}";

    ModuleStorage moduleStorage;
    ModuleCandidate moduleCandidate;

    public ModuleContainer(Class<? extends ToastModule> moduleClass, ModuleCandidate candidate) {
        this.moduleClass = moduleClass;
        this.moduleCandidate = candidate;
    }

    /**
     * Get the {@link jaci.openrio.toast.core.loader.module.ModuleCandidate} for this container
     */
    public ModuleCandidate getCandidate() {
        return moduleCandidate;
    }

    /**
     * Construct the underlying {@link jaci.openrio.toast.lib.module.ToastModule} object and retrieve the
     * data from it. This is handled by toast and shouldn't be called
     */
    public void construct() throws IllegalAccessException, InstantiationException {
        moduleInstance = moduleClass.newInstance();
        name = moduleInstance.getModuleName();
        version = moduleInstance.getModuleVersion();

        moduleStorage = new ModuleStorage(name);
        moduleInstance._moduleContainer = this;
        moduleInstance.onConstruct();
    }

    public void resolve_branches() {
        if (moduleClass.isAnnotationPresent(Branch.class)) {
            for (Branch br : moduleClass.getAnnotationsByType(Branch.class)) {
                String clazz = br.branch();
                try {
                    if (ModuleManager.moduleExists(br.dependency()) || RobotLoader.classExists(br.dependency())) {
                        Class branch = Class.forName(clazz);
                        if (br.immediate())
                            branch.getMethod(br.method()).invoke(null);
                        else
                            RobotLoader.queuedPrestart.add(branch.getMethod(br.method()));
                    }
                } catch (Exception e) {
                    Toast.log().error("Could not resolve branch: " + clazz + " for module: " + getName());
                }
            }
        }
    }

    /**
     * Get the name of the underlying {@link jaci.openrio.toast.lib.module.ToastModule} instance
     */
    public String getName() {
        return name;
    }

    /**
     * Get the version of the underlying {@link jaci.openrio.toast.lib.module.ToastModule} instance
     */
    public String getVersion() {
        return version;
    }

    /**
     * Get a standard concatenation of the Name and Version of the container
     */
    public String getDetails() {
        return name + "@" + version;
    }

    /**
     * Get the underlying {@link jaci.openrio.toast.lib.module.ToastModule} instance
     */
    public ToastModule getModule() {
        return moduleInstance;
    }

    /**
     * Get the {@link ModuleStorage} object for this container.
     * <p/>
     * ModuleStorage is a means by which Module authors can store data and have it accessible to other
     * modules. This is a black-board type implementation, where objects are stored by identifier.
     */
    public ModuleStorage getModuleStorage() {
        return moduleStorage;
    }

    public String toString() {
        return String.format("ToastModuleContainer[name:%s, version:%s, class:%s]", name, version, moduleClass);
    }
}
