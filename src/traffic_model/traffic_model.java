package traffic_model;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import javax.swing.JSplitPane;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import net.miginfocom.swing.MigLayout;

/**
 * Contains the main method and gui for the application.
 * @author User
 *
 */
public class traffic_model {
  /**
   * Stores the JFrame.
   */
  private JFrame frame;


  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    // This prevents potential race conditions by placing the gui initialise code in the event dispatch thread.
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          traffic_model window = new traffic_model();
          window.frame.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Creates the application's gui and event listeners.
   */
  public traffic_model() {
    initialize();
  }

  /**
   * Initialize the contents of the frame/gui.
   */
  private void initialize() {
    
    // Deals with setting up the frame.
    
    frame = new JFrame("Agent-based traffic wave model");
    // Size of entire frame - currently commented out due to using pack() instead. Uncomment this line if manual tweaking is required.
    frame.setBounds(0, 0, 1200, 800);
    // Sets the frame to fit the preferred size of it's components. 
    //frame.pack();
    // Allows the window to close when the user clicks the top right X.
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    // Sets the layout for the content pane - allows two panels to be placed side by side(1 row and 2 columns).
    
    
    // Makes the grid bag layout.
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[] {18, 3};
    gridBagLayout.rowHeights = new int[]{0, 0};
    // Is responsible for setting the relative widths of the columns. 
    gridBagLayout.columnWeights = new double[]{0.4, 1.0};
    gridBagLayout.rowWeights = new double[]{1.0, Double.MIN_VALUE};
    // Sets the frame to the gridBag layout.
    frame.getContentPane().setLayout(gridBagLayout);
    
    // Makes the model control panel - left hand side panel.
    ControlPanel controlPanel = new ControlPanel();
    GridBagConstraints gbc_cp = new GridBagConstraints();
    //gbc_cp.insets = new Insets(0, 0, 0, 5);
    // The panel will expand horizontally and vertically to fit components added.
    gbc_cp.fill = GridBagConstraints.BOTH;
    gbc_cp.gridx = 0;
    //gbc_cp.gridy = 0;
    frame.getContentPane().add(controlPanel, gbc_cp);
    
    //Makes the image panel - right hand side panel. 
    ModelPanel imagePanel = new ModelPanel();
    GridBagConstraints gbc_ip = new GridBagConstraints();
    //gbc_ip.gridwidth = 1;
    gbc_ip.fill = GridBagConstraints.BOTH;
    gbc_ip.gridx = 1;
    //gbc_ip.gridy = 1;
    
    frame.getContentPane().add(imagePanel, gbc_ip);
    
    //Deals with the menu bar.
    
    JMenuBar menuBar = new JMenuBar();
    frame.setJMenuBar(menuBar);
    
    // Deals with the File menu
    
    JMenu mnFile = new JMenu("File");
    menuBar.add(mnFile);
    
    // Deals with the Open menu item.
    
    JMenuItem mntmOpen = new JMenuItem("Open...");
    // Adds the keyboard shortcut ctrl + o.
    mntmOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, 0));
    mntmOpen.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          // Opens a file dialog and stores the inputed data within Storage.
          Storage.getInstance().setMap(IO.readData());
          imagePanel.repaint();
        } catch (Exception e1) {
          e1.printStackTrace();
        }
      }
    });
    mnFile.add(mntmOpen);
    
    // Deals with the Save menu item.
    
    JMenuItem mntmSaveResults = new JMenuItem("Save results");
    // Adds the keyboard shortcut ctrl + s.
    mntmSaveResults.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0));
    mntmSaveResults.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          // NOTE TO DANIEL : make a method in Storage that grabs the statistics results
          //double[][] outputmap = Storage.getInstance().get***********();
        } catch (Exception e1) {
          e1.printStackTrace();
        }
      }
    });

    mnFile.add(mntmSaveResults);
    
    // Deals with the Save as menu item.
    
    JMenuItem mntmSaveAs = new JMenuItem("Save as...");
    mnFile.add(mntmSaveAs);
   
    // Deals with the Exit menu item.  
    
    JMenuItem mntmExit = new JMenuItem("Exit");
    mnFile.add(mntmExit);
    
    // Deals with the Help menu
    
    JMenu mnHelp = new JMenu("Help");
    menuBar.add(mnHelp);
    
    // Deals with the About menu item.
    
    JMenuItem mntmAbout = new JMenuItem("About");
    mnHelp.add(mntmAbout);


  }
}


/**
 * Encapsulates the image/model panel within the gui.
 * A separate class that inherits from JPanel. Placed in traffic_model.java as it is semantically related to the frame/gui.
 * @author User
 *
 */
class ModelPanel extends JPanel {
  
  

  public ModelPanel() {
    // A matte border that is set to only draw on the left hand side (acts as a single line separating the panels).
    setBorder(BorderFactory.createMatteBorder(0,1,0,0,Color.black));
  }

  public Dimension getPreferredSize() {
      return new Dimension(500,500);
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    // If available, paints the area map data stored in Storage as an image.
    Image image = Storage.getInstance().getDataAsImage();
    
    if(image != null){
      // getWidth() and getHeight() (inherited from JPanel) allows the image to fit into a frame that is resized manually by the user.
      g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
    }
  }
}

/**
 * Encapsulates the control panel within the gui.
 * A separate class that inherits from JPanel. Placed in traffic_model.java as it is semantically related to the frame/gui.
 * @author User
 *
 */
class ControlPanel extends JPanel {

  public Dimension getPreferredSize() {
      return new Dimension(300,500);
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);

  }
}
