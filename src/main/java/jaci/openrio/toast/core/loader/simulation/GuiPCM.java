package jaci.openrio.toast.core.loader.simulation;

import javax.swing.*;
import java.awt.*;

public class GuiPCM extends JComponent {

    int x, y, pcmid;

    Color enabled = new Color(20, 200, 20);
    Color disabled = new Color(200, 20, 20);
    Color graytext = new Color(140, 140, 140);
    JLabel compressor_mode;

    public GuiPCM(int x, int y, int pcmid, JPanel parent) {
        this.x = x;
        this.y = y;
        this.pcmid = pcmid;
        this.setBounds(x, y, 400, 100);
        parent.add(this);

        createLabel("Pneumatics Control Module " + pcmid, 10, 0, 200, 14, new Color(180, 180, 180));
        createLabel("Compressor: ", 15, 15, 100, 12, graytext);
        boolean run = SimulationData.compressorRunning((byte) pcmid);
        compressor_mode = createLabel(run ? "Running" : "Off", 100, 30, 75, 12, run ? enabled : graytext);

        createLabel("Current: ", 15, 45, 100, 12, graytext);
        new GuiNumberSpinner(100, 50, 5, 0.5, 0, 11, true, this).setCallback(value -> {
            SimulationData.compressor_current[pcmid] = (float) value;
        });

        createLabel("Control Loop: ", 15, 60, 100, 12, graytext);

        new GuiButton(15, 80, 100, 20, SimulationData.compressor_pressure[pcmid], "Pressure Switch", true, this).setCallback(new GuiButton.ButtonCallback() {
            @Override
            public void onClick() { }

            @Override
            public void onToggle(boolean state) {
                SimulationData.setCompressorPressureSwitch((byte) pcmid, state);
            }
        });

        createLabel("Solenoids: ", 200, 15, 100, 12, new Color(150, 150, 150));

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
        if (SimulationData.enabled_compressors[pcmid]) g.setColor(enabled);
        else g.setColor(disabled);
        g.fillRect(100, 21, 15, 10);

        if (SimulationData.loop_compressors[pcmid]) g.setColor(enabled);
        else g.setColor(disabled);

        g.fillRect(100, 66, 15, 10);

        boolean run = SimulationData.compressorRunning((byte) pcmid);
        compressor_mode.setText(run ? "Running" : "Off");
        compressor_mode.setForeground(run ? enabled : graytext);

        int sc = 0;
        for (int j = 0; j < 2; j++)
            for (int i = 0; i < 4; i++) {
                g.setColor(graytext);
                g.drawString("" + sc, 240 + (j * 40), 45 + (17 * i));
                boolean en = SimulationData.solenoids[pcmid][sc];
                g.setColor(en ? enabled : disabled);
                g.fillRect(220 + (j * 40), 35 + (17 * i), 15, 10);
                sc++;
            }
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
