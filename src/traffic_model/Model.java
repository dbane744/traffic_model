package traffic_model;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

/**
 * Contains variables and methods which are utilised to run the agent-based traffic model.
 * @author User
 *
 */
public class Model{
  
  private int runtime;
  private int maxSpeed;
  private double animationSpeed;
  /**
   * Lists every road tile in the order they are placed.
   */
  private ArrayList<Road> roadList = new ArrayList<Road>();
  private ArrayList<Vehicle> vehicleList = new ArrayList<Vehicle>();
  /**
   * This stores the model panel to paint to.
   */
  private JPanel mPanel;
  
  
  /**
   * 
   * @param runtime The number of ticks the model will run for.
   * @param maxSpeed The max number of spaces a vehicle can travel at max speed.
   * @param animationSpeed The speed in which each tick occurs in real-time.
   */
  public Model(int runtime, int maxSpeed, double animationSpeed, JPanel mPanel){
    
    //Stores the arguments locally. 
    this.runtime = runtime;
    this.maxSpeed = maxSpeed;
    this.animationSpeed = animationSpeed;
    this.mPanel = mPanel;
    
  }
  
  /**
   * Runs the model.
   */
  public void runModel(){
    
    Vehicle currentVehicle;
    
    // Finds and stores all the road tiles.
    listRoad();
    
    // Finds and stores all the vehicles in the vehicle list.
    listVehicles();
    
    System.out.println("vehicle list size = " + vehicleList.size());
    
    double[][] map = Storage.getInstance().getTempMap();
    
    // A timer to wait between vehicle movements to allow the user to see the modelling process.
    Timer timer = new Timer();
    timer.scheduleAtFixedRate(new TimerTask() {
      
      // Stores the number of iterations the model has ran for.
      int iterations = 0;
      

      
      // This will run once at a certain interval defined by the user in 'runtime'.
      @Override
      public void run() {
        
        // Moves each vehicle depending on their current speed. 
        for (Vehicle vehicle : vehicleList) {
          

          Road moveTo = moveTo(vehicle.getY(), vehicle.getX());
          
         // Only moves if the new road tile is an empty road tile (which would be in the range of 1 to 4).
        //**************************************************************** DANIEL : add traffic light logic later
          if(map[moveTo.getY()][moveTo.getX()] <= 4){
            
            /*
             * Updates the map to the vehicle's new position.
             */
            
            // The current position minuses 9 to represent the loss of the vehicle.
            map[vehicle.getY()][vehicle.getX()] = (map[vehicle.getY()][vehicle.getX()] - 9);
            // The new position adds 9 to represent the gain of the vehicle.
            map[moveTo.getY()][moveTo.getX()] = (map[moveTo.getY()][moveTo.getX()] + 9);
                    
            /*
             * Updates the vehicle to it's new position. 
             */
            
            vehicle.setX(moveTo.getX());
            vehicle.setY(moveTo.getY());
            
            //System.out.println("X#" + vehicle.getX());
            System.out.println("Y#" + vehicle.getY());
            
          }
          // Repaints the model panel.
          mPanel.repaint();
        }
        
        iterations++;
        
        // Ends the model when the number of runtime iterations is reached.
        if(iterations == runtime){
          cancel();
        }
        
        
      }
    }, 100, 50);
    // First number is the delay of the user pressing the start button and the model starting.
    // The second(more useful) number is the delay between each run().
    // Both are in milliseconds.
    



    
      
      
      
  }
  
  /**
   * Gets an ordered list of each element in the road network. Follows the direction of the road.
   * Used to get a similarly ranked list of vehicles.
   */
  public void listRoad(){
    
    // Stores the current map state.
    double[][] map = Storage.getInstance().getTempMap();
    // Stores the first road element in the map found. Used as a starting point for storing each road tile. 
    int firstRoadX = 0;
    int firstRoadY = 0;
    
    
    boolean foundFirst = false;
    // Finds the x and y coordinate of the first road element in the map. 
    for (int i = 0; i < map.length; i++) {
      for (int j = 0; j < map.length; j++) {
        if(map[i][j] != 0){
          firstRoadY = i;
          firstRoadX = j;
          foundFirst = true;
        }
        // Breaks if the first road has been found.
        if(foundFirst){
          break;
        }
      }
      if(foundFirst){
        break;
      }
    }
    
    // Adds the first road element to the list.
    Road firstTile = new Road(firstRoadY, firstRoadX);
    roadList.add(firstTile);
    
    // Stores the current x and y coordinates of the current road tile.
    int currentX = 0;
    int currentY = 0;
    
    // Keeps looping through all road tiles until it reaches the first tile (** THE ROAD NETWORK MUST BE A CONTINUOUS CIRCUIT **)
    while(currentX != firstRoadX || currentY != firstRoadY){
      // If this is the first iteration currentX and currentY will be set to the first tile coords (Bypasses the while condition).
      if(currentX == 0 && currentY == 0){
        currentX = firstRoadX;
        currentY = firstRoadY;
      }
      
      // Moves to the next tile, stores the tile as a Road object then adds it to the road tile list.
      Road newRoadTile = moveTo(currentY, currentX);
      
      // Does not add the first tile twice (which will be the last tile found).
      if(newRoadTile.getX() != firstRoadX || newRoadTile.getY() != firstRoadY){
      roadList.add(newRoadTile);
      }
      
      // Sets the current x and y to the new values. 
      currentX = newRoadTile.getX();
      currentY = newRoadTile.getY();
    }
  }
  
  /**
   * Moves to the next road tile.
   * @param currentX The current x position.
   * @param currentY The current y position.
   * @return The road tile that is to be moved to(which encapsulates the x and y coordinates).
   */
  public Road moveTo(int currentY, int currentX){
    
    // Gets the current state of the map.
    double[][] map = Storage.getInstance().getTempMap();
    // Will store the new road tile to move to.
    Road newRoadTile = new Road(currentY, currentX);
    
    /*
     * Gets the direction the current road tile is currently on
     * (North/east/south/west facing).
     * Values correspond to direction: 1 - North, 2 - East , 3 - South, 4 - West.
     * If the current tile has a vehicle on it that value will be the road tile
     * + 9 or a traffic light + 100. So values in the range of 10-13 have 9
     * subtracted. Values of over 100 have 100 subtracted.
     */
    
    // Gets the current raw road value(that may include overlapping vehicles).
    int currentTileValue = (int) map[currentY][currentX];
    
    // Stores the actual road type value after potential vehicle/traffic light removal.
    int currentRoadType = findRoadType(currentTileValue);
    
    switch (currentRoadType) {
    // If the road is north facing the new tile will have current y - 1;
    case 1: newRoadTile.setY(currentY - 1);                                     //System.out.println("CASE 1");
    break;
    // If the road is east facing the new tile will have current x + 1;
    case 2: newRoadTile.setX(currentX + 1);                    //System.out.println("CASE 2");
    break;
    // If the road is south facing the new tile will have current y + 1;
    case 3: newRoadTile.setY(currentY + 1);            //System.out.println("CASE 3");
    break;
    // If the road is west facing the new tile will have current x - 1;
    case 4: newRoadTile.setX(currentX - 1);    //System.out.println("CASE 4");
    break;
    
  
    default: System.out.println("DEFAULT");//**** DANIEL MAKE AND THROW YOUR OWN EXCPETION
      break;
    }
    
    // Finds the adjoining road if the current road tile is a corner. 
    if(map[newRoadTile.getY()][newRoadTile.getX()] == 0){
      newRoadTile = onCornerFindRoad(currentY, currentX, currentRoadType);
    }
    
      return newRoadTile;
    }
  
  /**
   * Finds and returns the road type of the inputed road value (Road type being
   * the direction the road is facing i.e north = 1, east = 2, south = 3, west =
   * 4). The method deals with the fact other overlapping objects such as
   * vehicles may have altered the road value.
   * 
   * @return A value in the range of 1-4 corresponding to the inputed tile's directional road value.
   */
  public int findRoadType(int rawRoadValue){
    
    // Will store the directional value of the road (1-4).
    int actualRoadValue;
    
    // If the tile contains a vehicle.
    if(rawRoadValue >= 10 && rawRoadValue <=13){
      actualRoadValue = (rawRoadValue-9);
    } 
    // If the tile contains a traffic light. 
    else if(rawRoadValue > 100){
      actualRoadValue = (rawRoadValue - 100);
    } else{
      // This happens when the current tile value was already in the range of 1 - 4.
      actualRoadValue = rawRoadValue;
    }
    
    return actualRoadValue;
  }
  

  /**
   * If the new road tile is an empty space (tile with a 0) it will recalculate
   * the new tile by searching for surrounding tiles around the current tile
   * that have a different road value from the current (Because an adjoining
   * road will have a different direction value).
   * 
   * @param cornerY The y coordinate of the corner tile.
   * @param cornerX The x coordinate of the corner tile.
   * @param currentRoadType An integer value corresponding to the current road type/direction the road is facing.
   * @return The road tile of the adjoining road.
   */
  public Road onCornerFindRoad(int cornerY, int cornerX, int currentRoadType){
    
    double[][] map = Storage.getInstance().getMap();
    Road newRoadTile = new Road(cornerY, cornerX);
    
    // Each if statement uses findRoadType() to ensure that only the surrounding
    // road tiles are being assessed(disregarding overlapping vehicles).
    
    // Searches north of the current tile for another road.
      if(map[cornerY - 1][cornerX] != 0 && findRoadType((int)map[cornerY - 1][cornerX]) != currentRoadType){
        newRoadTile.setY(cornerY - 1);
        System.out.println("1");
      }
      // Searches east of the current tile for another road.
      else if(map[cornerY][cornerX + 1] != 0 && findRoadType((int)map[cornerY][cornerX + 1]) != currentRoadType){
        newRoadTile.setX(cornerX + 1);
        System.out.println("2");
      }
      // Searches south of the current tile for another road.
      else if(map[cornerY + 1][cornerX] != 0 && findRoadType((int)map[cornerY + 1][cornerX]) != currentRoadType){
        newRoadTile.setY(cornerY + 1);
        System.out.println("3");
      }
      // Searches west of the current tile for another road.
      else if(map[cornerY][cornerX - 1] != 0 && findRoadType((int)map[cornerY][cornerX - 1]) != currentRoadType){
        newRoadTile.setX(cornerX - 1);
        System.out.println("4");
      }
    

    return newRoadTile;
  }
  
  /**
   * Using the list of road tiles, this method will generate a list of all
   * vehicles ordered by their position on the road. It utilises addVehicle()
   * whenever it finds a road tile of 10,11,12 or 13 (tiles that signify there
   * is vehicle on it).
   */
  public void listVehicles(){
    
    double[][] map = Storage.getInstance().getTempMap();
    
    // Loops through all the road tiles.
     for (Road road : roadList) {
      
       // Checks if the current road tile contains a vehicle.
      if (map[road.getY()][road.getX()] == 10 || map[road.getY()][road.getX()] == 11
              || map[road.getY()][road.getX()] == 12 || map[road.getY()][road.getX()] == 13) {
        
        // Creates a new Vehicle and adds it to the list. Uses the position of the current road tile and the locally stored maxSpeed.
        vehicleList.add(new Vehicle(road.getY(), road.getX(), maxSpeed));
       }
    }

  }
}
