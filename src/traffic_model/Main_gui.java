/**
 *Copyright 2017 Daniel Bane
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


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
import javax.swing.JOptionPane;
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
 * 
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
		// This prevents potential race conditions by placing the gui initialise
		// code in the event dispatch thread.
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
		// Size of entire frame - currently commented out due to using pack()
		// instead. Uncomment this line if manual tweaking is required.
		frame.setBounds(0, 0, 1200, 800);
		// Disallows reszing of the frame.
		frame.setResizable(false);
		// Allows the window to close when the user clicks the top right X.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Sets the layout for the frame's content pane - uses Mig layout.
		frame.getContentPane().setLayout(
				new MigLayout("", "[grow,fill]0[]", "[grow,fill]0[]"));

		// Sets up the panels within the frame.

		ControlPanel cpPanel = new ControlPanel();
		StatisticsPanel sPanel = new StatisticsPanel();
		ModelPanel mPanel = new ModelPanel();

		// Adds a mouse listener to the model panel - allows users to place
		// vehicles and traffic lights.
		mPanel.addMouseListener(new MouseAdp(mPanel));

		frame.add(mPanel, "dock east");
		frame.add(cpPanel);
		frame.add(sPanel, "dock south");

		/*
		 * Deals with components within the control panel.
		 */

		// Layout manager for the control panel. Second parameter = rows third
		// parameter = columns.
		cpPanel.setLayout(new MigLayout("", "50[][]", "20[]10[]20[]20[]"));

		// Deals with the time/tick text box.

		// Time spinner text label
		JLabel timeLabel = new JLabel("Number of ticks to run for : ");
		cpPanel.add(timeLabel);

		// JSpinner that will hold the number of ticks to run for.

		// Creates the model that will hold the numbers that can be spinned.
		// First
		// number = default value. Second = min. Third = max. Fourth =
		// increment.
		SpinnerNumberModel model = new SpinnerNumberModel(500, 10, 100000, 100);
		// Creates spinner.
		JSpinner tickSpinner = new JSpinner(model);
		// Adds spinner to cpPanel
		cpPanel.add(tickSpinner, "wrap");

		// Light spinner text label
		JLabel lightLabel = new JLabel(
				"Number of ticks red lights will stay on for : ");
		cpPanel.add(lightLabel);

		// JSpinner that will hold the red light duration for the traffic
		// lights.

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
		// This displays JLabel text on the slider to show the slow end and the
		// fast end.
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
		startButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				// Button only works if the model is not currently running.
				if (!Storage.getInstance().getRuntime()) {
					// This is the model that will run.
					Model model = new Model((int) tickSpinner.getValue(),
							slider.getValue(), (int) lightSpinner.getValue(),
							mPanel, sPanel);
					// Sets the cancel model loop to false in case the user has
					// pressed
					// reset before running the model.
					Storage.getInstance().setCancelModelLoop(false);

					// Runs the model.
					model.runModel();

					// Repaints the live statistics panel to display updated
					// figures.
					sPanel.repaint();
				}
			}
		});
		cpPanel.add(startButton, "wrap");

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
		autoVehiclesButton.setToolTipText(
				"Automatically add a vehicle every 6 road tiles. Press multiple times for more vehicles.");
		autoVehiclesButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				// Button only works if the model is not currently running.
				if (!Storage.getInstance().getRuntime()) {
					if (Storage.getInstance().getTempMap() != null) {
						// Creates a dummy model - this is only created to
						// access access
						// listRoad() and autoAddVehicles() - this instance of
						// the model
						// will
						// not be run.
						Model model = new Model(500, 10, 50, null, null);
						// Adds a vehicle every six spaces. The button can be
						// pressed
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

		// Deals with the menu bar.

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
					// Opens a file dialog and stores the inputed data within
					// Storage as the initial map - the reset button will reset
					// to this map state.
					Storage.getInstance().setInitialMap(IO.readData());
					mPanel.repaint();

				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		mnFile.add(mntmOpen);

		// Deals with the Save menu item.
		// This button allows the user to save the current model display
		// (temporary map in storage).

		JMenuItem mntmSaveResults = new JMenuItem("Save map");
		mntmSaveResults.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {

					double[][] map = Storage.getInstance().getMap();

					// If there is map data loaded, a file dialog will appear
					// allowing the user to save.
					if (map != null) {
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

		JMenuItem mntmSaveStats = new JMenuItem("Save statistics");

		// Saves the currently loaded overall statistics as a txt file.
		mntmSaveStats.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Only saves if there is summary data to be saved.
				if (Storage.getInstance().getOverallDist() != 0) {
					
					// Sets the overall still percentage to the key of 1.0 and the overall distance value to the key of 2.0.
					double [] [] stats = new double[] [] {
					      { 1.0, Storage.getInstance().getOverallStill() },
					      { 2.0, Storage.getInstance().getOverallDist() }
					    } ;
					    
					    IO.writeData(stats);

				}
			}

		});
		
		mnFile.add(mntmSaveStats);

		// Deals with the Exit menu item.

		JMenuItem mntmExit = new JMenuItem("Exit");

		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(1);
			}
		});

		mnFile.add(mntmExit);

		// Deals with the Help menu

		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);

		// Deals with the About menu item.

		JMenuItem mntmAbout = new JMenuItem("About");

		// Opens a message dialog when the about menu item is clicked.
		mntmAbout.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frame,
						"<html>Left click the road to place a vehicle.<br><br>Right click to place a traffic light.<br><br> Use the control panel to set model parameters and press start to begin.<br><br>For more information including how to load road networks consult the readme.",
						"Help", JOptionPane.INFORMATION_MESSAGE);

			}
		});

		mnHelp.add(mntmAbout);
	}
}

