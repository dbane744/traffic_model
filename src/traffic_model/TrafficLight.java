package traffic_model;

/**
 * Encapsulates the position of a traffic light and its current red light on/off state. 
 * @author User
 *
 */
public class TrafficLight {

  private int xPosition;
  private int yPosition;
  private boolean redLight = false;
  
  TrafficLight(int y, int x){
    this.xPosition = x;
    this.yPosition = y;
  }
  
}
