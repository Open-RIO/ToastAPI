package jaci.openrio.toast.core.loader.simulation;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class GuiRelay extends JComponent {

    boolean isForward;
    boolean isReverse;

    int x,y,width,height;

    JPanel parent;

    public GuiRelay(int x, int y, int width, int height, JPanel parent) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.parent = parent;

        this.setBounds(x, y, width, height);
        parent.add(this);

        this.setBackground(new Color(180, 20, 20));
        this.setForeground(new Color(20, 180, 20));
    }

    /**
     * Set to true if this relay is in forward mode
     */
    public void setForward(boolean on) {
        this.isForward = on;
        this.repaint();
    }

    /**
     * Set to true if this relay is in Reverse Mode
     */
    public void setReverse(boolean on) {
        this.isReverse = on;
        this.repaint();
    }

    /**
     * Paint the component on the GUI
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        paint((Graphics2D) g);
    }

    /**
     * Paints the component with the Graphics2D instance. This is used to paint our graphics manually.
     * This is called once per refresh
     */
    public void paint(Graphics2D g) {
        g.setColor(this.getBackground());
        g.fillRect(0, 0, width, height);
        g.setColor(this.getForeground());
        if (isForward)
            g.fillRect(0, 0, width, height / 2);
        if (isReverse)
            g.fillRect(0, height/2, width, height / 2);
    }

}
