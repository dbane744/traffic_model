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

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 * Encapsulates the statistics panel within the gui.
 * 
 * Shows dynamic statistics for each model tick and summary statistics for the
 * overall averages when the model has finished. The two statistics are the
 * percentage of vehicles stood still and the average distance between each
 * vehicle and the vehicle in front.
 * 
 * @author Daniel Bane
 *
 */
public class StatisticsPanel extends JPanel {

	// Stores the model for the live statistics table.
	/**
	 * @uml.property name="lModel"
	 * @uml.associationEnd
	 */
	private DefaultTableModel lModel;

	// Stores the model for the summary statistics table.
	/**
	 * @uml.property name="oModel"
	 * @uml.associationEnd
	 */
	private DefaultTableModel oModel;

	// Stores the live statistics table.
	/**
	 * @uml.property name="sTable"
	 * @uml.associationEnd
	 */
	private JTable sTable;

	// Stores the summary statistics table.
	/**
	 * @uml.property name="oTable"
	 * @uml.associationEnd
	 */
	private JTable oTable;
	
	/**
	 * Gets the preferred size of the panel which is 500x500.
	 */
	public Dimension getPreferredSize() {
		return new Dimension(500, 500);
	}

	/**
	 * Creates the JTables to store the statistics data.
	 */
	public void makeTables() {

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
			if (i == 0)
				column.setPreferredWidth(300);
			// Second column will hold the numeric values.
			if (i == 1)
				column.setPreferredWidth(140);
		}

		// Grabs the model that will hold the dynamic data.
		lModel = (DefaultTableModel) sTable.getModel();
		lModel.setValueAt("Number of vehicles in model : ", 0, 0);
		lModel.setValueAt("Percentage of vehicles stood still : ", 1, 0);
		lModel.setValueAt(
				"<html>" + "The average number of tiles between <br> each vehicle and the vehicle in front : ",
				2, 0);

		// Adds the table to the panel.
		this.add(sTable);

		// Table to hold overall statistics that are shown after model
		// completion. 2 rows , 2 columns.
		// These stats will be calculated and displayed after the model has ran.
		// They show overall average figures.

		int numOfRows2 = 2;
		int numOfColumns2 = 2;

		// Overall average figures table.
		oTable = new JTable(numOfRows2, numOfColumns2);
		oTable.setRowHeight(40);

		// Sets the column widths.
		for (int i = 0; i < numOfColumns2; i++) {
			TableColumn column = oTable.getColumnModel().getColumn(i);
			// First column is much wider to hold descriptive text.
			if (i == 0)
				column.setPreferredWidth(300);
			// Second column will hold the numeric values.
			if (i == 1)
				column.setPreferredWidth(140);
		}

		// Grabs the model that will hold the overall average data.
		oModel = (DefaultTableModel) oTable.getModel();
		oModel.setValueAt("Average percentage of vehicles stood still :", 0, 0);
		oModel.setValueAt("Overall average distance between vehicles :", 1, 0);

		// Adds the table to the panel.
		this.add(oTable);

	}
	/**
	 * Repaints the panel.
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		/*
		 * Paints the values of the dynamic statistics using values stored in
		 * Storage.
		 */

		lModel.setValueAt(Storage.getInstance().getNumOfVehicles(), 0, 1);
		lModel.setValueAt(Storage.getInstance().getPercentStill(), 1, 1);
		lModel.setValueAt(Storage.getInstance().getAverageVehicDist(), 2, 1);

		// Paints summary statistics if the overall distance value is not 0.
		if (Storage.getInstance().getOverallDist() != 0) {

			// Obtains the summary statistics from Storage.
			oModel.setValueAt(Storage.getInstance().getOverallStill(), 0, 1);
			oModel.setValueAt(Storage.getInstance().getOverallDist(), 1, 1);
		}

		// Repaints the tables.
		this.add(sTable);
		this.add(oTable);
	}
}