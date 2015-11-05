package jaci.openrio.toast.core.loader.simulation.srx;

import jaci.openrio.toast.core.loader.simulation.CommonGUI;
import jaci.openrio.toast.core.loader.simulation.GuiScrollbar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class TalonSRX_GUI extends JPanel {

    public static TalonSRX_GUI INSTANCE;

    public static JPanel create() {
        JFrame frame = new JFrame("Toast CAN Talon SRX GUI");
        JPanel panel = new TalonSRX_GUI();
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(true);
        frame.setMinimumSize(new Dimension(600, 400));
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

    public TalonSRX_GUI() {
        INSTANCE = this;

        this.setBackground(new Color(11, 11, 11));
        this.setPreferredSize(new Dimension(600, 500));
        this.setLayout(new BorderLayout());
        this.setVisible(true);

        CommonGUI.setup_keys(this, this::reinitElements);

        initElements();
    }

    /**
     * Completely reinflate the GUI. This replaces all JComponents in the GUI.
     */
    public void reinitElements() {
        this.removeAll();
        initElements();
    }


    /**
     * Inflate the GUI. This does everything from adding all the elements, to adding the background image and setting up
     * the GUI. This is where most of the work is done, and is only called once: upon creation of the GUI.
     */
    public void initElements() {
        JPanel viewcontroller = new JPanel();
        int pref_height = 10;
        viewcontroller.setVisible(true);
        viewcontroller.setLayout(null);
        viewcontroller.setBackground(new Color(11, 11, 11));

        JScrollPane pane = new JScrollPane(viewcontroller);
        pane.setBorder(null);
        pane.getVerticalScrollBar().setUI(new GuiScrollbar());
        pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        int i = 0;
        for (SRX_Reg.SRX_Wrapper wrapper : SRX_Reg.wrappers.values()) {
            viewcontroller.add(new GuiSRX(50, 10 + 120 * i, wrapper, viewcontroller));
            pref_height += 120;
            i++;
        }
        viewcontroller.setPreferredSize(new Dimension(600, pref_height));

        this.add(pane);
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

}
