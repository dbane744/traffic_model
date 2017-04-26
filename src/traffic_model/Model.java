package traffic_model;

/**
 * Contains variables and methods which are utilised to run the agent-based traffic model.
 * @author User
 *
 */
public class Model {
  
  private int runtime;
  private int maxSpeed;
  private double animationSpeed;
  /**
   * Lists every road tile in the order they are placed.
   */
  private Road[] roadList;
  private Vehicle[] vehicleList;
  
  
  
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
    
    // The tick loop. 
    for (int i = 0; i < runtime; i++) {
      // Moves each vehicle depending on their current speed. 
      for (int j = 0; j < vehicleList.length; j++) {
        currentVehicle = vehicleList[j];
        currentVehicle.move();
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
    int firstRoadX;
    int firstRoadY;
    
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
    
    
    
    
    
  }
  
  /**
   * Moves to the next road tile.
   * @param currentX The current x position.
   * @param currentY The current y position.
   * @return The road tile that is to be moved to(which encapsulates the x and y coordinates).
   */
  public Road move(int currentX, int currentY){
    
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
    
    return newRoadTile;
  }
  
  
  public void addVehicle(int startingX, int startingY){
    
  }
  
  

}
