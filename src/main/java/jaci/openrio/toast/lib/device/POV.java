package jaci.openrio.toast.lib.device;

/**
* An enumeration of possible POV directions.
 *
 * @author Jaci
*/
public enum POV {
    NONE(-1), UP(0), UP_RIGHT(45), RIGHT(90), DOWN_RIGHT(135), DOWN(180), DOWN_LEFT(225), LEFT(270), UP_LEFT(315);

    public int direction;

    POV(int direction) {
        this.direction = direction;
    }
}