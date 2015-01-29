package jaci.openrio.toast.lib.listener;

/**
 * A base interface for Robot Base classes.
 * To load this class, it must be defined in the Manifest under the
 * 'Toast-Robot' property.
 *
 * @author Jaci
 */
public interface Robot {

    /**
     * Called before the Robot is noted as 'Ready'. Setup
     * should be done here
     */
    public void prestart();

    /**
     * Called when the Robot is ready to go
     */
    public void startCompetition();

}
