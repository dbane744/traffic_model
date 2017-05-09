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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Implements a mouse listener for the model panel. 
 * 
 * Grabs the location where the
 * mouse was clicked to allow the placement of cars or traffic lights. Left
 * click on or to the side of the road places a car. Right click places a
 * traffic light.
 * 
 * @author Daniel Bane
 *
 */
public class MouseAdp extends MouseAdapter {

	/**
	 * Stores the model panel.
	 */
	JPanel mPanel;

	public MouseAdp(JPanel mPanel) {
		this.mPanel = mPanel;
	}

	/**
	 * The listener for mouse clicks. A left click on the road will place a
	 * vehicle. A right click will place a traffic light.
	 */
	public void mouseClicked(MouseEvent me) {

		// Gets the current instance of temporaryMap in Storage.
		double[][] tempMap = Storage.getInstance().getTempMap();

		// Only listens for clicks if there is an image loaded in the panel and
		// the model is not running.
		if (tempMap != null && !Storage.getInstance().getRuntime()) {

			// Gets the current panel size.
			Dimension currentpanelSize = mPanel.getSize();
			int panelX = (int) currentpanelSize.getHeight();
			int panelY = (int) currentpanelSize.getWidth();

			// Gets the image size.
			BufferedImage image = Storage.getInstance().getDataAsImage();
			int imageX = image.getHeight();
			int imageY = image.getWidth();

			// Gets the ratio between the image size and the panel size.
			double ratio = (double) imageY / (double) panelY;

			// Returns the point that was clicked. And stores the x and y values
			// for further use.
			Point clickedPoint = me.getPoint();

			// The raw x and y values are rescaled to the ratio of the image -
			// allows the panel/image to be larger than the inputed image while
			// allowing the user to
			// select a coordinate within the original image.

			int x = (int) (clickedPoint.getX() * ratio);
			int y = (int) (clickedPoint.getY() * ratio);

			// Adds a vehicle to temporaryMap (which is displayed in the panel)
			// if the
			// user left clicks on the road.
			if (SwingUtilities.isLeftMouseButton(me)) {
				// Only places if the clicked tile is a road.
				if (tempMap[y][x] <= 4 && tempMap[y][x] > 0) {
					// Adds 9 to the road element to signify a vehicle.
					tempMap[y][x] = (tempMap[y][x]) + 9;
					// Replaces the map in storage with the new map.
					Storage.getInstance().setTempMap(tempMap);
					mPanel.repaint();
				}
			}

			// Places a traffic light if the user right clicks near a road.
			else if (SwingUtilities.isRightMouseButton(me)) {

				if (tempMap[y][x] <= 4 && tempMap[y][x] > 0) {
					// Adds 100 to the road element to signify a traffic light.
					tempMap[y][x] = (tempMap[y][x]) + 100;

					// If the traffic light is on a north or south road tiles
					// will be altered for painting to the east and west.
					if (tempMap[y][x] == 101 || tempMap[y][x] == 103) {
						// In case the traffic light is placed on a corner, only
						// paints to empty spaces.
						if (tempMap[y][x + 1] == 0) {
							// 200 signifies the adjacently painted tiles.
							tempMap[y][x + 1] = 200;
						}
						if (tempMap[y][x - 1] == 0) {
							tempMap[y][x - 1] = 200;
						}
					}

					// If the traffic light is on an east or west road tiles
					// will be altered for painting to the north and south.
					if (tempMap[y][x] == 102 || tempMap[y][x] == 104) {

						if (tempMap[y + 1][x] == 0) {
							tempMap[y + 1][x] = 200;
						}
						if (tempMap[y - 1][x] == 0) {
							tempMap[y - 1][x] = 200;
						}
					}

					// Replaces the map in storage with the new map.
					Storage.getInstance().setTempMap(tempMap);
					mPanel.repaint();
				}
			}

		}

	}
}
