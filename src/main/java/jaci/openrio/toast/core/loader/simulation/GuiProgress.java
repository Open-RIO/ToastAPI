package jaci.openrio.toast.core.loader.simulation;

import javax.swing.*;
import java.awt.*;

public class GuiProgress extends JComponent {

    int x,y,width,height;
    double val;

    JComponent parent;

    static Color grey = new Color(80, 80, 80);

    public GuiProgress(int x, int y, int width, int height, JComponent parent) {
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
     * Set value
     */
    public void setValue(double value) {
        val = value;
        repaint();
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
        g.setColor(val > 0 ? this.getForeground() : this.getBackground());

        if (val >= 0)
            g.fillRect(width / 2, 0, (int) (val * width / 2), height);
        else        // OS X Can't draw negative rectangles, so we need to shift it over
            g.fillRect(width / 2 + (int)(val * width / 2), 0, (int)(-val * width / 2), height);

        g.setColor(grey);
        g.fillRect(width / 2 - 1, 0, 2, height);
    }

}
