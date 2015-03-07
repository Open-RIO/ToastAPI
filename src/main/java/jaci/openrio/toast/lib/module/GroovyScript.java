package jaci.openrio.toast.lib.module;

/**
 * A class extended by Groovy Scripts wishing to be loaded as a module.
 * These scripts are placed in toast/groovy and should extend this class and
 * override the methods
 *
 * @author Jaci
 */
public class GroovyScript {

    /**
     * Called when the script is loaded into the classpath
     */
    public void loadScript() {}

    /**
     * Called before the robot is ready to be activated
     */
    public void prestartRobot() {}

    /**
     * Called when the robot is ready to be activated
     */
    public void startRobot() {}

}
