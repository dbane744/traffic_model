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

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Hashtable;
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
    cpPanel.setLayout(new MigLayout("","50[][]", "50[]10[]20[]20[]"));
    
    // Deals with the time/tick text box.
    
    // Text label
    JLabel timeLabel = new JLabel("Number of ticks to run for: ");
    cpPanel.add(timeLabel);
    
    // JSpinner that will hold the number of ticks to run for.

    // Creates the model that will hold the numbers that can be spinned. First
    // number = deafult value. Second = min. Third = max. Fourth = increment.
    SpinnerNumberModel model = new SpinnerNumberModel(500, 10, 100000, 100);
    // Creates spinner.
    JSpinner spinner = new JSpinner(model);
    // Adds spinner to cpPanel
    cpPanel.add(spinner, "wrap");
    
    
    // Text label
    JLabel animationSpeedLabel = new JLabel("Animation speed: ");
    cpPanel.add(animationSpeedLabel);
    
    // Creates the animation slider.
    // Slider min: 1, max:20, default value:10. 
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
      public void actionPerformed(ActionEvent e){

        // This is the model that will run.
        Model model = new Model((int)spinner.getValue(), 3, slider.getValue(), mPanel);
        // Sets the cancel model loop to false in case the user has pressed reset or stop before running the model.
        Storage.getInstance().setCancelModelLoop(false);
        // Runs the model.
        model.runModel();
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
        
        if(Storage.getInstance().getTempMap() != null){
          // Creates a dummy model - this is only created to access access
          // listRoad() and autoAddVehicles() - this instance of the model will
          // not be run.
          Model model = new Model(500, 3, 10, mPanel);
          // Adds a vehicle every six spaces. The button can be pressed multiple times to add more vehicles.
        model.autoAddVehicles();
        mPanel.repaint();
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
