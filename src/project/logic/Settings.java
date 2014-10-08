package project.logic;

import java.io.IOException;
import java.io.Serializable;

import project.io.ProjectIO;
/**
 * An object of this class will store the user preferences and program defaults
 * @version 1.0 - Created on 16/sep/2014
 * @author Gabriel Skoropada
 *
 */
public class Settings implements Serializable {

	private static final long serialVersionUID = 1L;

	/** a String representing the default date format to be used across the application. <strong>Default: dd/MM/yyyy</strong>  */
	protected String defaultDateFormat;
	/** a boolean flag. True for GUI; false for text based UI. <strong>Default: true</strong> */
	protected boolean GUI;
	/** a String storing the working file name. <strong>Default: portfolio.obj</strong> */
	protected String workingFile;
	/** a String storing the defaults file name. <strong>Default: defaults.obj</strong> */
	protected String defaultsFile;
	/** a boolean flag. True if the back end DB should be updated dynamically; false if DB should not dynamically updated. <strong>Default: true</strong>*/
	protected boolean updateDB;
	
	/** Default constructor. Creates a new Settings object with default values */
	public Settings() {
		defaultDateFormat = "dd/MM/yyyy";
		GUI = true;
		workingFile = "portfolio.obj";
		defaultsFile = "defaults.obj";
		updateDB = true;
	}
	
	
	/** @return a String representing the default date format for the application */
	public String getDefaultDateFormat() {
		return defaultDateFormat;
	}
	
	/** @param defaultDateFormat a String representing the default date format for the application */
	public void setDefaultDateFormat(String defaultDateFormat) {
		this.defaultDateFormat = defaultDateFormat;
	}
	/** @return true if the program will run in Graphical User Interface mode; false if the program will run in a text based interface */
	public boolean isGUI() {
		return GUI;
	}
	/** @param gUI true if the program will run in Graphical User Interface mode; false if the program will run in a text based interface */
	public void setGUI(boolean gUI) {
		GUI = gUI;
	}
	/** @return a String representing the working file name */
	public String getWorkingFile() {
		return workingFile;
	}
	/** @param workingFile a String representing the working file name */
	public void setWorkingFile(String workingFile) {
		this.workingFile = workingFile;
	}
	/** @return a String representing the defaults file name */
	public String getDefaultsFile() {
		return defaultsFile;
	}
	/** @param defaultsFile a String representing the working file name */
	public void setDefaultsFile(String defaultsFile) {
		this.defaultsFile = defaultsFile;
	}
	/** @return true if the back end DB should updated dynamically; false if the back end DB should not be updated dynamically */
	public boolean isUpdateDB() {
		return updateDB;
	}
	/** @param updateDB true if the back end DB should updated dynamically; false if the back end DB should not be updated dynamically */
	public void setUpdateDB(boolean updateDB) {
		this.updateDB = updateDB;
	}
	
	/**
	 * Saves the Settings object to the default settings file
	 * @return true if settings successfully saved.
	 */
	public boolean save() {
		boolean saved = false;
		
		try {
			ProjectIO.saveSettings(this);
			saved = true;
		} catch(IOException e) {
			
		}
		
		return saved;
	}
	
	/**
	 * Reads the settings from the default file and updates the object's value.
	 * @return true if read operation was successful
	 */
	public boolean read() {
		boolean read = false;
		Settings s = new Settings();
		try {
			s = ProjectIO.readSettings();
			read = true;
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		defaultDateFormat = s.getDefaultDateFormat();
		GUI = s.isGUI();
		workingFile = s.getWorkingFile();
		defaultsFile = s.getDefaultsFile();
		updateDB = s.isUpdateDB();
		
		System.out.println("** Settings read **");		
		System.out.println(toString());

		return read;
	}
	/**
	 * Overrides the default toString() method.
	 * @return a String with a verbose description of the object's attributes 
	 */
	public String toString() {
		return " Default date format: " + defaultDateFormat + 
				"\n GUI: " + GUI +
				"\n Working file: " + workingFile +
				"\n Defaults file: " + defaultsFile +
				"\n Dynamic DB Update: " + updateDB;
	}
	
}
