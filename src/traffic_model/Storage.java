package traffic_model;

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
   * Stores the map of the area including the road network.
   */
  private double[][] areaMap;

  
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
   * Sets the area map
   * 
   * @param inputMap
   */
  public void setMap(double[][] inputMap) {
    System.out.println("MAP IS ABOUT TO BE SET");
    areaMap = inputMap;
    System.out.println("THE MAP WAS SET");
  }

  /**
   * Gets the area map.
   * 
   * @return
   */
  public double[][] getMap() {

    System.out.println("THE MAP WAS GOTTEN");
    return this.areaMap;
  }
}
