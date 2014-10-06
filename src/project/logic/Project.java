package project.logic;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import static project.ui.ProjectUI.DATE_FORMAT;

/**
 * A Project object stores basic information of projects being executed by the organization.<br>
 * The Project class is abstract and is implemented by the two Project subtypes: OngoingProject and FinishedProject
 * @author Gabriel Skoropada
 * @version 2.0
 * @see OngoingProject
 * @see FinishedProject
 */
abstract public class Project implements Serializable {
	
	private static final long serialVersionUID = -5164726797294313603L;
	
	// Attributes definition
	
	/** A String storing the project code */
	protected String projectCode;
	/** A String storing the project name */
	protected String projectName;
	/** A String storing the project client */
	protected String client;
	/** A Date object storing the start date of the project */
	protected Date startDate;
	
	/** SimpleDateFormat object used to parse strings into Date objects */
	protected SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
			
	//Constructors definition
	
	/**
	 * Default constructor. Creates a Project object with default values
	 */
	public Project() {
		projectCode = "P0000";
		projectName = null;
		startDate = null;
		client = null;
	}
	/**
	 * Creates a Project object with the data passed in the parameters.
	 * @param pCode a String representing the project code
	 * @param pName a String representing the project name
	 * @param SDate a Date object representing the project start date
	 * @param Cl 	a String representing the project client
	 */
	public Project(String pCode, String pName, Date SDate, String Cl) {
		projectCode = pCode;
		projectName = pName;
		startDate = SDate;
		client = Cl;
	}
	
	//Setter methods
	/**
	 * Sets the code for the object
	 * @param pCode	a String representing the project code
	 */
	public void setCode (String pCode) {
		projectCode = pCode;
	}
	
	/**
	 * Sets the name for the object
	 * @param pName a String representing the project name
	 */
	public void setName (String pName) {
		projectName = pName;
	}
	
	/**
	 * Sets the start date for the object
	 * @param d	a Date object representing the project start date
	 */
	public void setStartDate (Date d){  
		startDate = d;
	}
	
	/**
	 * Sets the client for the object
	 * @param Cl	a String representing the project client
	 */
	public void setClient (String Cl) {
		client = Cl;
	}
	
	//Getter methods
	
	/**
	 * @return a String representing the project code
	 */
	public String getCode () {
		return projectCode;
	}
	
	/**
	 * 
	 * @return a String representing the project name
	 */
	public String getName () {
		return projectName;
	}
	
	/**
	 * 
	 * @return a Date object representing the project start date
	 */
	public Date getStartDate () {  
		return startDate;
	}

	/**
	 * 
	 * @return a String representing the project client
	 */
	public String getClient () {
		return client;
	}

	
	//Other Methods
	
	/**
	 * Encodes the project data with the object attributes separated by the <i>%</i> token
	 * @return	a String representation of the object
	 */
	public String toString() {
		return (projectCode + "%"+projectName+"%"+dateFormat.format(startDate)+"%"+client); // String formatted for file use
		
	}
	
	/**
	 * Abstract method to be implemented in the subtype classes. Compares this project object with the <i>p</i> parameter object.
	 * @param p	A Project object to be compared with this object
	 * @return	true if all the attributes in both objects match
	 * @since version 2.0
	 */
	abstract public boolean equals(Project p); //Method to be implemented by subclasses
	
	/**
	 * Abstract method to be implemented in the subtype classes. Returns the object's attributes in an Object array. 
	 * Used in the GUI to build the project tables in the main window.
	 * @return	an Object[] containing this object's attributes.
	 * @since version 2.0
	 * @see project.ui.ProjectGUI
	 */
	abstract public Object[] toTable();
	
}
