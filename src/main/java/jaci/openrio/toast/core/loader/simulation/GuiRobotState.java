package jaci.openrio.toast.core.loader.simulation;

import jaci.openrio.toast.lib.state.RobotState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;

/**
 * A GUI Element for switching the Robot State during Simulation
 *
 * @author Jaci
 */
public class GuiRobotState extends JComponent implements MouseListener {

    RobotState state;
    int x, y;
    int width = 100;
    int height = 30;

    static final Color bgPassive = new Color(20, 20, 20);
    static final Color bgClicked = new Color(11, 11, 11);
    static final Color fgPassive = new Color(100, 100, 100);

    public GuiRobotState(int x, int y, RobotState state, JPanel parent) {
        this.x = x;
        this.y = y;
        this.state = state;
        this.setBounds(x, y, width, height);
        parent.add(this);
        parent.addMouseListener(this);

        this.setBackground(bgPassive);
        this.setForeground(fgPassive);
    }

    /**
     * Paints the component. This calls the super as well as calling the second 'paint()' method that will
     * draw the button based on the state given in {@link SimulationData}
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        paint((Graphics2D) g);
    }

    /**
     * Paints the object with the new Graphics2D object. This takes data from the {@link SimulationData} class in order
     * to paint the correct button.
     */
    public void paint(Graphics2D g) {
        g.setColor(this.getBackground());
        g.fillRect(0, 0, width, height);
        g.setColor(SimulationData.currentState == state ? new Color(100, 170, 100) : this.getForeground());
        FontMetrics metrics = g.getFontMetrics();
        Rectangle2D textBounds = metrics.getStringBounds(state.state, g);
        g.drawString(state.state, (float) ((width - textBounds.getWidth()) / 2), (float) ((height - textBounds.getHeight()) / 2 + metrics.getAscent()));
    }

    /**
     * Does a check for whether or not the Mouse exists within the bounds of the button.
     */
    public boolean inBounds(MouseEvent e) {
        return e.getX() > x && e.getX() < x + width && e.getY() > y && e.getY() < y + height;
    }

    /**
     * Apply this button
     */
    public void apply() {
        SimulationData.currentState = this.state;
        repaint();
    }

    /**
     * Stub Method - Not Used
     */
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    /**
     * Serves to change the colour of the button to indicate it being depressed.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (inBounds(e)) {
            this.setBackground(bgClicked);
        }
        this.repaint();
    }

    /**
     * Invokes the new state change
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (inBounds(e))
            apply();

        this.setBackground(bgPassive);
        this.repaint();
    }

    /**
     * Stub Method - Not Used
     */
    @Override
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * Stub Method - Not Used
     */
    @Override
    public void mouseExited(MouseEvent e) {
    }
}
