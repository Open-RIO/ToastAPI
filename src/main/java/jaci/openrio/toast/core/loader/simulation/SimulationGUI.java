package jaci.openrio.toast.core.loader.simulation;

import jaci.openrio.toast.core.Toast;
import jaci.openrio.toast.core.loader.simulation.jni.InterruptContainer;
import jaci.openrio.toast.core.loader.simulation.srx.TalonSRX_GUI;
import jaci.openrio.toast.lib.profiler.Profiler;
import jaci.openrio.toast.lib.state.RobotState;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * This is the Rat's nest of code that is the Simulation GUI. There is no clean way to programmatically create a GUI
 * with Swing. Most of the code called in this class is only called once, at runtime. This class is mostly self-contained
 * in terms of functionality, with hooks to {@link SimulationData}
 *
 * @author Jaci
 */
public class SimulationGUI extends JPanel {
    public static SimulationGUI INSTANCE;

    /**
     * The 'test' main method for testing how the Simulation GUI looks without invoking the rest of the Toast
     * Framework. This is really only used in Development environments when we change the layout or content of the GUI.
     */
    public static void main(String[] args) {
        //Test main method, never invoked outside of development environments
        Profiler.INSTANCE.section("Pre-Initialization").section("Simulation").start("GUI");
        create();
        Profiler.INSTANCE.section("Pre-Initialization").section("Simulation").stop("GUI");
    }

    /**
     * Create the Simulation GUI. This inflates the GUI and sets all the constants. This also
     * hooks into the Toast.shudownSafely method for when the GUI is closed.
     */
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
    GuiRelay[] relays = new GuiRelay[4];

    GuiRobotState disabled, auto, test, teleop;

    public SimulationGUI() {
        INSTANCE = this;

        this.setBackground(new Color(11, 11, 11));
        this.setPreferredSize(new Dimension(700, 500));
        this.setVisible(true);
        this.setLayout(null);

        CommonGUI.setup_keys(this, this::reinitElements);

        initElements();

        CommonGUI.registerKeyCommand(this, "D", () -> { setState(RobotState.DISABLED); });
        CommonGUI.registerKeyCommand(this, "A", () -> { setState(RobotState.AUTONOMOUS); });
        CommonGUI.registerKeyCommand(this, "T", () -> { setState(RobotState.TELEOP); });

        CommonGUI.registerKeyCommand(this, "C", this::openSRX);
        CommonGUI.registerKeyCommand(this, "P", this::openPneumatics);
    }

    /**
     * Set the state and repaint
     */
    public void setState(RobotState state) {
        SimulationData.currentState = state;
        repaintState();
    }

    /**
     * Repaint State Buttons
     */
    public void repaintState() {
        disabled.repaint();
        auto.repaint();
        teleop.repaint();
        test.repaint();
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
            spinner.enableProgress();
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

        createLabel("FWD", 235, 467, 40, 9, new Color(150, 150, 150));
        createLabel("REV", 235, 479, 40, 9, new Color(150, 150, 150));
        for (int i = 0; i < SimulationData.relay_fwd.length; i++) {
            boolean isForward = SimulationData.relay_fwd[i];
            boolean isReverse = SimulationData.relay_rvs[i];

            GuiRelay relay = new GuiRelay(267 + (int)(21.5 * i), 472, 10, 22, this);
            relay.setForward(isForward);
            relay.setReverse(isReverse);

            relays[i] = relay;
        }

        disabled = new GuiRobotState(575, 20, RobotState.DISABLED, this);
        auto = new GuiRobotState(575, 50, RobotState.AUTONOMOUS, this);
        teleop = new GuiRobotState(575, 80, RobotState.TELEOP, this);
        test = new GuiRobotState(575, 110, RobotState.TEST, this);

        GuiButton pneumaticsButton = new GuiButton(20, 100, 100, 30, false, "Pneumatics", false, this);
        pneumaticsButton.setCallback(new GuiButton.ButtonCallback() {
            @Override
            public void onClick() {
                openPneumatics();
            }
            public void onToggle(boolean state) { }
        });

        GuiButton srxButton = new GuiButton(20, 140, 100, 30, false, "CAN Talon SRX", false, this);
        srxButton.setCallback(new GuiButton.ButtonCallback() {
            @Override
            public void onClick() {
                openSRX();
            }
            public void onToggle(boolean state) { }
        });
    }

    /**
     * Open the Talon SRX Sub-GUI
     */
    public void openSRX() {
        if (TalonSRX_GUI.INSTANCE == null)
            TalonSRX_GUI.create();
    }

    /**
     * Open the Pneumatics Sub-GUI
     */
    public void openPneumatics() {
        if (PneumaticsGUI.INSTANCE == null)
            PneumaticsGUI.create();
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

    /**
     * Paint the components on the GUI. This is called whenever a global simulation Refresh is required, however most
     * repainting is done in the actual components themselves when required.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        paint((Graphics2D) g);
    }

    /**
     * Paint extra components. This is used for the small 'credit' label near the bottom of the GUI, as well as extra
     * details in the future when they are required.
     */
    public void paint(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(new Color(180, 180, 180));
        g.setFont(new Font("Arial", Font.ITALIC | Font.BOLD, 9));
        g.drawString("Toast Simulation GUI      -by OpenRIO-", 500, 490);
    }

}
