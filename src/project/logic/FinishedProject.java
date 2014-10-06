package project.logic;

import java.util.Date;

/** A FinishedProject is a subclass of Project. It represents projects that the organization has already
 * completed. 
* @author Gabriel Skoropada
* @version 2.0
* @see Project
* @see OngoingProject
*/
public class FinishedProject extends Project {
	// Attributes definition
	
	private static final long serialVersionUID = 1744673478219878795L;
	/** a Date object representing the end date of the project */
	private Date endDate;
	/** a double object representing the total cost of the project */
	private double totalCost;
	
	//Constructors definition
	
	/**
	 * Default constructor. Creates a new FinishedProject object with default values.
	 */
	public FinishedProject() {
		super();
	}
	
	/**
	 * Creates a new FinishedProject object with the data passed in the parameters.
	 * @param pC	a String representing the project code
	 * @param pN	a String representing the project name
	 * @param sD	a Date object representing the start date of the project
	 * @param cli	a String representing the project client
	 * @param eD	a Date object representing the end date of the project
	 * @param tC	a double object representing the total cost of the project
	 */
	public FinishedProject(String pC, String pN, Date sD, String cli, Date eD, double tC) {
		super(pC, pN, sD, cli);
		endDate = eD;
		totalCost = tC;		
	}
	
	/**
	 * Creates an FinishedProject object using the attributes from another Project object 
	 * @param p	the Project object to be used as a base for the new FinishedProject object
	 */
	public FinishedProject(Project p) {
		super(p.getCode(), p.getName(), p.getStartDate(), p.getClient());
		if (p instanceof FinishedProject) {
			endDate = ((FinishedProject)p).getEndDate();
			totalCost = ((FinishedProject)p).getTotalCost();
		} else if (p instanceof OngoingProject) {
			endDate=new Date();
			totalCost=((OngoingProject)p).getBudget();
		}
	}
	
	//Setter methods
	/**
	 * Sets the end date of the object
	 * @param eD	a Date object representing the project start date
	 */
	public void setEndDate(Date eD) {
		endDate = eD;
	}
	
	/**
	 * Sets the total cost of the object
	 * @param tC	a double object representing the project total cost
	 */
	public void setTotalCost(double tC) {
		totalCost = tC;
	}
	
	//Getter methods
	/**
	 * 
	 * @return a Date object representing the project end date
	 */
	public Date getEndDate() {
		return endDate;
	}
	/**
	 * 
	 * @return a double object representing the project total cost
	 */
	public double getTotalCost() {
		return totalCost;
	}
	
	// Other Methods
	/** {@inheritDoc}*/
	public String toString() {
		return ("F%"+super.toString()+"%"+super.dateFormat.format(endDate)+"%"+totalCost); // String formatted for file use
	}
	
	/** {@inheritDoc}*/
	public boolean equals(Project p) {
		boolean equal=true;
		
		if(p instanceof FinishedProject) {
			if(!p.getCode().equals(super.getCode())) equal=false;
			if(!p.getClient().equals(super.getClient())) equal=false;
			if(!p.getName().equals(super.getName())) equal=false;
			if(!p.getStartDate().equals(super.getStartDate())) equal=false;
			if(!((FinishedProject)p).getEndDate().equals(endDate)) equal=false;
			if(((FinishedProject)p).getTotalCost() != totalCost) equal=false;
		}
		
		return equal;
	}
	
	/** {@inheritDoc}*/
	public Object[] toTable() {
		
		Object[] tableRow = {super.getCode(), 
							super.getName(),
							super.getClient(),
							super.getStartDate(),
							endDate,
							totalCost
							};
		
		return tableRow;
		
	}
		
}
