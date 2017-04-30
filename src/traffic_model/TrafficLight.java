package traffic_model;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Encapsulates the position of a traffic light and its current red light on/off
 * state.
 * 
 * @author User
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
   * On instantiation, generates a random number between 10 and 20. This is the
   * number of ticks the light will take before turning the red light on. It is
   * meant to represent the random nature in which pedestrians cross the road +
   * the fact some pedestrian crossings are more used / take longer to activate
   * than others.
   */
  private int noLightLength = ThreadLocalRandom.current().nextInt(10, 20 + 1);
  
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

}
