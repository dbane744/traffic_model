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
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Hashtable;
import java.awt.event.ActionEvent;
import javax.swing.JSplitPane;
import javax.swing.JTable;

import java.awt.FlowLayout;
import java.awt.Font;
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
    
    // Layout manager for the control panel. Second parameter = rows third parameter = columns.
    cpPanel.setLayout(new MigLayout("","50[][]", "20[]10[]20[]20[]"));
    
    // Deals with the time/tick text box.
    
    // Time spinner text label
    JLabel timeLabel = new JLabel("Number of ticks to run for : ");
    cpPanel.add(timeLabel);
    
    // JSpinner that will hold the number of ticks to run for.

    // Creates the model that will hold the numbers that can be spinned. First
    // number = default value. Second = min. Third = max. Fourth = increment.
    SpinnerNumberModel model = new SpinnerNumberModel(500, 10, 100000, 100);
    // Creates spinner.
    JSpinner tickSpinner = new JSpinner(model);
    // Adds spinner to cpPanel
    cpPanel.add(tickSpinner, "wrap");
    
    // Light spinner text label
    JLabel lightLabel = new JLabel("Number of ticks red lights will stay on for : ");
    cpPanel.add(lightLabel);
    
    // JSpinner that will hold the red light duration for the traffic lights.
    
    SpinnerNumberModel lightModel = new SpinnerNumberModel(50, 10, 200, 10);
    JSpinner lightSpinner = new JSpinner(lightModel);
    cpPanel.add(lightSpinner, "wrap");
    
    
    // Text label
    JLabel animationSpeedLabel = new JLabel("Animation speed: ");
    cpPanel.add(animationSpeedLabel);
    
    // Creates the animation slider.
    // Slider min: 1, max:60, default value:30. 
    JSlider slider = new JSlider(1, 60, 30);
    slider.setPaintTicks(true);
    slider.setMajorTickSpacing(10);
    slider.setPaintTicks(true);
    
    // Cerates the slider label table.
    // This displays JLabel text on the slider to show the slow end and the fast end.
    Hashtable table = new Hashtable();
    table.put(new Integer(60), new JLabel("Slow"));
    table.put(new Integer(1), new JLabel("Fast"));
    slider.setLabelTable(table);
    slider.setPaintLabels(true);
    
    // Adds the slider to the cpPanel.
    cpPanel.add(slider, "wrap");
    
    // Deals with the start button.
    
    JButton startButton = new JButton("START");
    startButton.setToolTipText("Click to start the model");
    startButton.addActionListener(new ActionListener(){
      
      @Override
      public void actionPerformed(ActionEvent e) {
        
        // Button only works if the model is not currently running.
        if (!Storage.getInstance().getRuntime()) {
          // This is the model that will run.
          Model model = new Model((int) tickSpinner.getValue(), 3, slider.getValue(), (int) lightSpinner.getValue(),
                  mPanel, sPanel);
          // Sets the cancel model loop to false in case the user has pressed
          // reset or stop before running the model.
          Storage.getInstance().setCancelModelLoop(false);
          // Runs the model.
          model.runModel();
          // Repaints the live statistics panel to display updated figures.
          sPanel.repaint();
        }
      }
    });
    cpPanel.add(startButton, "wrap");
    
    // Deals with the stop button.
    
    JButton stopButton = new JButton("STOP");
    stopButton.setToolTipText("Click to stop the model");
    stopButton.addActionListener(new ActionListener() {
      
      @Override
      public void actionPerformed(ActionEvent e) {
        
        // Stops currently running model. 
        Storage.getInstance().setCancelModelLoop(true);
      }
    });
    cpPanel.add(stopButton, "wrap");
    
    // Deals with the reset button.
    
    JButton resetButton = new JButton("RESET");
    resetButton.setToolTipText("Click to stop and reset the model");
    resetButton.addActionListener(new ActionListener() {
      
      @Override
      public void actionPerformed(ActionEvent e) {
        
        Storage.getInstance().resetMap();
        mPanel.repaint();
      }
    });
    cpPanel.add(resetButton, "wrap");
    
    // Deals with the auto add vehicles button.
    
    JButton autoVehiclesButton = new JButton("Auto add vehicles");
    autoVehiclesButton.setToolTipText("Automatically add a vehicle every 6 road tiles. Press multiple times for more vehicles.");
    autoVehiclesButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        // Button only works if the model is not currently running.
        if (!Storage.getInstance().getRuntime()) {
          if (Storage.getInstance().getTempMap() != null) {
            // Creates a dummy model - this is only created to access access
            // listRoad() and autoAddVehicles() - this instance of the model
            // will
            // not be run.
            Model model = new Model(500, 3, 10, 50, null, null);
            // Adds a vehicle every six spaces. The button can be pressed
            // multiple times to add more vehicles.
            model.autoAddVehicles();
            mPanel.repaint();
          }
        }
      }
    });
    cpPanel.add(autoVehiclesButton);
    
    // Deals with the invert colours button.
    
    JButton invertButton = new JButton("Invert Colours");
    invertButton.setToolTipText("Invert the background and road colours");
    invertButton.addActionListener(new ActionListener() {
      
      @Override
      public void actionPerformed(ActionEvent e) {
        Storage.getInstance().invertColours();
        mPanel.repaint();
      }
    });
    cpPanel.add(invertButton);
    
    
    /*
     * Deals with components in the statistics panel.
     */
    
    // Makes the JTables within sPanel.
    sPanel.makeTables();
    

    
    
    
    
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
    // This button allows the user to save the current model display (temporary map in storage).
    
    JMenuItem mntmSaveResults = new JMenuItem("Save map");
    mntmSaveResults.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          
          double[][] map = Storage.getInstance().getMap();
          
          // If there is map data loaded, a file dialog will appear allowing the user to save.
          if(map != null){
            IO.writeData(Storage.getInstance().getTempMap());
          }
        } catch (Exception e1) {
          e1.printStackTrace();
        }
      }
    });

    mnFile.add(mntmSaveResults);
    
    // Deals with the Save Statistics menu item.
    // This button allows the user to save the currently loaded statistics.
    
    JMenuItem mntmSaveAs = new JMenuItem("Save statistics");
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
  }
}

/**
 * Encapsulates the statistics panel within the gui.
 * A separate class that inherits from JPanel. Placed in traffic_model.java as it is semantically related to the frame/gui.
 * @author Daniel Bane
 *
 */
class StatisticsPanel extends JPanel {

  // Stores the model for the live statistics table.
  private DefaultTableModel lModel;
  
  // Stores the live statistics table.
  private JTable sTable;
  
  public Dimension getPreferredSize() {
      return new Dimension(500, 500);
  }
  
  /**
   * Creates the JTables to store the statistics data.
   */
  public void makeTables(){
    
    // Table to hold live statistics. 3 rows , 2 columns. 
    // These stats will update dynamically as the model runs.
    
    
    int numOfRows1 = 3;
    int numOfColumns1 = 2;
    
    sTable = new JTable(numOfRows1, numOfColumns1);
    sTable.setRowHeight(40);
    
    
    // Sets the column widths.
    for (int i = 0; i < numOfColumns1; i++) {
      TableColumn column = sTable.getColumnModel().getColumn(i);
      // First column is much wider to hold descriptive text.
      if(i == 0) column.setPreferredWidth(300);
      // Second column will hold the numeric values.
      if(i == 1) column.setPreferredWidth(60);
    }
    

    // Grabs the model that will hold the dynamic data.
    lModel = (DefaultTableModel)sTable.getModel();
    lModel.setValueAt("Number of vehicles in model : ", 0, 0);
    lModel.setValueAt("Percentage of vehicles stood still : ", 1, 0);
    lModel.setValueAt("<html>" + "The average number of tiles between <br> each vehicle and another vehicle : ", 2, 0);
    
    // Adds the table to the panel.
    this.add(sTable);
    
    
    
    // Table to hold overall statistics that are shown after model completion. 2 rows , 2 columns. 
    // These stats will be calculated and displayed after the model has ran. They show overall average figures. 
    
    int numOfRows2 = 2;
    int numOfColumns2 = 2;
    
    // Overall average figures table.
    JTable oTable = new JTable(numOfRows2, numOfColumns2);
    oTable.setRowHeight(40);
    
    // Sets the column widths.
    for (int i = 0; i < numOfColumns2; i++) {
      TableColumn column = oTable.getColumnModel().getColumn(i);
      // First column is much wider to hold descriptive text.
      if(i == 0) column.setPreferredWidth(300);
      // Second column will hold the numeric values.
      if(i == 1) column.setPreferredWidth(60);
    }
    
    // Grabs the model that will hold the overall average data.
    DefaultTableModel oModel = (DefaultTableModel)oTable.getModel();
    oModel.setValueAt("Average percentage of vehicles stood still :", 0, 0);
    oModel.setValueAt("Overall average distance :", 1, 0);
    
    // Adds the table to the panel.
    this.add(oTable);
    
  }
  
  

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    
    /*
     * Paints the values of the dynamic statistics using values stored in Storage.
     */
    
    lModel.setValueAt(Storage.getInstance().getNumOfVehicles(), 0, 1);
    lModel.setValueAt(Storage.getInstance().getPercentStill(), 1, 1);
    lModel.setValueAt(Storage.getInstance().getAverageVehicDist(), 2, 1);
    
    this.add(sTable);
  }
}
