package project.logic;

import java.util.Date;


 
 /** An OngoingProject is a subclass of Project. It represents projects that the organization is
 * currently undertaking.
 * @author Gabriel Skoropada
 * @version 2.0
 * @see Project
 * @see FinishedProject
 */
public class OngoingProject extends Project {

	// Attributes definition
	
	private static final long serialVersionUID = 7919637131965796331L;
	/** A Date object representing the project deadline */
	private Date deadline;
	/** A double object representing the project budget */
	private double budget;
	/** An int object representing the project completion status*/
	private int completion;
	
	//Constructors definition
	
	/**
	 * Default constructor. Creates a new OngoingProject object with default values.
	 */
	public OngoingProject() {
		super();
		deadline=null;
		budget=0;
		completion=0;
	}
	
	/**
	 * Creates an OngoingProject object with the data passed in the parameters
	 * @param pC	a String representing the project code
	 * @param pN	a String representing the project names
	 * @param sD	a Date object representing the project start date 
	 * @param cli	a String representing the project client
	 * @param d		a Date object representing the project deadline
	 * @param b		a double object representing the project budget
	 * @param comp	an int object representing the project completion status
	 */
	public OngoingProject(String pC, String pN, Date sD, String cli, Date d, double b, int comp) {
		super(pC, pN, sD, cli);
		deadline = d;
		budget = b;
		completion = comp;	
	}
	
	/**
	 * Creates an OngoingProject object using the attributes from another Project object 
	 * @param p	the Project object to be used as a base for the new OngoingProject object
	 */
	public OngoingProject(Project p) {
		super(p.getCode(), p.getName(), p.getStartDate(), p.getClient());
		if (p instanceof FinishedProject) {
			deadline = null;
			budget = ((FinishedProject)p).getTotalCost();
			completion = 0;
		} else if (p instanceof OngoingProject) {
			deadline = ((OngoingProject)p).getDeadline();
			budget = ((OngoingProject)p).getBudget();
			completion = ((OngoingProject)p).getCompletion(); 
		}
	}
// Setter methods
	/**
	 * Sets the object's deadline
	 * @param d	a Date object representing the project deadline
	 */
	public void setDeadline (Date d){
		deadline = d;
	}
	
	/**
	 * Sets the object's budget
	 * @param b	a double object representing the project budget
	 */
	public void setBudget (double b) {
		budget = b;
	}
	
	/**
	 * Sets the object's completion
	 * @param comp	an int object representing the project completion status
	 */
	public void setCompletion (int comp) {
		completion = comp;
	}

// Getter methods
	/**
	 * 
	 * @return a Date object with the project deadline
	 */
	public Date getDeadline () {
		return deadline;
	}
	
	/**
	 * 
	 * @return a double object with the project budget
	 */
	public double getBudget () {
		return budget;
	}
	
	/**
	 * 
	 * @return an int object with the project completion
	 */
	public int getCompletion() {
		return completion;
	}
	
// Other Methods
	
	/** {@inheritDoc} */
	public String toString() {
		return ("O%"+super.toString()+"%"+super.dateFormat.format(deadline)+"%"+budget+"%"+completion); // String formatted for file use
	}
	
	/** {@inheritDoc} */
	public boolean equals(Project p) {
		boolean equal=true;
		
		if(p instanceof OngoingProject) {
			if(!p.getCode().equals(super.getCode())) equal=false;
			if(!p.getClient().equals(super.getClient())) equal=false;
			if(!p.getName().equals(super.getName())) equal=false;
			if(!p.getStartDate().equals(super.getStartDate())) equal=false;
			if(!((OngoingProject)p).getDeadline().equals(deadline)) equal=false;
			if(((OngoingProject)p).getBudget() != budget) equal=false;
			if(((OngoingProject)p).getCompletion() != completion) equal=false;
		} else {
			equal = false;
		}
		
		return equal;
	}
	
	/** {@inheritDoc} */
	public Object[] toTable() {
		
		Object[] tableRow = {super.getCode(), 
							super.getName(),
							super.getClient(),
							super.getStartDate(),
							deadline,
							budget,
							completion};
		
		return tableRow;
		
	}
	
}
