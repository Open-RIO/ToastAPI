package jaci.openrio.toast.core.loader.simulation;

import jaci.openrio.toast.lib.math.MathHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A number spinner GUI Element for Simulation
 *
 * @author Jaci
 */
public class GuiNumberSpinner {

    int x, y;
    double val, step, min, max;

    JButton plus;
    JButton minus;
    JLabel label;

    JComponent parent;
    Callback cb;

    public GuiNumberSpinner(int x, int y, double value, double step, double min, double max, boolean editable, JComponent parent) {
        this.x = x; this.y = y; this.val = MathHelper.round(value, 2); this.step = step; this.min = min; this.max = max;

        this.parent = parent;

        plus = new JButton("+");
        plus.setBounds(x, y, 10, 10);
        plus.setFont(new Font("Arial", 0, 9));

        minus = new JButton("-");
        minus.setBounds(x + 40, y, 10, 10);
        minus.setFont(new Font("Arial", 0, 9));

        label = new JLabel(String.valueOf(val), SwingConstants.CENTER);
        label.setBounds(x + 10, y, 30, 10);
        label.setFont(new Font("Arial", 0, 10));
        label.setForeground(new Color(180, 180, 180));
        parent.add(label);

        setEditable(editable);
        attachHooks();
        refresh();
    }

    public void setCallback(Callback cb) {
        this.cb = cb;
    }

    public void setValue(double val) {
        this.val = MathHelper.round(Math.max(-1, Math.min(1, val)), 2);
        refresh();
    }

    public void setEditable(boolean state) {
        if (state) {
            parent.add(plus);
            parent.add(minus);
        } else {
            parent.remove(plus);
            parent.remove(minus);
        }
        refresh();
    }

    void attachHooks() {
        plus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                val = MathHelper.round(Math.max(min, Math.min(max, val + step)), 2);
                refresh();
            }
        });

        minus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                val = MathHelper.round(Math.max(min, Math.min(max, val - step)), 2);
                refresh();
            }
        });
    }

    public void refresh() {
        label.setText(String.valueOf(MathHelper.round(val, 2)));
        if (val == min)
            minus.setEnabled(false);
        else
            minus.setEnabled(true);

        if (val == max)
            plus.setEnabled(false);
        else
            plus.setEnabled(true);

        if (cb != null)
            cb.callback(val);

        plus.repaint();
        minus.repaint();
    }

    public static interface Callback {
        public void callback(double value);
    }

}
