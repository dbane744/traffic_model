package traffic_model;

/**
 * Encapsulates the x and y position of one road tile.
 * @author User
 *
 */
public class Road {

  private int x;
  private int y;
  
  public Road(int x, int y){
    this.x = x;
    this.y = y;
  }
  
  public int getX(){
    return x;
  }
  
  public int getY(){
    return y;
  }
  
  public void setX(int x){
    this.x = x;
  }
  
  public void setY(int y){
    this.y = y;
  }
}
