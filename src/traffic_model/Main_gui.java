package traffic_model;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JSplitPane;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Point;

import net.miginfocom.swing.MigLayout;

/**
 * Contains the main method and gui for the application.
 * @author User
 *
 */
public class Main_gui {
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
          Main_gui window = new Main_gui();
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
  public Main_gui() {
    initialize();
  }

  /**
   * Initialize the contents of the frame/gui.
   */
  private void initialize() {
    
    /*
     * Deals with setting up the frame.
     */
    
    frame = new JFrame("Agent-based traffic wave model");
    // Size of entire frame - currently commented out due to using pack() instead. Uncomment this line if manual tweaking is required.
    frame.setBounds(0, 0, 1200, 800);
    // Disallows reszing of the frame.
    frame.setResizable(false);
    // Allows the window to close when the user clicks the top right X.
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    // Sets the layout for the frame's content pane - uses Mig layout.
    frame.getContentPane().setLayout(new MigLayout("", "[grow,fill]0[]", "[grow,fill]0[]"));
    
    // Sets up the panels within the frame.
    
    ControlPanel cpPanel = new ControlPanel();
    StatisticsPanel sPanel = new StatisticsPanel();
    ModelPanel mPanel = new ModelPanel();
    
    // Adds a mouse listener to the model panel - allows users to place vehicles and traffic lights.
    mPanel.addMouseListener(new MouseAdp(mPanel));
    
    frame.add(mPanel, "dock east");
    frame.add(cpPanel);
    frame.add(sPanel, "dock south");
    
    /*
     * Deals with components within the control panel.
     */
    
    
    // Layout manager for the control panel. Second paramter = rows third paraemter = columns.
    cpPanel.setLayout(new MigLayout("","[][]", "50[]150[]20[]20[]"));
    
    // Deals with the time/tick text box.
    
    // Text label
    JLabel timeLabel = new JLabel("Number of ticks to run for: ");
    cpPanel.add(timeLabel);
    
    // Text box user will enter into.
    // timeField sets the number of ticks the model will run for. 
    JFormattedTextField timeField = new JFormattedTextField();
    // Initial value of the text field. 
    timeField.setValue(new Double(100));
    // Width of the text field
    timeField.setColumns(5);
    cpPanel.add(timeField, "wrap");
    
    // Deals with the start button.
    
    JButton startButton = new JButton("START");
    startButton.setToolTipText("Click to start the model");
    startButton.addActionListener(new ActionListener(){
      
      @Override
      public void actionPerformed(ActionEvent e){
        // DANIEL LET THE USER PUT THESSE VALUES IN ***********
        Model model = new Model(300, 3, 10, mPanel);
        model.runModel();
      }
    });
    cpPanel.add(startButton, "wrap");
    
    // Deals with the pause button.
    
    JButton pauseButton = new JButton("PAUSE");
    startButton.setToolTipText("Click to pause the model");
    cpPanel.add(pauseButton, "wrap");
    
    // Deals with the stop button.
    
    JButton resetButton = new JButton("RESET");
    startButton.setToolTipText("Click to stop and reset the model");
    resetButton.addActionListener(new ActionListener() {
      
      @Override
      public void actionPerformed(ActionEvent e) {
        
        Storage.getInstance().resetMap();
        mPanel.repaint();
      }
    });
    cpPanel.add(resetButton);
    
    /*
     * Deals with the menus.
     */
    
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
          // Opens a file dialog and stores the inputed data within Storage as the initial map - the reset button will reset to this map state.
          Storage.getInstance().setInitialMap(IO.readData());
          mPanel.repaint();
         
          
          
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
 * @author Daniel Bane
 *
 */
class ModelPanel extends JPanel {
  
  public ModelPanel() {
    // A matte border that is set to only draw on the left hand side (acts as a single line separating the panels).
    setBorder(BorderFactory.createMatteBorder(0,1,0,0,Color.black));
  }

  public Dimension getPreferredSize() {
      return new Dimension(800,800);
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    // If available, paints the area map data stored in Storage as an image.
    // Casts into a BufferedImage as this is suited to alterting individual pixels.
    BufferedImage image = Storage.getInstance().getDataAsImage();
    
    if(image != null){
      
      // getWidth() and getHeight() (inherited from JPanel) allows the image to fit into a frame that is resized manually by the user.
      g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null);
    }
  }
}

/**
 * Encapsulates the control panel within the gui.
 * A separate class that inherits from JPanel. Placed in traffic_model.java as it is semantically related to the frame/gui.
 * @author Daniel Bane
 *
 */
class ControlPanel extends JPanel {
  
  public ControlPanel(){
    // A matte border that is set to only draw on the bottom side (acts as a single line separating the panels).
    setBorder(BorderFactory.createMatteBorder(0,0,1,0,Color.black));
  }

  public Dimension getPreferredSize() {
      return new Dimension(400,400);
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    
    g.drawString("Control Panel", 0, 20);
  }
}

/**
 * Encapsulates the statistics panel within the gui.
 * A separate class that inherits from JPanel. Placed in traffic_model.java as it is semantically related to the frame/gui.
 * @author Daniel Bane
 *
 */
class StatisticsPanel extends JPanel {

  public Dimension getPreferredSize() {
      return new Dimension(500, 500);
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    
    g.drawString("Statistics", 0, 20);
  }
}
