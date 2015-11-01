package jaci.openrio.toast.core.loader.simulation;

import jaci.openrio.toast.core.Toast;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class PneumaticsGUI extends JPanel {
    public static PneumaticsGUI INSTANCE;

    public static JPanel create() {
        JFrame frame = new JFrame("Toast Pneumatics GUI");
        JPanel panel = new PneumaticsGUI();
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                super.windowOpened(e);
            }

            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                INSTANCE = null;
            }
        });
        return panel;
    }

    public PneumaticsGUI() {
        INSTANCE = this;

        this.setBackground(new Color(11, 11, 11));
        this.setPreferredSize(new Dimension(400, 375));
        this.setVisible(true);
        this.setLayout(null);

        initElements();
    }

    /**
     * Completely reinflate the GUI. This replaces all JComponents in the GUI.
     */
    public void reinitElements() {
        this.removeAll();
        initElements();
    }

    GuiPCM[] pcms = new GuiPCM[3];

    /**
     * Inflate the GUI. This does everything from adding all the elements, to adding the background image and setting up
     * the GUI. This is where most of the work is done, and is only called once: upon creation of the GUI.
     */
    public void initElements() {
        for (int i = 0; i < pcms.length; i++) {
            pcms[i] = new GuiPCM(10, 30 + 105 * i, i, this);
        }
    }

}
