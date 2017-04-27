package traffic_model;

import java.util.ArrayList;

import javax.swing.JPanel;

/**
 * Contains variables and methods which are utilised to run the agent-based traffic model.
 * @author User
 *
 */
public class Model extends ModelPanel {
  
  private int runtime;
  private int maxSpeed;
  private double animationSpeed;
  /**
   * Lists every road tile in the order they are placed.
   */
  private ArrayList<Road> roadList;
  private ArrayList<Vehicle> vehicleList;

  
  
  
  /**
   * 
   * @param runtime The number of ticks the model will run for.
   * @param maxSpeed The max number of spaces a vehicle can travel at max speed.
   * @param animationSpeed The speed in which each tick occurs in real-time.
   */
  public Model(int runtime, int maxSpeed, double animationSpeed){
    
    //Stores the arguments locally. 
    this.runtime = runtime;
    this.maxSpeed = maxSpeed;
    this.animationSpeed = animationSpeed;
  }
  
  /**
   * Runs the model.
   */
  public void runModel(){
    
    Vehicle currentVehicle;
    
    // Resets the current map state to the initial state. 
    Storage.getInstance().resetMap();
    
    double[][] map = Storage.getInstance().getMap();
    
    // The tick loop. 
    for (int i = 0; i < runtime; i++) {
      // Moves each vehicle depending on their current speed. 
      for (Vehicle vehicle : vehicleList) {
        

        Road moveTo = moveTo(vehicle.getX(), vehicle.getY());
        
       // Only moves if the new road tile is an empty road tile (which would be in the range of 1 to 4).
      //**************************************************************** DANIEL : add traffic light logic later
        if(map[moveTo.getX()][moveTo.getY()] <= 4){
          
          /*
           * Updates the map to the vehicle's new position.
           */
          
          // The current position minuses 9 to represent the loss of the vehicle.
          map[vehicle.getX()][vehicle.getY()] = (map[vehicle.getX()][vehicle.getY()] - 9);
          // The new position adds 9 to represent the gain of the vehicle.
          map[moveTo.getX()][moveTo.getY()] = (map[moveTo.getX()][moveTo.getY()] + 9);
                  
          /*
           * Updates the vehicle to it's new position. 
           */
          
          vehicle.setX(moveTo.getX());
          vehicle.setY(moveTo.getY());
          
          super.repaint();
        }
      }

    }
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
    
    // Finds the x and y coordinate of the first road element in the map. 
    for (int i = 0; i < map.length; i++) {
      for (int j = 0; j < map.length; j++) {
        if(map[i][j] != 0){
          firstRoadX = i;
          firstRoadY = j;
          break;
        }
      }
    }
    
    // Adds the first road element to the list.
    roadList.add(new Road(firstRoadX, firstRoadY));
    
    // Stores the current x and y coordinates of the current road tile.
    int currentX = 0;
    int currentY = 0;
    
    // Keeps looping through all road tiles till it reaches the first tile (** THE ROAD NETWORK MUST BE A CONTINUOUS CIRCUIT **)
    while(currentX != firstRoadX && currentY != firstRoadY){
      // If this is the first iteration currentX and currentX will be set to the first tile coords (Bypasses the while condition).
      if(currentX == 0 && currentY == 0){
        currentX = firstRoadX;
        currentY = firstRoadY;
      }
      
      // Moves to the next tile, stores the tile as a Road object then adds it to the road tile list.
      Road newRoadTile = moveTo(currentX, currentY);
      roadList.add(newRoadTile);
    }
    
    for (Road road : roadList) {
      System.out.println(road.getX() + "   " + road.getY());
      
    }
    
  }
  
  /**
   * Moves to the next road tile.
   * @param currentX The current x position.
   * @param currentY The current y position.
   * @return The road tile that is to be moved to(which encapsulates the x and y coordinates).
   */
  public Road moveTo(int currentX, int currentY){
    
    // Gets the current state of the map.
    double[][] map = Storage.getInstance().getTempMap();
    // Will store the new road tile to move to.
    Road newRoadTile = new Road(currentX, currentY);
    
    /*
     * Gets the direction the current road tile is currently on
     * (North/east/south/west facing).
     * Values correspond to direction: 1 - North, 2 - East , 3 - South, 4 - West.
     * If the current tile has a vehicle on it that value will be the road tile
     * + 9 or a traffic light + 100. So values in the range of 10-13 have 9
     * subtracted. Values of over 100 have 100 subtracted.
     */
    
    // Gets the current raw value.
    int currentTileValue = (int) map[currentX][currentY];
    // Will store the actual road type value after vehicle/traffic light checks.
    int currentRoadType;
    
    if(currentTileValue <= 10 && currentTileValue >=13){
      currentRoadType = (currentTileValue-9);
    } 
    else if(currentTileValue > 100){
      currentRoadType = (currentTileValue - 100);
    } else{
      // This happens when the current tile value was already in the range of 1 - 4.
      currentRoadType = currentTileValue;
    }
    
    switch (currentRoadType) {
    // If the road is north facing the new tile will have current x - 1;
    case 1: newRoadTile.setX(currentX - 1);
    break;
    // If the road is east facing the new tile will have current y + 1;
    case 2: newRoadTile.setX(currentY + 1);
    break;
    // If the road is south facing the new tile will have current x + 1;
    case 3: newRoadTile.setX(currentX + 1);
    break;
    // If the road is west facing the new tile will have current y - 1;
    case 4: newRoadTile.setX(currentY - 1);
    break;
    
  
    default: //**** DANIEL MAKE AND THROW YOUR OWN EXCPETION
      break;
    }
    
    // Finds the adjoining road if the current road tile is a corner. 
    if(map[newRoadTile.getX()][newRoadTile.getY()] == 0){
      newRoadTile = onCornerFindRoad(currentX, currentY, currentRoadType);
    }
    
      return newRoadTile;
    }
  

  /**
   * If the new road tile is an empty space (tile with a 0) it will recalculate
   * the new tile by searching for surrounding tiles around the current tile
   * that have a different road value from the current (Because an adjoining
   * road will have a different direction value).
   * 
   * @param cornerX The x coordinate of the corner tile.
   * @param cornerY The y coordinate of the corner tile.
   * @param currentRoadType An integer value corresponding to the current road type/direction the road is facing.
   * @return The road tile of the adjoining road.
   */
  public Road onCornerFindRoad(int cornerX, int cornerY, int currentRoadType){
    
    double[][] map = Storage.getInstance().getMap();
    Road newRoadTile = new Road(cornerX, cornerY);
    
    if(map[cornerX][cornerY] == 0){
      // Searches north of the current tile for another road.
      if(map[cornerX - 1][cornerY] != 0 && map[cornerX - 1][cornerY] != currentRoadType){
        newRoadTile.setX(cornerX - 1);
      }
      // Searches east of the current tile for another road.
      else if(map[cornerX][cornerY + 1] != 0 && map[cornerX][cornerY + 1] != currentRoadType){
        newRoadTile.setX(cornerY + 1);
      }
      // Searches south of the current tile for another road.
      else if(map[cornerX + 1][cornerY] != 0 && map[cornerX + 1][cornerY] != currentRoadType){
        newRoadTile.setX(cornerX + 1);
      }
      // Searches west of the current tile for another road.
      else if(map[cornerX][cornerY - 1] != 0 && map[cornerX][cornerY - 1] != currentRoadType){
        newRoadTile.setX(cornerY - 1);
      }
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
    
    double[][] map = Storage.getInstance().getMap();
    
    // Loops through all the road tiles.
     for (Road road : roadList) {
      
       // Checks if the current road tile contains a vehicle.
      if (map[road.getX()][road.getY()] == 10 || map[road.getX()][road.getY()] == 11
              || map[road.getX()][road.getY()] == 12 || map[road.getX()][road.getY()] == 13) {
        
        // Creates a new Vehicle and adds it to the list. Uses the position of the current road tile and the locally stored maxSpeed.
        vehicleList.add(new Vehicle(road.getX(), road.getY(), maxSpeed));
       }
    }
  }
}
