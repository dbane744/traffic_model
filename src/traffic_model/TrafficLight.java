package traffic_model;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Encapsulates the position of a traffic light and its current red light on/off
 * state.
 * 
 * @author Daniel Bane
 *
 */
public class TrafficLight {

  /**
   * Stores the x position of the light.
   */
  private int xPosition;
  /**
   * Stores the x position of the light.
   */
  private int yPosition;
  /**
   * Stores whether the red light is on.
   */
  private boolean redLight = false;
  /**
   * Stores the number of ticks the red light stays on for. 
   */
  private int redLightLength;
  /**
   * On instantiation, generates a random number between 10 and 50. This is the
   * number of ticks the light will take before turning the red light on. It is
   * meant to represent the fact that some traffic lights stay on green light for longer than others.
   */
  private int noLightLength = ThreadLocalRandom.current().nextInt(10, 50 + 1);
  
  /**
   * The number of ticks the red light has been on for in the most recent cycle (will reset when redLightLength is reached).
   */
  private int numOfRedTicks;

  /**
   * Stores the number of ticks the light has been off for in the most recent cycle (will reset when noLightLength is reached).
   */
  private int numOfOffTicks = 0;

  TrafficLight(int y, int x, int redLightLength) {
    // Stores arguments locally.
    this.xPosition = x;
    this.yPosition = y;
    this.redLightLength = redLightLength;
    
    // Subtracts 100 from the tile the light is on to turn the light off as the default stance.
    double[][] map = Storage.getInstance().getTempMap();
    map[y][x] = map[y][x] - 100;
  }

  public void tick() {

    // If the light is currently off.
    if (!redLight) {

      numOfOffTicks++;

      // If the off duration has reached its limit the light will turn on.
      if (numOfOffTicks == noLightLength) {
        // Resets the number of off ticks to 0 for the next cycle.
        numOfOffTicks = 0;
        // Sets the state of redLight to true.
        redLight = true;
        // Updates the temporary map in Storage by adding 100 to the position of
        // this traffic light.
        double[][] map = Storage.getInstance().getTempMap();
        map[yPosition][xPosition] = map[yPosition][xPosition] + 100;
      }
    } else { // If the light is currently on.

      numOfRedTicks++;
      // If the on duration has reached its limit the light will turn off.
      if (numOfRedTicks == redLightLength) {
        // Resets the number of red ticks to 0 for the next cycle.
        numOfRedTicks = 0;
        // Sets the state of redLight to false.
        redLight = false;
        // Updates the temporary map in Storage by subtracting 100 to the
        // position of this traffic light.
        double[][] map = Storage.getInstance().getTempMap();
        map[yPosition][xPosition] = map[yPosition][xPosition] - 100;
      }

    }
  }
  
  /**
   * Resets the traffic light tiles in the map to +200.
   */
  public void resetLight(){
    double[][] map = Storage.getInstance().getTempMap();
    
    // If the traffic light is currently off and there is no vehicle on the tile.
    if(map[yPosition][xPosition] > 0 && map[yPosition][xPosition] <= 4){
      map[yPosition][xPosition] = map[yPosition][xPosition] + 100;
    }
    // If the traffic light is currently off and there is a vehicle on the tile (takes into account the 9 value of the vehicle).
    else if(map[yPosition][xPosition] >= 10 && map[yPosition][xPosition] <= 14){
      map[yPosition][xPosition] = map[yPosition][xPosition] + 91;
    } 
    // If the traffic light is currently on and there is a vehicle on the tile.
    // !WARNING! THIS DESTROYS THE VEHICLE - the user should reset the model
    // each time rather than pressing start again if the number of vehicles is
    // important.
    else if (map[yPosition][xPosition] > 104){ 
      map[yPosition][xPosition] = map[yPosition][xPosition] - 9;
    }
    
    // If the light is already on and there is no vehicle on the tile it will do nothing.
  }
}
