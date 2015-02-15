package jaci.openrio.toast.core.loader.simulation;

import jaci.openrio.toast.core.loader.simulation.jni.InterruptContainer;
import jaci.openrio.toast.lib.state.RobotState;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
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
        return panel;
    }

    GuiNumberSpinner[] dioSpinners = new GuiNumberSpinner[10];
    GuiNumberSpinner[] pwmSpinners = new GuiNumberSpinner[10];

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
            imageLabel.setBounds(150, 50, 400, 400);
            this.add(imageLabel);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < SimulationData.dioValues.length; i++) {
            byte val = SimulationData.dioValues[i];
            byte dir = SimulationData.dioDirections[i];

            GuiNumberSpinner spinner = new GuiNumberSpinner(90, 196 + (22 * i), val, 1, 0, 1, dir == 1, this);
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

            GuiNumberSpinner spinner = new GuiNumberSpinner(540, 194 + (22 * i), val, 0.05, -1, 1, false, this);
            pwmSpinners[i] = spinner;
        }

        GuiRobotState disabled = new GuiRobotState(575, 20, RobotState.DISABLED, this);
        GuiRobotState auto = new GuiRobotState(575, 50, RobotState.AUTONOMOUS, this);
        GuiRobotState teleop = new GuiRobotState(575, 80, RobotState.TELEOP, this);
        GuiRobotState test = new GuiRobotState(575, 110, RobotState.TEST, this);

//        Color title = new Color(230, 230, 230);
//        createLabel("Accelerometer:", 7, 10, 100, 14, title);
//        createSpinner("X:", -1, 1, 0.05, 7, 30, "accelerometerX");
//        createSpinner("Y:", -1, 1, 0.05, 7, 55, "accelerometerY");
//        createSpinner("Z:", -1, 1, 0.05, 7, 80, "accelerometerZ");
//        createSpinner("Rng:", -1, 1, 0.05, 7, 105, "accelerometerRange");
//
//        createLabel("Power Dist Panel:", 7, 140, 150, 14, title);
//        createSpinner("Temp:", 0, 40, 0.5, 7, 160, "pdpTemperature");
//        createSpinner("Volt:", 0, 12.8, 0.5, 7, 185, "pdpVoltage");
//        createSpinner("RIO:", 0, 12.8, 0.5, 7, 210, "powerVinVoltage");
//        createSpinner("Amp:", 0, 20, 0.25, 7, 235, "powerVinCurrent");
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
