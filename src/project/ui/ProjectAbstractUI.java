package project.ui;

import project.logic.Settings;

/** Abstract User Interface class for JProject */
public abstract interface ProjectAbstractUI {
	
	/**
	 * Starts the User Interface
	 * @param s a Settings object
	 * @return true if the user chose to exit the application; false if there has been a change in
	 * the settings.
	 */
	abstract public boolean start(Settings s);

}
