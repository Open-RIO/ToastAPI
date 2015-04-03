package jaci.openrio.toast.core.loader.simulation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;

/**
 * A GUI Element for a button in Simulation
 *
 * @author Jaci
 */
public class GuiButton extends JComponent implements MouseListener {

    boolean state;
    boolean toggle;
    String label;
    int x, y;
    int width;
    int height;
    ButtonCallback callback;

    static Color bgPassive = new Color(20, 20, 20);
    static Color bgClicked = new Color(11, 11, 11);
    Color fgPassive = new Color(100, 100, 100);
    Color fgActive = new Color(100, 170, 100);

    public GuiButton(int x, int y, int width, int height, boolean state, String label, boolean toggle, JPanel parent) {
        this.x = x;
        this.y = y;
        this.state = state;
        this.label = label;
        this.toggle = toggle;
        this.width = width;
        this.height = height;
        this.setBounds(x, y, width, height);
        parent.add(this);
        parent.addMouseListener(this);

        this.setBackground(bgPassive);
        this.setForeground(fgPassive);
    }

    public void setActiveColor(Color color) {
        this.fgActive = color;
    }

    public void setInactiveColor(Color color) {
        this.fgPassive = color;
    }

    public void setCallback(ButtonCallback cb) {
        this.callback = cb;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        paint((Graphics2D) g);
    }

    public void paint(Graphics2D g) {
        g.setColor(this.getBackground());
        g.fillRect(0, 0, width, height);
        g.setColor(state ? fgActive : fgPassive);
        g.setFont(this.getFont());
        FontMetrics metrics = g.getFontMetrics();
        Rectangle2D textBounds = metrics.getStringBounds(label, g);
        g.drawString(label, (float) ((width - textBounds.getWidth()) / 2), (float) ((height - textBounds.getHeight()) / 2 + metrics.getAscent()));
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
        if (inBounds(e)) {
            if (callback != null)
                callback.onClick();
            if (toggle) {
                this.state = !state;
                if (callback != null)
                    callback.onToggle(state);
            }
        }

        this.setBackground(bgPassive);
        this.repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    public static interface ButtonCallback {
        public void onClick();
        public void onToggle(boolean state);
    }
}
