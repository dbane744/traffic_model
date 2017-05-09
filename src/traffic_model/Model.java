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

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

/**
 * Contains variables and methods which are utilised to run the agent-based
 * traffic model.
 * 
 * @author User
 *
 */
public class Model {

	/**
	 * Stores the number of ticks the model will run for.
	 */
	private int runtime;
	/**
	 * Stores the speed at which the animation will occur. Dictates the timer value in runModel().
	 */
	private double animationSpeed;
	/**
	 * Lists every road tile in the order they are placed.
	 */
	private ArrayList<Road> roadList = new ArrayList<Road>();
	/**
	 * Lists every vehicle in the order they are positioned on the road.
	 */
	private ArrayList<Vehicle> vehicleList = new ArrayList<Vehicle>();
	/**
	 * Lists ever tarffic light in the order they are positioned on the road.
	 */
	private ArrayList<TrafficLight> lightList = new ArrayList<TrafficLight>();
	/**
	 * This stores the model panel to paint to.
	 */
	private JPanel mPanel;
	/**
	 * This stores the statistics panel to paint to.
	 */
	private JPanel sPanel;

	/**
	 * The number of ticks the red light on the traffic lights should stay on
	 * for.
	 */
	private int redLightLength;

	/**
	 * 
	 * @param runtime
	 *            The number of ticks the model will run for.
	 * @param animationSpeed
	 *            The speed in which each tick occurs in real-time.
	 */
	public Model(int runtime, double animationSpeed, int redLightLength,
			JPanel mPanel, JPanel sPanel) {

		// Stores the arguments locally.
		this.runtime = runtime;
		this.animationSpeed = animationSpeed;
		this.mPanel = mPanel;
		this.redLightLength = redLightLength;
		this.sPanel = sPanel;
	}

	/**
	 * Returns the traffic light list.
	 * 
	 * @return List of traffic lights.
	 */
	public ArrayList<TrafficLight> getLightList() {
		return lightList;
	}

	/**
	 * Runs the model.
	 */
	public void runModel() {

		Vehicle currentVehicle;

		/*
		 * The list methods must be called in this order to work correctly!
		 * Traffic lights turn themselves off after being found and created to
		 * allow listRoad() to function.
		 */

		// Finds and stores all the traffic lights.
		listTrafficLights();

		// Finds and stores all the road tiles.
		listRoad();

		// Finds and stores all the vehicles in the vehicle list.
		listVehicles();

		// Will store all the average percentages of vehicles stood still.
		ArrayList<Double> stoodStillList = new ArrayList<Double>();

		// Will store all the average distances between vehicles.
		ArrayList<Double> avDistList = new ArrayList<Double>();

		// A timer to wait between vehicle movements to allow the user to see
		// the modelling process.
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {

			// Stores the number of iterations the model has ran for.
			int iterations = 0;

			// This will run once at a certain interval defined by the user in
			// 'runtime'.
			@Override
			public void run() {

				// Sets the runtime to be true in storage which is then used to
				// disable
				// features that would break the model if used during runtime.
				Storage.getInstance().setRuntime(true);

				// Moves each vehicle.
				for (Vehicle vehicle : vehicleList) {

					// Will end the loop and cancel the timer if the reset
					// button has been pressed by the user/the temporary map has
					// been reset.
					if (Storage.getInstance().getCancelModelLoop()) {
						// Resets the boolean within storage to false so the
						// model can be run again.
						Storage.getInstance().setCancelModelLoop(false);

						// Resets the traffic light to on.
						for (TrafficLight light : lightList) {
							light.resetLight();
							mPanel.repaint();
						}
						cancel();
						// Resets disabled features.
						Storage.getInstance().setRuntime(false);
						break;
					}

					moveVehicle(vehicle);
				}

				// Calculates the percentage of vehicles stood still and saves
				// the value in Storage and stoodStillList.
				double still = calcPercentStoodStill();
				Storage.getInstance().setPercentStill((int) still);
				stoodStillList.add(still);

				// Calculates the average distance between each vehicle and the
				// closet vehicle in front. Adds the value to storage and
				// avDistList.
				double dist = calcAverageVehicleDist();
				Storage.getInstance().setAverageVehicDist(dist);
				avDistList.add(dist);

				sPanel.repaint();

				for (TrafficLight light : lightList) {
					light.tick();
				}

				iterations++;

				// Ends the model when the number of runtime iterations is
				// reached.
				if (iterations == runtime) {

					// Calculate the overall average of all the average values
					// obtained in the model and stores them in storage.
					Storage.getInstance()
							.setOverallStill(calcListAverage(stoodStillList));
					Storage.getInstance()
							.setOverallDist(calcListAverage(avDistList));

					// The traffic light values in the stored
					// temporary map are reset to +100 to allow the
					// identification of the
					// lights in further runs of the model using the same data.

					for (TrafficLight light : lightList) {
						light.resetLight();
						mPanel.repaint();
					}

					cancel();
					// Resets disabled features.
					Storage.getInstance().setRuntime(false);
				}
			}
		}, 100, (long) animationSpeed);
		// First number is the delay of the user pressing the start button and
		// the model starting.
		// The second(more useful) number is the delay between each run().
		// Both are in milliseconds.
	}

	/**
	 * Tries to move the inputed vehicle one road tile.
	 * 
	 * @param vehicle
	 *            The vehicle to move.
	 */
	public void moveVehicle(Vehicle vehicle) {

		double[][] map = Storage.getInstance().getTempMap();

		// The road tile that the vehicle will try to move to.
		Road nextTile = moveTo(vehicle.getY(), vehicle.getX());

		// Only moves if the new road tile is an empty road tile (which would be
		// in the range of 1 to 4).
		if (map[nextTile.getY()][nextTile.getX()] <= 4) {

			// As the vehicle will now move the stood still state is set to
			// false.
			vehicle.setStoodStill(false);

			/*
			 * Updates the map to the vehicle's new position.
			 */

			// The current position minuses 9 to represent the loss of the
			// vehicle.
			map[vehicle.getY()][vehicle
					.getX()] = (map[vehicle.getY()][vehicle.getX()] - 9);
			// The new position adds 9 to represent the gain of the vehicle.
			map[nextTile.getY()][nextTile
					.getX()] = (map[nextTile.getY()][nextTile.getX()] + 9);

			/*
			 * Updates the vehicle to it's new position.
			 */

			vehicle.setX(nextTile.getX());
			vehicle.setY(nextTile.getY());
		} else {
			// If the vehicle didn't move the stood still state will be set to
			// true.
			vehicle.setStoodStill(true);
		}
		// Repaints the model panel.
		mPanel.repaint();
	}

	/**
	 * Moves to the next road tile. The reason this method does not use the
	 * current road tile index to find the next tile is so that future versions
	 * of the software can more easily integrate intersecting roads that do not
	 * follow a specific road circuit.
	 * 
	 * @param currentX
	 *            The current x position.
	 * @param currentY
	 *            The current y position.
	 * @return The road tile that is to be moved to(which encapsulates the x and
	 *         y coordinates).
	 */
	public Road moveTo(int currentY, int currentX) {

		// Gets the current state of the map.
		double[][] map = Storage.getInstance().getTempMap();
		// Will store the new road tile to move to.
		Road newRoadTile = new Road(currentY, currentX);

		/*
		 * Gets the direction the current road tile is currently on
		 * (North/east/south/west facing). Values correspond to direction: 1 -
		 * North, 2 - East , 3 - South, 4 - West. If the current tile has a
		 * vehicle on it that value will be the road tile + 9 or a traffic light
		 * + 100. So values in the range of 10-13 have 9 subtracted. Values of
		 * over 100 have 100 subtracted.
		 */

		// Gets the current raw road value(that may include overlapping
		// vehicles).
		int currentTileValue = (int) map[currentY][currentX];

		// Stores the actual road type value after potential vehicle/traffic
		// light removal.
		int currentRoadType = findRoadType(currentTileValue);

		// Finds the new tile to move to.
		switch (currentRoadType) {
			// If the road is north facing the new tile will have current y - 1;
			case 1 :
				newRoadTile.setY(currentY - 1); // System.out.println("CASE 1");
				break;
			// If the road is east facing the new tile will have current x + 1;
			case 2 :
				newRoadTile.setX(currentX + 1); // System.out.println("CASE 2");
				break;
			// If the road is south facing the new tile will have current y + 1;
			case 3 :
				newRoadTile.setY(currentY + 1); // System.out.println("CASE 3");
				break;
			// If the road is west facing the new tile will have current x - 1;
			case 4 :
				newRoadTile.setX(currentX - 1); // System.out.println("CASE 4");
				break;

			default :
				System.out.println(
						"DEFAULT   current road type= " + currentRoadType);// ****
																			// DANIEL
																			// MAKE
																			// AND
																			// THROW
																			// YOUR
																			// OWN
																			// EXCPETION
				break;
		}

		// Finds the adjoining road if the current road tile is a corner.
		if (map[newRoadTile.getY()][newRoadTile.getX()] == 0) {
			newRoadTile = onCornerFindRoad(currentY, currentX, currentRoadType);
		}

		return newRoadTile;
	}

	/**
	 * Finds and returns the road type of the inputed road value (Road type
	 * being the direction the road is facing i.e north = 1, east = 2, south =
	 * 3, west = 4). Filters out any overlapping objects such as vehicles and
	 * traffic lights.
	 * 
	 * @return A value in the range of 1-4 corresponding to the inputed tile's
	 *         directional road value.
	 */
	public int findRoadType(int rawRoadValue) {

		// Will store the directional value of the road (1-4).
		int actualRoadValue;

		// If the tile contains a vehicle.
		if (rawRoadValue >= 10 && rawRoadValue <= 13) {
			actualRoadValue = (rawRoadValue - 9);
		}
		// If the tile contains a traffic light and no vehicle.
		else if (rawRoadValue > 100 && rawRoadValue <= 104) {
			actualRoadValue = (rawRoadValue - 100);
		}
		// If the current tile contains a traffic light and a vehicle.
		else if (rawRoadValue > 104) {
			actualRoadValue = (rawRoadValue - 109);
		} else {
			// This happens when the current tile value was already in the range
			// of 1 - 4.
			actualRoadValue = rawRoadValue;
		}

		return actualRoadValue;
	}

	/**
	 * If the new road tile is an empty space (tile with a 0) it will
	 * recalculate the new tile by searching for surrounding tiles around the
	 * current tile that have a different road value from the current (Because
	 * an adjoining road will have a different direction value).
	 * 
	 * @param cornerY
	 *            The y coordinate of the corner tile.
	 * @param cornerX
	 *            The x coordinate of the corner tile.
	 * @param currentRoadType
	 *            An integer value corresponding to the current road
	 *            type/direction the road is facing.
	 * @return The road tile of the adjoining road.
	 */
	public Road onCornerFindRoad(int cornerY, int cornerX,
			int currentRoadType) {

		double[][] map = Storage.getInstance().getMap();
		Road newRoadTile = new Road(cornerY, cornerX);

		// Each if statement uses findRoadType() to ensure that only the
		// surrounding
		// road tiles are being assessed(disregarding overlapping vehicles).

		// Searches north of the current tile for another road.
		if (map[cornerY - 1][cornerX] != 0 && findRoadType(
				(int) map[cornerY - 1][cornerX]) != currentRoadType) {
			newRoadTile.setY(cornerY - 1);
		}
		// Searches east of the current tile for another road.
		else if (map[cornerY][cornerX + 1] != 0 && findRoadType(
				(int) map[cornerY][cornerX + 1]) != currentRoadType) {
			newRoadTile.setX(cornerX + 1);
		}
		// Searches south of the current tile for another road.
		else if (map[cornerY + 1][cornerX] != 0 && findRoadType(
				(int) map[cornerY + 1][cornerX]) != currentRoadType) {
			newRoadTile.setY(cornerY + 1);
		}
		// Searches west of the current tile for another road.
		else if (map[cornerY][cornerX - 1] != 0 && findRoadType(
				(int) map[cornerY][cornerX - 1]) != currentRoadType) {
			newRoadTile.setX(cornerX - 1);
		}

		return newRoadTile;
	}

	/**
	 * Gets an ordered list of each element in the road network. Follows the
	 * direction of the road. Used to get a similarly ranked list of vehicles.
	 */
	public void listRoad() {

		// Stores the current map state.
		double[][] map = Storage.getInstance().getTempMap();
		// Stores the first road element in the map found. Used as a starting
		// point for storing each road tile.
		int firstRoadX = 0;
		int firstRoadY = 0;

		boolean foundFirst = false;
		// Finds the x and y coordinate of the first road element in the map.
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map.length; j++) {
				// Searches for the first road element - excludes the painted
				// tiles that
				// are valued 200 adjacent to traffic lights for cosmetic
				// purposes.
				if (map[i][j] != 0 && map[i][j] < 200) {
					firstRoadY = i;
					firstRoadX = j;
					foundFirst = true;
				}
				// Breaks if the first road has been found.
				if (foundFirst) {
					break;
				}
			}
			if (foundFirst) {
				break;
			}
		}

		// Adds the first road element to the list.
		Road firstTile = new Road(firstRoadY, firstRoadX);
		roadList.add(firstTile);

		// Stores the current x and y coordinates of the current road tile.
		int currentX = 0;
		int currentY = 0;

		// Keeps looping through all road tiles until it reaches the first tile
		// (** THE ROAD NETWORK MUST BE A CONTINUOUS CIRCUIT **)
		while (currentX != firstRoadX || currentY != firstRoadY) {
			// If this is the first iteration currentX and currentY will be set
			// to the first tile coords (Bypasses the while condition).
			if (currentX == 0 && currentY == 0) {
				currentX = firstRoadX;
				currentY = firstRoadY;
			}

			// Moves to the next tile, stores the tile as a Road object then
			// adds it to the road tile list.
			Road newRoadTile = moveTo(currentY, currentX);

			// Does not add the first tile twice (which will be the last tile
			// found).
			if (newRoadTile.getX() != firstRoadX
					|| newRoadTile.getY() != firstRoadY) {
				roadList.add(newRoadTile);
			}

			// Sets the current x and y to the new values.
			currentX = newRoadTile.getX();
			currentY = newRoadTile.getY();
		}
	}

	/**
	 * Using the list of road tiles, this method will generate a list of all
	 * vehicles ordered by their position on the road. It makes a new Vehicle()
	 * whenever it finds a road tile of 10,11,12 or 13 (tiles that signify there
	 * is vehicle on it).
	 */
	public void listVehicles() {

		double[][] map = Storage.getInstance().getTempMap();

		// Loops through all the road tiles.
		for (Road road : roadList) {

			// Checks if the current road tile contains a vehicle.
			if (map[road.getY()][road.getX()] == 10
					|| map[road.getY()][road.getX()] == 11
					|| map[road.getY()][road.getX()] == 12
					|| map[road.getY()][road.getX()] == 13) {

				// Creates a new Vehicle and adds it to the list. Uses the
				// position of the current road tile and the locally stored
				// maxSpeed.
				vehicleList.add(new Vehicle(road.getY(), road.getX()));
			}
		}
		// Stores the number of vehicles in Storage so it can be accessed by the
		// gui.
		Storage.getInstance().setNumOfVehicles(vehicleList.size());
	}

	/**
	 * Using the list of road tiles, generates a list of all the traffic lights
	 * ordered by their position on the road. Makes a TrafficLight() whenever it
	 * finds a tile over the value of 100.
	 */
	public void listTrafficLights() {

		double[][] map = Storage.getInstance().getTempMap();

		// Loops through all the map tiles to locate the traffic lights.
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map.length; j++) {

				// Checks if the current road tile contains a traffic light.
				if (map[i][j] > 100 && map[i][j] <= 104) {
					// Creates a new traffic light and adds it to the list using
					// the current map position.
					lightList.add(new TrafficLight(i, j, redLightLength));
					// --------------------DANIEL - THE USER SHOULD INPUT THE
					// LENGTH OF THE RED LIGHT
				}
			}
		}
	}

	/**
	 * Automatically adds a vehicle in every 6 spaces of the road network. Skips
	 * over traffic lights. Can be pressed multiple times to add more vehicles.
	 */
	public void autoAddVehicles() {
		listRoad();
		double[][] map = Storage.getInstance().getTempMap();
		// When this int hits 6 it will allow the for loop to place a vehicle.
		// It then resets to 0.
		int iteration = 0;

		for (Road roadTile : roadList) {

			if (iteration == 6) {
				// Ensures there is no traffic light or vehicle currently
				// present on the tile.
				if (map[roadTile.getY()][roadTile.getX()] > 0
						&& map[roadTile.getY()][roadTile.getX()] <= 4) {
					// Adds the vehicle
					map[roadTile.getY()][roadTile
							.getX()] = map[roadTile.getY()][roadTile.getX()]
									+ 9;
					// Resets the iteration.
					iteration = 0;
				}
			} else {
				iteration++;
			}
		}
	}

	/**
	 * Calculates the percentage of vehicles currently stood still.
	 * 
	 */
	public double calcPercentStoodStill() {

		// Stores the total number of vehicles.
		double totalVehicles = Storage.getInstance().getNumOfVehicles();
		// Will store the number of vehicles stood still.
		double stillVehicles = 0;

		for (Vehicle vehicle : vehicleList) {
			// If the vehicle is still it will increment the above variable.
			if (vehicle.getStoodStill()) {
				stillVehicles++;
			}
		}

		double percentStill = (stillVehicles / totalVehicles) * 100;

		return percentStill;

	}

	/**
	 * Calculates the average number of tiles the closest vehicle is to the
	 * vehicle in front.
	 */
	public double calcAverageVehicleDist() {

		// Will store the individual distances for each vehicle.
		ArrayList<Double> distanceValues = new ArrayList<Double>();

		// Grabs an instance of temp map.
		double[][] map = Storage.getInstance().getTempMap();

		// Loops through each vehicle to gain the distance values.
		for (Vehicle vehicle : vehicleList) {

			// Grabs the current coordinates of the vehicle.
			int x = vehicle.getX();
			int y = vehicle.getY();

			// Stores the current road tile value.
			int currentRoadValue = (int) map[y][x];

			// States whether the closest vehicle has been found yet.
			boolean found = false;

			// The distance to search for another vehicle.
			double searchRadius = 1;

			// Will store the road tile to examine.
			Road roadTile;

			while (!found) {

				// Finds the next road tile to examine.
				roadTile = (moveTo(y, x));

				// If the examined tile contains a vehicle the while loop will
				// finish and the searchRadius will be stored in distanceValues.
				if ((map[roadTile.getY()][roadTile.getX()] >= 10
						&& map[roadTile.getY()][roadTile.getX()] <= 14)
						|| (map[roadTile.getY()][roadTile.getX()] >= 110
								&& map[roadTile.getY()][roadTile
										.getX()] <= 114)) {

					distanceValues.add(searchRadius);
					found = true;
				} else { // If no vehicle is found the search radius will
							// increment and x and y will be set to the examined
							// tile.
					searchRadius++;
					x = roadTile.getX();
					y = roadTile.getY();
				}

			}
		}

		// Sums of the distance values.
		double sum = 0;
		// Count of the distance values.
		double count = 0;

		// Finds the average value of all the distances.
		for (double distanceValue : distanceValues) {

			count++;
			sum = +distanceValue;
		}

		// Calculates the average and stores the value in Storage.
		double average = sum / count;

		return average;
	}

	/**
	 * Calculates the overall average of values in the given list.
	 */
	public double calcListAverage(ArrayList<Double> list) {

		// Stores the sum of all the averages.
		double sum = 0;

		for (int i = 0; i < list.size(); i++) {
			sum += list.get(i);
		}

		double average = sum / list.size();

		return average;
	}
}
