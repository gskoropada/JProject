
package project.logic;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import project.io.*;
import static project.ui.ProjectUI.DATE_FORMAT;

/**
 * 
 * A Portfolio object represents a collection of Project objects.
 * It includes methods to add, remove, replace, find and list objects within the collection.
 * Also includes methods to initialize the collection from a file or from an ArrayList&#60;Project&#62;
 * and to save the collection to a file.
 * @author Gabriel Skoropada
 * @version 2.0
 */
public class Portfolio {

	/** Working ArrayList&#60;Project&#62; object*/
	private ArrayList<Project> portfolio;
	/** Default date format*/
	private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
	
	/** Default constructor. Creates an empty ArrayList&#60;Project&#62; object */
	public Portfolio() {
				
		portfolio = new ArrayList<Project>();
	}
	
	/**
	 * Initializes the portfolio working ArrayList&#60;Project&#62; with data from the file
	 * @param file	String containing the name of the file to use for initialization
	 */
	public void init(String file) {
		
		try {
			portfolio = ProjectIO.open(file);
			System.out.println("** File read successful **\n");
		} catch (ClassNotFoundException | IOException e) {
			System.out.println("** FILE ERROR **\n");
		}
	}
	/** Initializes the portfolio working ArrayList&#60;Project&#62; with data from another
	* ArrayList&#60;Project&#62;
	* @param projects	ArrayList&#60;Project&#62; object containing the data */
	public void init(ArrayList<Project> projects) {
		portfolio = projects;
	}

	/**
	 * Initializes the portfolio working ArrayList&#60;Project&#62; with data from a SQL Server Database
	 */
	public void initDB() {
		try {
			ProjectDB.connect();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			portfolio = ProjectDB.init();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/** Returns the working portfolio as an ArrayList&#60;Project&#62;
	 * @return	the working portfolio as an ArrayList&#60;Project&#62; */	
	public ArrayList<Project> getPortfolio() {
	
		return portfolio;
	}
	
	
	
	/** Returns an ArrayList containing all the OngoingProjects in the portfolio 
	 * @return an Arraylist containing all the OngoingProjects in the portfolio */ 
	public ArrayList<Project> getOngoingProjects() {
		
		ArrayList<Project> oPrjs = new ArrayList<Project>();
		
		for(int i=0; i<portfolio.size();i++){
			if(portfolio.get(i) instanceof OngoingProject) {
				oPrjs.add((OngoingProject) portfolio.get(i));
			}
		}
		
		return oPrjs;
	}
	
	/** Returns an ArrayList containing all the FinishedProjects in the portfolio 
	 * @return an Arraylist containing all the FinishedProjects in the portfolio */
	public ArrayList<Project> getFinishedProjects() {
		
		ArrayList<Project> fPrjs = new ArrayList<Project>();
		
		for(int i=0; i<portfolio.size();i++){
			if(portfolio.get(i) instanceof FinishedProject) {
				fPrjs.add((FinishedProject) portfolio.get(i));
			}
		}
		
		return fPrjs;
	}
	
	/** Adds a Project object to the working ArrayList&#60;Project&#62;
	 * @param p	Project object to be added to the working ArrayList */
	public void add (Project p) {
		
		if (p instanceof OngoingProject) {
			portfolio.add((OngoingProject)p);
		} else if (p instanceof FinishedProject) {
			portfolio.add((FinishedProject) p);
		}
	}
		
	/** Returns the Project object at a given index position. 
	 * @param index	Index representing the project in the ArrayList
	 * @return The requested Project object
	 * @throws IndexOutOfBoundsException Throws the exception when the required index is a negative number or greater than the ArrayList size*/
	public Project get (int index) throws IndexOutOfBoundsException {

		if (index < 0 || index > portfolio.size()) {
			
			throw new IndexOutOfBoundsException();
		}
		
		return portfolio.get(index);
		
	}
	/** Returns the index of a specific Project object with a given Code within the 
	* working ArrayList&#60;Project&#62;. Returns -1 if the Code is not found
	* @param c	String representing the required project code
	* @return	Returns the index of a specific Project object with a given Code within the 
	* working ArrayList&#60;Project&#62;. Returns -1 if the Code is not found */
	public int findByCode (String c) {
		
		
		int index = -1;
		for (int i=0; i<portfolio.size(); i++) {
			if (portfolio.get(i).getCode().equalsIgnoreCase(c)) {
				index = i;
				break;
			}
		}
		
		return index;
	}
	
	/** Returns the index of a specific Project object with a given Code within the 
	* working ArrayList&#60;Project&#62; excluding from the search a specific project.
	* Returns -1 if the Code is not found
	* @param c	String representing the required project code
	* @param exclude	Integer representing the index location of the Project object to exclude
	* @return	Returns the index of a specific Project object with a given Code within the 
	* working ArrayList&#60;Project&#62;. Returns -1 if the Code is not found */
	public int findByCode (String c, int exclude) {
		
		int index = -1;
		for (int i=0; i<portfolio.size(); i++) {
			if (portfolio.get(i).getCode().equalsIgnoreCase(c) && i != exclude) {
				index = i;
				break;
			}
		}
		
		return index;
	}
	
	/**
	 * Counts the OngoingProjects in the working ArrayList
	 * @return	an Integer value representing the number of OngoingProjects in the working ArrayList
	 */
	public int getOngoingCount() {
		
		int ongoing=0;
		
		for(int i = 0; i<portfolio.size();i++) {
			if(portfolio.get(i) instanceof OngoingProject) ongoing++;
		}
		return ongoing;
	}
	
	/**
	 * Counts the FinishedProjects in the working ArrayList
	 * @return	an Integer value representing the number of FinishedProjects in the working ArrayList
	 */
	public int getFinishedCount() {
		
		int finished=0;
		
		for(int i = 0; i<portfolio.size();i++) {
			if(portfolio.get(i) instanceof FinishedProject) finished++;
		}
		return finished;
	}

	/**
	 * Dumps a list of the projects in the ArrayList to the console.
	 */
	public void listProjects() {
		
		System.out.println("\nThere are " + this.getOngoingCount() + " Ongoing Projects and " + this.getFinishedCount() + " Finished Projects.");
		
		System.out.println("\nOngoing Projects\n----------------\n");
		System.out.println(" Prj Code  | Project Name             | Client              | Start Date | Deadline   | Budget      | Completion");
		System.out.println("-----------+--------------------------+---------------------+------------+------------+-------------+-------------");
		 																						   
		for(int x=0;x<portfolio.size();x++){
			if (portfolio.get(x) instanceof OngoingProject) {
				System.out.printf("%-10s", portfolio.get(x).getCode());
				System.out.print(" |");
				System.out.printf("%-25s", portfolio.get(x).getName());
				System.out.print(" |");
				System.out.printf("%-20s", portfolio.get(x).getClient());
				System.out.print(" |");
				System.out.printf("%11s", dateFormat.format(portfolio.get(x).getStartDate()));
				System.out.print(" |");
				System.out.printf("%11s", dateFormat.format(((OngoingProject)portfolio.get(x)).getDeadline()));
				System.out.print(" |");
				System.out.printf("$ %10.2f", ((OngoingProject)portfolio.get(x)).getBudget());
				System.out.print(" |");
				System.out.printf("%10d", ((OngoingProject)portfolio.get(x)).getCompletion());
				System.out.print("%\n");
				
			}
		}
		
			System.out.println("\nFinished Projects\n-----------------\n");
			System.out.println(" Prj Code  | Project Name             | Client              | Start Date | End Date   | Total Cost");
			System.out.println("-----------+--------------------------+---------------------+------------+------------+-------------");
			 																						   
			for(int x=0;x<portfolio.size();x++){
				if (portfolio.get(x) instanceof FinishedProject) {
					System.out.printf("%-10s", portfolio.get(x).getCode());
					System.out.print(" |");
					System.out.printf("%-25s", portfolio.get(x).getName());
					System.out.print(" |");
					System.out.printf("%-20s", portfolio.get(x).getClient());
					System.out.print(" |");
					System.out.printf("%11s", dateFormat.format(portfolio.get(x).getStartDate()));
					System.out.print(" |");
					System.out.printf("%11s", dateFormat.format(((FinishedProject)portfolio.get(x)).getEndDate()));
					System.out.print(" |");
					System.out.printf("$ %10.2f", ((FinishedProject)portfolio.get(x)).getTotalCost());
					System.out.print("\n");
										
				}
			
			}
			
			System.out.print("\n");
		
	}
	/**
	 * Replaces a Project object in the specified index position.
	 * @param p	New Project object to be included in the ArrayList
	 * @param index	Integer representing the location in the ArrayList where the new Project object should be placed
	 */
	public void replaceProject(Project p, int index) {

		portfolio.set(index, p);
		
	}
	/**
	 * Removes a Project object from the specified position
	 * @param index	Integer value representing the location in the ArrayList of the Project object to be removed
	 */
	public void remove(int index) {
		
		portfolio.remove(index);
		
	}
	
	/**
	 * Calls the 'ProjectIO.save' method and passes the file where it should be stored and
	 * the working ArrayList&#60;Project&#62;.
	 * It catches the exception thrown by that method and displays an error message.
	 * @param file	String containing the name of the file where the Arraylist should be saved
	 * @return true if the file was successfully saved. false if there was an error
	 * @see ProjectIO#save
	 */
	public boolean save(String file) {

		
		try {
			ProjectIO.save(file, portfolio);
			System.out.println("** File Saved **\n");
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("** FILE ERROR **\n");
			return false;
		}
		
	}
	
	
}
