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
    
    // Resets the current map state to the initial state. 
    Storage.getInstance().resetMap();
    
    // The tick loop. 
    for (int i = 0; i < runtime; i++) {
      for (int j = 0; j < array.length; j++) {
        
      }
    }
  }
  
  

}
