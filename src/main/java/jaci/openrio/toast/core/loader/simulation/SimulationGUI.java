package jaci.openrio.toast.core.loader.simulation;

import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.core.loader.simulation.jni.InterruptContainer;
import jaci.openrio.toast.lib.state.RobotState;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Field;
import java.util.Arrays;

public class SimulationGUI extends JPanel {
    public static SimulationGUI INSTANCE;

    public static void main(String[] args) {
        //Test
        create();
    }

    public static JPanel create() {
        JFrame frame = new JFrame("Toast Simulation GUI");
        JPanel panel = new SimulationGUI();
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
                Toast.getToast().shutdownSafely();
            }
        });
        return panel;
    }

    GuiNumberSpinner[] dioSpinners = new GuiNumberSpinner[10];
    GuiNumberSpinner[] pwmSpinners = new GuiNumberSpinner[10];
    GuiNumberSpinner[] accelSpinners = new GuiNumberSpinner[3];

    public SimulationGUI() {
        INSTANCE = this;

        this.setBackground(new Color(11, 11, 11));
        this.setPreferredSize(new Dimension(700, 500));
        this.setVisible(true);
        this.setLayout(null);

        initElements();
    }

    public void reinitElements() {
        this.removeAll();
        initElements();
    }

    public void initElements() {
        Image logo;
        try {
            logo = ImageIO.read(SimulationGUI.class.getClassLoader().getResourceAsStream("assets/toast/gui/darkRIOsmall.png")).getScaledInstance(400, 400, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(logo));
            imageLabel.setBounds(150, 70, 400, 400);
            this.add(imageLabel);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < SimulationData.dioValues.length; i++) {
            byte val = SimulationData.dioValues[i];
            byte dir = SimulationData.dioDirections[i];

            GuiNumberSpinner spinner = new GuiNumberSpinner(90, 216 + (22 * i), val, 1, 0, 1, dir == 1, this);
            dioSpinners[i] = spinner;
            final int finalI = i;
            spinner.setCallback(new GuiNumberSpinner.Callback() {
                @Override
                public void callback(double value) {
                    SimulationData.dioValues[finalI] = (byte) value;
                    InterruptContainer container = InterruptContainer.getByPin(finalI);
                    if (container != null)
                        container.trigger(value >= 0.99);
                }
            });
        }

        for (int i = 0; i < SimulationData.pwmValues.length; i++) {
            double val = SimulationData.pwmValues[i];

            GuiNumberSpinner spinner = new GuiNumberSpinner(540, 214 + (22 * i), val, 0.05, -1, 1, false, this);
            pwmSpinners[i] = spinner;
        }

        createLabel("Accelerometer", 20, 10, 100, 14, new Color(180, 180, 180));

        for (int i = 0; i < SimulationData.accelerometer.length; i++) {
            double val = SimulationData.accelerometer[i];

            String l = i == 0 ? "X" : i == 1 ? "Y" : "Z";

            createLabel(l + " Axis", 80, 28 + (15 * i), 100, 10, new Color(150, 150, 150));

            GuiNumberSpinner spinner = new GuiNumberSpinner(20, 33 + (15 * i), val, 0.1, -5, 5, true, this);
            accelSpinners[i] = spinner;
            final int finalI = i;
            spinner.setCallback(new GuiNumberSpinner.Callback() {
                @Override
                public void callback(double value) {
                    SimulationData.accelerometer[finalI] = value;
                }
            });
        }

        createLabel("Power", 210, 10, 100, 14, new Color(180, 180, 180));

        GuiNumberSpinner voltage = new GuiNumberSpinner(205, 30, 12.5, 0.1, 5, 12.8, true, this);
        voltage.setCallback(new GuiNumberSpinner.Callback() {
            @Override
            public void callback(double value) {
                SimulationData.powerVinVoltage = value;
                SimulationData.pdpVoltage = value;
            }
        });

        GuiButton brownout = new GuiButton(205, 45, 50, 20, false, "Brownout", true, this);
        brownout.setFont(new Font("Arial", 0, 10));
        brownout.setActiveColor(new Color(170, 100, 100));
        brownout.setCallback(new GuiButton.ButtonCallback() {
            @Override
            public void onClick() {
            }

            @Override
            public void onToggle(boolean state) {
                SimulationData.powerUserActive6V = !state;
                SimulationData.powerUserActive5V = !state;
                SimulationData.powerUserActive3V3 = !state;
            }
        });

        GuiRobotState disabled = new GuiRobotState(575, 20, RobotState.DISABLED, this);
        GuiRobotState auto = new GuiRobotState(575, 50, RobotState.AUTONOMOUS, this);
        GuiRobotState teleop = new GuiRobotState(575, 80, RobotState.TELEOP, this);
        GuiRobotState test = new GuiRobotState(575, 110, RobotState.TEST, this);
    }

    public JLabel createLabel(String text, int x, int y, int width, int fontSize, Color color) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setBounds(x, y, width, 20);
        label.setFont(new Font("Arial", 0, fontSize));
        this.add(label);
        return label;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        paint((Graphics2D) g);
    }

    public void paint(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(new Color(180, 180, 180));
        g.setFont(new Font("Arial", Font.ITALIC | Font.BOLD, 9));
        g.drawString("Toast Simulation GUI      -by Jaci-", 540, 490);
    }

}
