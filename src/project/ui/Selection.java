package project.ui;

import java.util.ArrayList;

import project.logic.Portfolio;

/**
 * Uses an ArrayList to keep track of projects selected by the user
 * @author Gabriel Skoropada
 * @version 1.0
 * @see ProjectGUI
 */
public class Selection {

	/**
	 * ArrayList containing the SelectionEntry objects that represent the displayed projects
	 */
	private ArrayList<SelectionEntry> sel;
	
	/**
	 * Creates a new Selection object with an uninitialized ArrayList
	 */
	public Selection () {
		sel = new ArrayList<SelectionEntry>();
	}
	
	/**
	 * Creates a new Selection object from the data from a Portfolio object
	 * @param p	the Portfolio object represented in the GUI
	 */
	public Selection (Portfolio p) {
		init(p);
	}
	
	/**
	 * Initialises a new Selection object from the data from a Portfolio object
	 * @param p	the Portfolio object represented in the GUI
	 */
	public void init(Portfolio p) {
		for(int i=0;i<p.getPortfolio().size();i++) {
			sel.add(new SelectionEntry(p.get(i).getCode(), false));
		}
	}
	
	/**
	 * Adds a project to the selection object, with a default status of not selected
	 * @param c	a String representing the project code to add
	 */
	public void add(String c) {
		sel.add(new SelectionEntry(c, false));
	}
	
	/**
	 * Checks the selection status for a specific project code
	 * @param c	a String representing the project code to check
	 * @return	true if the project is selected or false if it is not
	 */
	public Boolean status(String c) {
		return findByCode(c).getStatus();
	}
	
	/**
	 * Returns the quantity of projects in the Selection object
	 * @return	an Integer representing the number of projects in the Selection object 
	 */
	public int size() {
		return sel.size();
	}
	
	/**
	 * Returns a SelectionEntry object at a specified location of the working ArrayList
	 * @param index	an Integer representing the location where the project is
	 * @return	the SelectionEntry object at the specified index
	 */
	public SelectionEntry get(int index) {
		return sel.get(index);
	}
	
	/**
	 * Removed a specific SelectionEntry from the working ArrayList
	 * @param index	an Integer representing the location of the entry to be removed
	 */
	public void remove(int index) {
		sel.remove(index);
	}
	
	/**
	 * Returns the SelectionEntry object with the specified project code
	 * @param c	a String representing the code to find in the working ArrayList
	 * @return	the requested SelectionEntry object or an empty one if the code is not found
	 */
	public SelectionEntry findByCode(String c) {
		SelectionEntry se = new SelectionEntry();
		
		for(int i=0;i<sel.size();i++) {
			if(sel.get(i).getCode().equalsIgnoreCase(c)) {
				se = sel.get(i);
			}
		}
				
		return se;
	}
	
	/**
	 * Returns a list of the projects selected in the GUI
	 * @return	a String array with the codes of the projects the user selected in the GUI
	 */
	public String[] getSelected() {
		
		ArrayList<String> selected = new ArrayList<String>();
		
		for(int i=0; i<sel.size(); i++) {
			if(sel.get(i).getStatus()) {
				selected.add(sel.get(i).getCode());
			}
		}
		
		String[] selArray = new String[selected.size()]; 
		selArray = selected.toArray(selArray);
		
		return selArray;
	}
	
	/**
	 * Represents the selection status of a project, identified by its code.
	 * @author Gabriel Skoropada
	 * @version 1.0
	 */
	public class SelectionEntry {
		/**
		 * The code of the project
		 */
		private String code;
		/**
		 * The project's selection status. True = selected 
		 */
		private Boolean selected;
		
		/**
		 * Default constructor. Creates an empty SelectionEntry object
		 */
		public SelectionEntry() {
			code = null;
			selected = false;
		}
		
		/**
		 * Creates a SelectionEntry object with the information passed in the parameters
		 * @param c	a String representing the project code
		 * @param s	a boolean value representing the project's selection status
		 */
		public SelectionEntry(String c, Boolean s) {
			code = c;
			selected = s;
		}
		
		/**
		 * Returns the selection state of the object
		 * @return	a Boolean value representing the selection state. True = selected
		 */
		public Boolean getStatus() {
			return selected;
		}
		
		/**
		 * Returns the project code of the object
		 * @return	a String representing the Project code
		 */
		public String getCode() {
			return code;
		}

		/**
		 * Toggles the selection status of the object.
		 */
		public void toggle() {
			selected = !selected;
		}
		
	}
	
}
