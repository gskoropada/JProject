package project.io;

import java.io.*;
import java.util.ArrayList;

import project.logic.*;

/**
 * This class performs all I/O functions of the application.
 * @author Gabriel Skoropada
 * @version 1.0
 * @see Portfolio
 */
public class ProjectIO {

	/** 
	 * This method stores the data passed in the ArrayList&#60;Project&#62; object into the file
	 * specified in the <i>file</i> String parameter
	 * @param	file	a String representing the file where the data should be stored
	 * @param	portfolio	an ArrayList&#60;Project&#62; object with the data to be saved
	 * @throws	IOException	when there is any critical error with the file
	 */
	public static void save (String file, ArrayList<Project> portfolio) throws IOException {
		
		
			ObjectOutputStream objOut = new ObjectOutputStream (new FileOutputStream (file));
			objOut.writeObject (portfolio);
			objOut.close();
		}
	
	/** 
	 * The 'open' method returns an ArrayList&#60;Project&#62; object from the file
	 * specified in the 'file' String parameter
	 * @param 	file	a String representing the file to open
	 * @return an ArrayList&#60;Project&#62; with the information stored in the file
	 * @throws	ClassNotFoundException When the classes stored in the file are not found 
	 * @throws	IOException when there is any critical error with the file
	 * @throws	FileNotFoundException When the file that must be opened is not found
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<Project> open (String file) throws ClassNotFoundException, IOException, FileNotFoundException {

		
		
		ObjectInputStream objIn = new ObjectInputStream (new FileInputStream (file));
		ArrayList<Project> portfolio = new ArrayList<Project>();
		portfolio = (ArrayList<Project>) objIn.readObject();
		objIn.close ();

		return portfolio;
	}
	
}
