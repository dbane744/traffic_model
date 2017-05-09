package traffic_model;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * Deals with the inputting and outputting of text files.
 * @author Daniel Bane
 *
 */
public class IO {

	/**
	 * Reads in a text file and returns it as a 2D array.
	 * 
	 * @return A 2D array that models a road network.
	 */
	public static double[][] readData() {

		BufferedReader br = null;
		// Creates and shows the file dialog to the user.
		FileDialog fd = new FileDialog(new Frame(), "Choose a file",
				FileDialog.LOAD);
		fd.setVisible(true);
		File f = null;
		// Creates the file.
		if ((fd.getDirectory() != null) || (fd.getFile() != null)) {

			f = new File(fd.getDirectory() + fd.getFile());
		}

		try {
			br = new BufferedReader(new FileReader(f));
		} catch (FileNotFoundException | NullPointerException ex) {

			// Ends the method if no file is selected and throws a
			// RuntimeException
			throw new RuntimeException(ex);
		}

		// Counts the number of lines in the file.
		int lines = -1;
		String textIn = "";
		String[] file = null;

		try {
			while (textIn != null) {

				textIn = br.readLine();
				lines++;
			}
		} catch (IOException ioe) {

			// Throws a RuntimeException for unforeseen circumstances.
			throw new RuntimeException(ioe);
			// Always closes the connection.
		} finally {
			try {

				br.close();
			} catch (IOException ioe2) {

				throw new RuntimeException(ioe2);
			}
		}
		// Now that the number of lines has been counted the data will be read.
		try {
			br = new BufferedReader(new FileReader(f));

		} catch (FileNotFoundException fnfe) {
			return null;
		}
		// The file String array is instantiated using the previously counted
		// number
		// of lines.
		file = new String[lines];

		try {
			// Writes the data into the 1D array.
			for (int i = 0; i < file.length; i++) {

				file[i] = br.readLine();
			}
		} catch (IOException e) {

			throw new RuntimeException(e);
			// Always closes the connection.
		} finally {

			try {
				br.close();
			} catch (IOException e2) {
				throw new RuntimeException(e2);
			}
		}

		double[][] data = new double[lines][];
		// parses the Strings in the file into doubles and into a 2D array.
		for (int i = 0; i < lines; i++) {
			// Delimits individual strings using the "," character on a line by
			// line
			// basis.
			StringTokenizer st = new StringTokenizer(file[i], ",");
			data[i] = new double[st.countTokens()];
			int j = 0;

			while (st.hasMoreTokens()) {

				data[i][j] = Double.parseDouble(st.nextToken());
				j++;
			}
		}

		return data;
	}

	/**
	 * Writes the currently displayed image to a text file by default.
	 * 
	 * @param dataIn
	 *            The 2d array that will be saved as a text file.
	 */
	public static void writeData(double[][] dataIn) {

		BufferedWriter bw = null;

		FileDialog fd = new FileDialog(new Frame(), "Save the file",
				FileDialog.SAVE);
		// Sets the default file name to 'output' and extension to .txt
		fd.setFile("output.txt");
		fd.setVisible(true);
		File f2 = null;
		// The user selects a file from the dialogue.
		if ((fd.getDirectory() != null) || (fd.getFile() != null)) {

			f2 = new File(fd.getDirectory() + fd.getFile());
		}

		try {
			// Wraps a buffered writer around a file writer.
			bw = new BufferedWriter(new FileWriter(f2));

		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}

		try {
			// Writes each line.
			for (int i = 0; i < dataIn.length; i++) {
				for (int j = 0; j < dataIn[i].length; j++) {

					bw.write(String.valueOf(dataIn[i][j]) + ",");
				}
				System.out.println("");
				bw.newLine();
			}
			bw.flush();

		} catch (IOException ioe2) {
			throw new RuntimeException(ioe2);
		} finally {

			try {
				// Always closes the connection.
				bw.close();

			} catch (IOException ioe3) {
				throw new RuntimeException(ioe3);
			}
		}
	}
}
