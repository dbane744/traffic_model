/**
 *Copyright 2017 Daniel Bane
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
