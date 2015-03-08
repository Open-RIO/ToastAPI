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

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        paint((Graphics2D) g);
    }

    public void paint(Graphics2D g) {
        g.setColor(this.getBackground());
        g.fillRect(0, 0, width, height);
        g.setColor(SimulationData.currentState == state ? new Color(100, 170, 100) : this.getForeground());
        FontMetrics metrics = g.getFontMetrics();
        Rectangle2D textBounds = metrics.getStringBounds(state.state, g);
        g.drawString(state.state, (float) ((width - textBounds.getWidth()) / 2), (float) ((height - textBounds.getHeight()) / 2 + metrics.getAscent()));
    }

    public boolean inBounds(MouseEvent e) {
        return e.getX() > x && e.getX() < x + width && e.getY() > y && e.getY() < y + height;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (inBounds(e)) {
            this.setBackground(bgClicked);
        }
        this.repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (inBounds(e))
            SimulationData.currentState = this.state;

        this.setBackground(bgPassive);
        this.repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
