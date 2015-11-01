package jaci.openrio.toast.core.loader.simulation;

import jaci.openrio.toast.lib.math.MathHelper;
import jaci.openrio.toast.core.Environment;

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
        
        if(Environment.getOS() == Environment.OS.WINDOWS) {
            plus.setBorder(null);
            minus.setBorder(null);
        }

        label = new JLabel(String.valueOf(val), SwingConstants.CENTER);
        label.setBounds(x + 10, y, 30, 10);
        label.setFont(new Font("Arial", 0, 10));
        label.setForeground(new Color(180, 180, 180));
        parent.add(label);

        setEditable(editable);
        attachHooks();
        refresh();
    }

    /**
     * Set the Callback object. The callback object is called when the Number Spinner is increased or decreased
     * by the user.
     */
    public void setCallback(Callback cb) {
        this.cb = cb;
    }

    /**
     * Set the value of the Number Spinner. This is clamped to 1 and -1 and rounded to
     * 2 decimal places.
     */
    public void setValue(double val) {
        this.val = MathHelper.round(Math.max(-1, Math.min(1, val)), 2);
        refresh();
    }

    /**
     * Set the object as editable. If editable, the number spinner will show the + and - buttons for the user to
     * activate. If not, these buttons are removed and it serves purely as a display.
     */
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

    /**
     * Attach the + and - hooks to the buttons. This is used to influence the value of the spinner.
     */
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

    /**
     * Refresh the element. This enables/disables any + or - buttons if the clamp is reached, as well
     * as call the callback if required.
     */
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
        /**
         * Called when a Number Spinner changes value. Use this to trigger any hooks related to the number spinner.
         */
        public void callback(double value);
    }

}
