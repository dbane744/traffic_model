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
  private double[][] areaMap;
  
  /**
   * Stores the current state of the model (changes constantly as the model is ran). Can be reset by the user to contain areaMap. 
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
  public void setMap(double[][] inputMap) {
    areaMap = inputMap;
    temporaryMap = inputMap;
  }

  /**
   * Gets the area map.
   * 
   * @return
   */
  public double[][] getMap() {

    return this.areaMap;
  }
  
  /**
   * Sets the current state of the temporary map. 
   * @param inputMap The current state of the map while the model is running.
   */
  public void setTempMap(double[][] inputMap){
    
    temporaryMap = inputMap;
  }
  
  /**
   * Gets the current map state.
   * @return The current map state.
   */
  public double[][] getTempMap(){
    
    return temporaryMap;
  }
  
  /**
   * Resets the temporary map to the state of the initially loaded areaMap. 
   */
  public void resetMap(){
    
    temporaryMap = areaMap;
  }
  
  /**
   * Processes the 2Darray stored in areaMap into an Image.
   * 
   * @return An Image of areaMap
   */
  public Image getDataAsImage() {
      //Only works if there is data stored.
      if (areaMap != null) {
          // Copies the data into a 1D array to allow the creation of an Image. 
          double[] areaMap1D= get1DArray();
          // Stores the colour of each pixel in the array/image. 
          int[] pixels = new int[areaMap1D.length];

          for (int i = 0; i < pixels.length; i++) {
              int value = (int) areaMap1D[i];

              // Sets the colour of road pixels(which should be valued 1) to black.
              if (value == 1) {

                  Color color = new Color(0, 0, 0);
                  pixels[i] = color.getRGB();
              }

              // Sets the colour of vehicles(which should be valued 2) to blue.
              else if (value == 2) {

                  Color color = new Color(255, 0, 0);
                  pixels[i] = color.getRGB();
              } 
              
              // Sets the colour of the traffic lights(which should be valued 3) to red.
              else if (value == 3){
                
                Color color = new Color(0, 0, 255);
                pixels[i] = color.getRGB();
              } else {
                  // Sets every other pixel (0 / empty spaces) to white.
                  Color color = new Color(255, 255, 255);
                  pixels[i] = color.getRGB();
              }
          }
          
          // The next bit creates the image using the pixel array.
          
          MemoryImageSource memSource = new MemoryImageSource(areaMap[0].length,
                  areaMap.length, pixels, 0, areaMap[0].length);
          // Creates a panel for the image to sit on.
          Panel panel = new Panel();
          Image image = panel.createImage(memSource);

          return image;

      } else {

          return null;
      }

  }
  
  /**
   * Moves the data from areaMap into a 1D array - allows it to be turned into an image.
   * 
   * @return A 1D array.
   */
  private double[] get1DArray() {

      // Holds the new data temporarily. 
      double[] tempArray = new double[areaMap.length * areaMap[0].length];
      // Cycles through each element in the 2D areaMap array and copies it to tempArray. 
      for (int i = 0; i < areaMap.length; i++) {
          for (int j = 0; j < areaMap[0].length; j++) {
              tempArray[j + (i * (areaMap[0].length))] = areaMap[i][j];

          }
      }
      // Returns the 1D array. 
      return tempArray;
  }
}
