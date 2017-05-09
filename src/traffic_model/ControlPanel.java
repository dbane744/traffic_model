package traffic_model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * Encapsulates the control panel within the gui.
 * 
 * @author Daniel Bane
 *
 */
public class ControlPanel extends JPanel {

	public ControlPanel() {
		// A matte border that is set to only draw on the bottom side (acts as a
		// single line separating the panels).
		setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
	}
    
	/**
	 * Gets the preferred size of the panel which is 400 x 400.
	 */
	public Dimension getPreferredSize() {
		return new Dimension(400, 400);
	}
	/**
	 * Repaints the panel.
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}
}
