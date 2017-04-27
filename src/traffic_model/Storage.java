package traffic_model;

import java.awt.Color;
import java.awt.Image;
import java.awt.Panel;
import java.awt.image.MemoryImageSource;

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
   * Sets the area map. Also resets the temporary map to the same data.
   * 
   * @param inputMap
   */
  public void setInitialMap(double[][] inputMap) {
    initialMap = inputMap;
    // Also resets the temporary map.
    temporaryMap = inputMap;
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
    
    temporaryMap = initialMap;
  }
  
  /**
   * Processes the 2Darray stored in initialMap into an Image.
   * 
   * @return An Image of initialMap
   */
  public Image getDataAsImage() {
      //Only works if there is data stored.
      if (temporaryMap != null) {
          // Copies the data into a 1D array to allow the creation of an Image. 
          double[] temporaryMap1D= get1DArray();
          // Stores the colour of each pixel in the array/image. 
          int[] pixels = new int[temporaryMap1D.length];

          for (int i = 0; i < pixels.length; i++) {
              int value = (int) temporaryMap1D[i];

              // Sets the colour of road pixels(which should be valued 1,2,3 or 4 depending on their N/E/S/W facing direction) to black.
              if (value == 1 || value == 2 || value == 3 || value == 4) {

                  Color color = new Color(0, 0, 0);
                  pixels[i] = color.getRGB();
              }

              // Sets the colour of vehicles(which should be valued the value of the road tile they are on + 9) to blue.
              // i.e a north facing road (which has a value of 1) would have the value of 10 if a vehicle was on it.
              else if (value == 10 | value == 11 | value == 12 | value == 13) {

                  Color color = new Color(255, 0, 0);
                  pixels[i] = color.getRGB();
              } 
              
              // Sets the colour of the traffic lights(which should be valued 100) to red.
              else if (value == 100){
                
                Color color = new Color(0, 0, 255);
                pixels[i] = color.getRGB();
              } else {
                  // Sets every other pixel (0 / empty spaces) to white.
                  Color color = new Color(255, 255, 255);
                  pixels[i] = color.getRGB();
              }
          }
          
          // The next bit creates the image using the pixel array.
          
          MemoryImageSource memSource = new MemoryImageSource(temporaryMap[0].length,
                  temporaryMap.length, pixels, 0, temporaryMap[0].length);
          // Creates a panel for the image to sit on.
          Panel panel = new Panel();
          Image image = panel.createImage(memSource);

          return image;

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
}
