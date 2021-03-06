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

/**
 * Encapsulates a singular vehicle/agent.
 * 
 * @author Daniel Bane
 *
 */
public class Vehicle {

	/**
	 * Stores the current x position of the vehicle in the temporary map.
	 */
	private int currentX;
	/**
	 * Stores the current y position of the vehicle in the temporary map.
	 */
	private int currentY;

	/**
	 * The current speed the vehicle is travelling. Dictates how many times this
	 * vehicles move() will be called per tick. Current speed is increased by 1
	 * every tick the vehicle moves without stopping.
	 * 
	 */
	private int currentSpeed;
	/**
	 * Stores whether the vehicle is stood still or not.
	 */
	private boolean stoodStill;

	public Vehicle(int startingY, int startingX) {
		this.currentX = startingX;
		this.currentY = startingY;
	}

	/**
	 * Gets the current X position of the vehicle.
	 */
	public int getX() {
		return currentX;
	}

	/**
	 * Gets the current Y position of the vehicle.
	 */
	public int getY() {
		return currentY;
	}

	/**
	 * Sets the current x.
	 * 
	 * @param newX
	 *            The new x value of the vehicle within the map.
	 */
	public void setX(int newX) {
		currentX = newX;
	}

	/**
	 * Sets the current y.
	 * 
	 * @param newY
	 *            The new y value of the vehicle within the map.
	 */
	public void setY(int newY) {
		currentY = newY;
	}

	/**
	 * Sets whether the vehicle is stood still or not.
	 * 
	 * @param input
	 *            A boolean value.
	 */
	public void setStoodStill(boolean input) {

		stoodStill = input;
	}

	/**
	 * Gets whether the vehicle is stood still or not.
	 * 
	 * @return A boolean value.
	 */
	public boolean getStoodStill() {

		return stoodStill;
	}

}
