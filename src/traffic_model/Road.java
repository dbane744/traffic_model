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
 * Encapsulates the x and y position of one road tile.
 * 
 * @author Daniel Bane
 *
 */
public class Road {

	/**
	 * The x position of the road tile.
	 */
	private int x;
	/**
	 * The y position of the road tile.
	 */
	private int y;

	public Road(int y, int x) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Returns the x coordinate.
	 * 
	 * @return
	 */
	public int getX() {
		return x;
	}

	/**
	 * Returns the y coordinate.
	 * 
	 * @return
	 */
	public int getY() {
		return y;
	}

	/**
	 * Sets the x coordinate.
	 * 
	 * @param x
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Sets the y coordinate.
	 * 
	 * @param y
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Returns a string representation of the tile.
	 */
	public String toString() {
		return "This road tile has the position: x = " + x + " y = " + y;
	}
}
