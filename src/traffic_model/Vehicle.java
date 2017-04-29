package traffic_model;

/**
 * Encapsulates a singular vehicle/agent. 
 * @author Daniel Bane
 *
 */
public class Vehicle {
  
  private int currentX;
  private int currentY;

  /**
   * The current speed the vehicle is travelling. Dictates how many times this
   * vehicles move() will be called per tick. Current speed is increased by 1
   * every tick the vehicle moves without stopping.
   * 
   */
  private int currentSpeed;
  /**
   * The maximum speed this vehicle can reach.
   */
  private int maxSpeed;
  
  public Vehicle(int startingY, int startingX, int maxSpeed){
    this.currentX = startingX;
    this.currentY = startingY;
    this.maxSpeed = maxSpeed;
  }
  
  /*
   * Gets the current X position of the vehicle.
   */
  public int getX(){
    return currentX;
  }
  
  /*
   * Gets the current Y position of the vehicle.
   */
  public int getY(){
    return currentY;
  }
  
  /**
   * Sets the current x.
   * @param newX The new x value of the vehicle within the map.
   */
  public void setX(int newX){
    currentX = newX;
  }
  
  /**
   * Sets the current y.
   * @param newY The new y value of the vehicle within the map.
   */
  public void setY(int newY){
    currentY = newY;
  }
}
