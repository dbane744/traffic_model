package traffic_model;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;



/**
 * Implements a mouse listener on the model panel.
 * Grabs the location where the mouse was clicked to allow the placement of cars or traffic lights.
 * Left click on or to the side of the road places a car. Right click places a traffic light.
 * @author Daniel Bane
 *
 */
public class MouseAdp extends MouseAdapter{
  
  /**
   * Stores the model panel.
   */
  JPanel mPanel;
  
  public MouseAdp(JPanel mPanel){
    this.mPanel = mPanel;
  }
  
  public void mouseClicked(MouseEvent me){
    
    // Gets the current instance of temporaryMap in Storage.
    double[][] tempMap = Storage.getInstance().getTempMap();
    
    // Only listens for clicks if there is an image loaded in the panel.
    if(tempMap != null){
    
    // Gets the current panel size (this can alter as the user is able to resize the frame themselves).   
    Dimension currentpanelSize = mPanel.getSize();
    int panelX =  (int)currentpanelSize.getHeight();
    int panelY = (int)currentpanelSize.getWidth();
    
    // Gets the image size.
    BufferedImage image = Storage.getInstance().getDataAsImage();
    int imageX = image.getHeight();
    int imageY = image.getWidth();
    
    // Gets the ratio between the image size and the current panel size.
    double ratio = (double)imageY / (double)panelY;
    
      
    // Returns the point that was clicked. And stores the x and y values for further use.
    Point clickedPoint = me.getPoint();
   
    
      // The raw x and y values are rescaled to the ratio of the image -
      // allows the panel/image to be resized while allowing the user to
      // select a coordinate within the original image.

    int x = (int) (clickedPoint.getX() * ratio);
    int y = (int) (clickedPoint.getY() * ratio);
            System.out.println("modx: " + x + "mody: " + y);
    
      /*
       * The next if/else if statements use for loops to search 15 spaces
       * upwards, downwards, leftwards and rightwards of the clicked point
       * to find a road. If a road is found a traffic light or car will be
       * placed in the temporary map.
       * One click only places one traffic light at the first road element found. Prevents multiple traffic lights being placed.
       * The reason for this is to make it easier for the user to place objects rather than having to directly click the small road.
       * WARNING: This means that inputed road networks cannot be in close proximity or bugs will arise with traffic light placement.
       */
    
    // Places a vehicle in temporaryMap (which is displayed in the panel) if the user left clicks on or near the road.
    if(SwingUtilities.isLeftMouseButton(me)){
      
      // Will be true when the first (and only) road tile is found.
      boolean foundRoad = false;
     
      // Searches 15 spaces to the right of the clicked point for a road.
      for (int i = 0; i < 15; i++) {
        
        System.out.println("x:" + x + "y: " + y);
        
        if(tempMap[y][x + i] <= 4 && tempMap[y][x + i] > 0){
          // Adds 9 to the road element to signify a vehicle.
          tempMap[y][x + i] = (tempMap[y][x + i]) + 9; 
          // Replaces the map in storage with the new map.
          Storage.getInstance().setTempMap(tempMap);
          System.out.println("HIT A ROAD");
          foundRoad = true;
          mPanel.repaint();
        }
        
        // Ends the loop if a road tile has been found.
        if(foundRoad){
          break;
        }
      }
    } 
    
    
    //******* DANIEL - make 3 more for loops for left, up and down
    
    // Places a traffic light if the user right clicks near a road.
    else if(SwingUtilities.isRightMouseButton(me)){
      
      
      System.out.println("RIGHT CLICK");
    }
    
    }
    
  }
}

