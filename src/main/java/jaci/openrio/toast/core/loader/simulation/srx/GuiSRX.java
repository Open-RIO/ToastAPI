package jaci.openrio.toast.core.loader.simulation.srx;

import edu.wpi.first.wpilibj.CANTalon;
import jaci.openrio.toast.core.loader.simulation.GuiNumberSpinner;

import javax.swing.*;
import java.awt.*;

public class GuiSRX extends JComponent {

    int x, y;
    SRX_Reg.SRX_Wrapper wrapper;
    static Color enabled = new Color(20, 200, 20);
    static Color disabled = new Color(200, 20, 20);
    static Color graytext = new Color(140, 140, 140);

    JLabel control_mode, slotid, follower, speed, position;
    GuiNumberSpinner pvbus, voltage;

    public GuiSRX(int x, int y, SRX_Reg.SRX_Wrapper wrapper, JPanel parent) {
        this.x = x; this.y = y; this.wrapper = wrapper;
        wrapper.onUpdate(this::update);
        this.setBounds(x, y, 500, 150);
        parent.add(this);

        createLabel("SRX: " + wrapper.dev_id, 0, 10, 100, 20, new Color(200, 200, 200));
        control_mode = createLabel("", 10, 30, 200, 14, graytext);
        slotid = createLabel("", 10, 50, 200, 14, graytext);
        follower = createLabel("", 10, 70, 200, 14, graytext);
        speed = createLabel("", 230, 70, 200, 14, graytext);
        position = createLabel("", 230, 90, 200, 14, graytext);

        createLabel("Throttle: ", 230, 30, 70, 14, graytext);
        pvbus = new GuiNumberSpinner(300, 35, 0, 0, -1, 1, false, this).enableProgress().setFontSize(14);

        createLabel("Voltage: ", 230, 50, 70, 14, graytext);
        voltage = new GuiNumberSpinner(300, 55, 0, 0, -12, 12, false, this).enableProgress().setFontSize(14);

        update();
    }

    /**
     * On Talon Update
     */
    public void update() {
        control_mode.setText("Control Mode: " + CANTalon.TalonControlMode.valueOf(wrapper.mode).toString());
        slotid.setText("Slot ID: " + wrapper.slotid);
        follower.setText("Following: " + wrapper.follower);

        pvbus.setValue(wrapper.pvbus);
        voltage.setValue(wrapper.voltage);
        speed.setText("Speed Delta: " + wrapper.speed);
        position.setText("Position Delta: " + wrapper.position);

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

    }

    /**
     * Create a JLabel to place next to various components.
     */
    public JLabel createLabel(String text, int x, int y, int width, int fontSize, Color color) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setBounds(x, y, width, 20);
        label.setFont(new Font("Arial", 0, fontSize));
        this.add(label);
        return label;
    }

}
