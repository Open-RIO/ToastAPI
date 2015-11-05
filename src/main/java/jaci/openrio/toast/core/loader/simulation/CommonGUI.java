package jaci.openrio.toast.core.loader.simulation;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Sets up operations that are common throughout all Simulation GUIs
 *
 * @author Jaci
 */
public class CommonGUI {

    /**
     * Setup keyboard shortcuts for the panel
     */
    public static void setup_keys(JPanel panel, Runnable reinit) {
        panel.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ctrl R"), "repaint");
        panel.getActionMap().put("repaint", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.repaint();
            }
        });

        panel.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ctrl shift R"), "reinit");
        panel.getActionMap().put("reinit", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reinit.run();
                panel.repaint();
            }
        });
    }

    /**
     * Add a key command to the GUI
     */
    public static void registerKeyCommand(JPanel panel, String keyset, Runnable run) {
        panel.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(keyset), keyset);
        panel.getActionMap().put(keyset, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                run.run();
            }
        });
    }

}
