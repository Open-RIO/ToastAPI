package jaci.openrio.toast.lib.module;

import jaci.openrio.toast.core.loader.module.ModuleContainer;

/**
 * The Base class for all Toast Modules. This should be extending instead of {@link edu.wpi.first.wpilibj.RobotBase} if you wish
 * for your module to work with Toast.
 *
 * @author Jaci
 */
public abstract class ToastModule {

    /**
     * The {@link jaci.openrio.toast.core.loader.module.ModuleContainer} object for this Module. This is set before
     * {@link #onConstruct} by the {@link jaci.openrio.toast.core.loader.RobotLoader}
     */
    public ModuleContainer _moduleContainer;

    /**
     * Get a Friendly name for the Module. This is what the Module is referenced by. This should be unique
     * per module
     * @return A unique, friendly name for the module
     */
    public abstract String getModuleName();

    /**
     * Get a Version for the Module. This should change as the Module is updated. If you Module is only being used by
     * your team, this isn't really necessary. This is here for distributed APIs and modules.
     * @return The version of the module
     */
    public abstract String getModuleVersion();

    /**
     * Called on 'Pre-Initialization' of the robot. This is called before the Robot is indicated as 'ready to go'. Inputs
     * and Outputs should be configured here. This method should not have much over-head
     */
    public abstract void prestart();

    /**
     * Called on 'Initialization' of the robot. This is called after the Robot is indicated as 'ready to go'. Things like
     * Network Communications and Camera Tracking should be initialized here.
     */
    public abstract void start();

    /**
     * Called when this Module has been discovered and constructed. This method isn't usually used for much, but
     * can be useful for triggering things before the robot is in Pre-Initialization.
     */
    public void onConstruct() { }
}
