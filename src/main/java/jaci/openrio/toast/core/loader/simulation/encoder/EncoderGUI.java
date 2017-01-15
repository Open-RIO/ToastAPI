package jaci.openrio.toast.core.loader.simulation.encoder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jaci.openrio.toast.core.loader.simulation.GuiNumberSpinner;
import jaci.openrio.toast.core.loader.simulation.GuiNumberSpinner.Callback;

/**
 * Panel for simulated encoders
 * 
 * @author LoadingPleaseWait
 */
public class EncoderGUI extends JPanel {

	private static final long serialVersionUID = -5795176560686508623L;

	public static EncoderGUI INSTANCE;
	
    /**
     * @return EncoderGUI instance
     */
    public static JPanel create() {
        JFrame frame = new JFrame("Toast Encoder GUI");
        JPanel panel = new EncoderGUI();
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setPreferredSize(new Dimension(400, 375));
        frame.setVisible(true);
        frame.setResizable(true);
        frame.addWindowListener(new WindowAdapter() {
            /* (non-Javadoc)
             * @see java.awt.event.WindowAdapter#windowOpened(java.awt.event.WindowEvent)
             */
            @Override
            public void windowOpened(WindowEvent event) {
                super.windowOpened(event);
            }

            /* (non-Javadoc)
             * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
             */
            @Override
            public void windowClosing(WindowEvent event) {
                super.windowClosing(event);
                INSTANCE = null;
            }
        });

        return panel;
    }
	
	/**
	 * Set up the JPanel
	 */
	public EncoderGUI(){
		INSTANCE = this;
		
		this.setBackground(new Color(11, 11, 11));
        this.setPreferredSize(new Dimension(400, 375));
        this.setVisible(true);
        this.setLayout(null);
        
        initElements();
	}
	
	/**
	 * Remove everything and then add everything back
	 */
	public void reinitElements(){
		removeAll();
		initElements();
	}
	
	/**
	 * Initialize elements in the panel
	 */
	public void initElements(){
		// labels and number spinners for each encoder
		for(Long key : EncoderReg.wrappers.keySet()){
			JLabel encoderLabel = new JLabel("Encoder " + key);
	        encoderLabel.setForeground(Color.WHITE);// white text
	        encoderLabel.setBounds(10, key.intValue() * 20 + 10, 100, 20);
	        add(encoderLabel);
	        // encoder value
	        JLabel countLabel = new JLabel("Count");
	        countLabel.setForeground(Color.WHITE);
	        countLabel.setBounds(120, key.intValue() * 20 + 10, 100, 20);
	        add(countLabel);
			GuiNumberSpinner countSpinner = new GuiNumberSpinner(170, key.intValue() * 20 + 15, 0, 1, -10, 10, true, this);
			countSpinner.setCallback(new Callback() {
				
				/* (non-Javadoc)
				 * @see jaci.openrio.toast.core.loader.simulation.GuiNumberSpinner.Callback#callback(double)
				 */
				@Override
				public void callback(double value) {
					EncoderReg.wrappers.get(key).setValue((int) value);
				}
			});
			// encoder rate
			JLabel rateLabel = new JLabel("Rate");
			rateLabel.setForeground(Color.WHITE);
			rateLabel.setBounds(260, key.intValue() * 20 + 10, 100, 20);
			add(rateLabel);
			GuiNumberSpinner rateSpinner = new GuiNumberSpinner(300, key.intValue() * 20 + 15, 1, 0.1, -5, 5, true, this);
			rateSpinner.setCallback(new Callback() {
				
				/* (non-Javadoc)
				 * @see jaci.openrio.toast.core.loader.simulation.GuiNumberSpinner.Callback#callback(double)
				 */
				@Override
				public void callback(double value) {
					EncoderReg.wrappers.get(key).setRate(value);
				}
			});
		}
	}

}
