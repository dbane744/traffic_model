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
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.util.ArrayList;

/**
 * Stores large data sets used by the application. Includes map/road network data and statistical results of the model.
 * This class is a singleton to allow easy access by multiple classes.
 * 
 * @author Daniel Bane
 *
 */
public class Storage {

	/**
	 * Stores the instance of this class -  a singleton.
	 */
	private static Storage storage = null;

	/**
	 * Stores the initial map state including the road network.
	 */
	private double[][] initialMap;

	/**
	 * Stores the current state of the model (changes constantly as the model is ran). Can be reset by the user to contain initialMap. 
	 */
	private double[][] temporaryMap;

	/**
	 * Is set to true when reset map is called. Tells the model vehicle movement loop to cancel.
	 */
	private boolean cancelModelLoop = false;
	/**
	 * If true, inverts the background and road colours.
	 */
	private boolean invertColours;
	/**
	 * Stores the number of vehicles in the current temporary map.
	 */
	private int numOfVehicles = 0;
	/**
	 * Stores whether the data is currently being altered (the model is running).
	 */
	private boolean runtime = false;
	/**
	 * Stores the percentage of vehicles currently stood still.
	 * @return The percentage as a double.
	 */
	private int percentStoodStill;
	/**
	 * Stores the average number of tiles each vehicle is from the vehicle in front.
	 */
	private double averageVehicDist;
	/**
	 * Stores the overall average percentage of vehicles stood still.
	 */
	private double overallStill = 0;
	/**
	 * Stores the overall average distance between each vehicle..
	 */
	private double overallDist = 0;



	// Defeats instantiation 
	private Storage(){
	}

	/**
	 * Grabs the instance of the class (as a singleton).
	 * 
	 * @return A link to this singleton. 
	 */
	public static Storage getInstance(){
		if(storage == null){
			storage = new Storage();
		}
		return storage;
	}

	/**
	 * Returns the boolean value of whether to cancel the vehicle move loop.
	 * @return 
	 */
	public boolean getCancelModelLoop(){
		return cancelModelLoop;
	}

	/**
	 * Sets whether to cancel the vehicle move loop.
	 * @param value
	 */
	public void setCancelModelLoop(boolean value){
		cancelModelLoop = value;
	}



	/**
	 * Sets the area map. Also resets the temporary map to the same data.
	 * 
	 * @param inputMap
	 */
	public void setInitialMap(double[][] inputMap) {

		initialMap = inputMap;

		temporaryMap = new double[initialMap.length][initialMap[0].length];
		// Copies the data from initialMap into temporaryMap.
		for (int i = 0; i < initialMap.length; i++) {
			for (int j = 0; j < initialMap[0].length; j++) {
				temporaryMap[i][j] = initialMap[i][j];
			}
		}
	}

	/**
	 * Sets the current state of the temporary map. 
	 * @param inputMap The current state of the map.
	 */
	public void setTempMap(double[][] inputMap){

		temporaryMap = inputMap;
	}

	/**
	 * Gets the area map.
	 * 
	 * @return
	 */
	public double[][] getMap() {

		return this.initialMap;
	}



	/**
	 * Gets the current map state.
	 * @return The current map state.
	 */
	public double[][] getTempMap(){

		return temporaryMap;
	}

	/**
	 * Resets the temporary map to the state of the initially loaded initialMap. 
	 */
	public void resetMap(){
		cancelModelLoop = true;
		for (int i = 0; i < initialMap.length; i++) {
			for (int j = 0; j < initialMap[0].length; j++) {
				temporaryMap[i][j] = initialMap[i][j];
			}
		}
	}

	/**
	 * Processes the 2Darray stored in initialMap into an Image.
	 * 
	 * @return An Image of initialMap
	 */
	public BufferedImage getDataAsImage() {
		//Only works if there is data stored.
		if (temporaryMap != null) {
			// Copies the data into a 1D array to allow the creation of an Image. 
			double[] temporaryMap1D= get1DArray();
			// Stores the colour of each pixel in the array/image. 
			int[] pixels = new int[temporaryMap1D.length];

			for (int i = 0; i < pixels.length; i++) {
				int value = (int) temporaryMap1D[i];

				// Sets the colour of road pixels(which should be valued 1,2,3 or 4 depending on their N/E/S/W facing direction) to black.
				// If invert colour is true the road will be white instead.
				if (value == 1 || value == 2 || value == 3 || value == 4) {
					Color color;
					if(!invertColours){
						color = new Color(0, 0, 0);
					} else{
						color = new Color(255, 255, 255);
					}

					pixels[i] = color.getRGB();
				}

				// Sets the colour of vehicles(which should be valued the value of the road tile they are on + 9) to blue.
				// i.e a north facing road (which has a value of 1) would have the value of 10 if a vehicle was on it.
				else if (value == 10 || value == 11 || value == 12 || value == 13) {

					Color color = new Color(0, 0, 255);
					pixels[i] = color.getRGB();
				} 

				// Sets the colour of the traffic lights(which should be valued >100) and the two adjacent tiles(which are valued as 200) to red.
				else if (value > 100){

					Color color = new Color(255, 0, 0);
					pixels[i] = color.getRGB();
				} else {
					Color color;

					if(!invertColours){
						// Sets every other pixel (0 / empty spaces) to white. If invert colours is true it they will be set to black.
						color = new Color(255, 255, 255);
					} else{
						color = new Color(0, 0, 0);
					}
					pixels[i] = color.getRGB();
				}
			}

			// The next bit creates the image using the pixel array.

			MemoryImageSource memSource = new MemoryImageSource(temporaryMap[0].length,
					temporaryMap.length, pixels, 0, temporaryMap[0].length);
			// Creates a panel for the image to sit on.
			Panel panel = new Panel();
			Image image = panel.createImage(memSource);

			BufferedImage bufferedImage = imageToBufferedImage(image);

			return bufferedImage;

		} else {

			return null;
		}

	}

	/**
	 * Moves the data from temporaryMap into a 1D array - allows it to be turned into an image.
	 * 
	 * @return A 1D array.
	 */
	private double[] get1DArray() {

		// Holds the new data temporarily. 
		double[] tempArray = new double[temporaryMap.length * temporaryMap[0].length];
		// Cycles through each element in the 2D temporaryMap array and copies it to tempArray. 
		for (int i = 0; i < temporaryMap.length; i++) {
			for (int j = 0; j < temporaryMap[0].length; j++) {
				tempArray[j + (i * (temporaryMap[0].length))] = temporaryMap[i][j];

			}
		}
		// Returns the 1D array. 
		return tempArray;
	}

	// This method turns an image into a buffered image. Code was obtained from http://www.rgagnon.com/javadetails/java-0601.html
	public BufferedImage imageToBufferedImage(Image im) {

		BufferedImage bi = new BufferedImage
				(im.getWidth(null),im.getHeight(null),BufferedImage.TYPE_INT_RGB);
		Graphics bg = bi.getGraphics();
		bg.drawImage(im, 0, 0, null);
		bg.dispose();
		return bi;
	}

	/**
	 * Toggles the invert colours boolean.
	 */
	public void invertColours(){
		invertColours = !invertColours;
	}

	/**
	 * Sets the number of vehicles in the currently stored temporary map.
	 * @return
	 */
	public void setNumOfVehicles(int num){

		numOfVehicles = num;
	}

	/**
	 * Gets the number of vehicles in the currently stored temporary map.
	 * @return
	 */
	public int getNumOfVehicles(){
		
			return numOfVehicles;
	}

	/**
	 * Sets whether the stored data is undergoing alteration (the model is running).
	 */
	public void setRuntime(boolean input){

		runtime = input;
	}

	/**
	 * Gets the runtime state.
	 * @return Boolean value.
	 */
	public boolean getRuntime(){

		return runtime;
	}

	/**
	 * Stores the percentage of vehicles currently stood still.
	 */
	public void setPercentStill(int percent){

		percentStoodStill = percent;
	}

	/**
	 * Gets the percentage of vehicles currently stood still.
	 * @return The percentage in double format.
	 */
	public int getPercentStill(){

		return percentStoodStill;
	}

	/**
	 * Stores the average distance between each vehicle and the vehicle in front.
	 * @param Double value
	 */
	public void setAverageVehicDist(double value){

		averageVehicDist = value;
	}

	/**
	 * Gets the average distance between each vehicle and the vehicle in front.
	 * @return Double value.
	 */
	public double getAverageVehicDist(){

		return averageVehicDist;
	}
	/**
	 * Sets the over percentage of vehicles that were stood still.
	 * @param input Double value
	 */
	public void setOverallStill(double input){
		
		overallStill = input;
	}
         /**
	 * Gets the overall average percentage of vehicles that were stood still.
	 * @return Double value.
	 */
	public double getOverallStill(){
		
		return overallStill;
	}
	/**
	 * Stores the overall average distance between each vehicle and the vehicle in front.
	 * @param input Double value.
	 */
	public void setOverallDist(double input){
		
		overallDist = input;
	}
	/**
	 * Gets the overall average distance between each vehicle and the vehicle in front.
	 * @return Double value.
	 */
	public double getOverallDist(){
		
		return overallDist;
	}
}
