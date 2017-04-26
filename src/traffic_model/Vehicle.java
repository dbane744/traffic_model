package traffic_model;

/**
 * Encapsulates a singular vehicle/agent. 
 * @author Daniel Bane
 *
 */
public class Vehicle {
  
  private int currentX;
  private int currentY;
  private int previousX;
  private int previousY;
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
  
  public Vehicle(int startingX, int startingY, int maxSpeed){
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
  
  
  public void move(){
    
    
  }
  

}
