package jaci.openrio.toast.core.loader.simulation;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

/**
 * Custom Scroll Bar GUI Painter
 */
public class GuiScrollbar extends BasicScrollBarUI {

    static Color color = new Color(30, 30, 30);
    static Color bg = new Color(11, 11, 11);

    @Override
    protected JButton createDecreaseButton(int orientation) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension());
        return button;
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension());
        return button;
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) { }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
        Graphics2D g2 = (Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        JScrollBar sb = (JScrollBar)c;
        c.setBackground(bg);
        if(!sb.isEnabled() || r.width>r.height) {
            return;
        }
        g2.setPaint(color);
        g2.fillRoundRect(r.x-1,r.y,r.width,r.height,10,10);
        g2.dispose();
    }

    @Override
    protected void setThumbBounds(int x, int y, int width, int height) {
        super.setThumbBounds(x, y, width, height);
        scrollbar.repaint();
    }

}
