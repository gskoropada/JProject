/*
 * Text based interface. 
 * TODO Pending issues:
 * 		- Implement user preferences?
 * TODO	Check 'add new project' deadline validation
 * 		- Implement DB connectivity 
 * 		
 */

package project.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import project.logic.FinishedProject;
import project.logic.OngoingProject;
import project.logic.Portfolio;
import project.logic.Project;
import project.logic.Settings;

/**
 * ProjectUI is the text based interface that drives the Project Management application.
 * It uses a Portfolio object to interact with the stored data and modify it.
 * @author Gabriel Skoropada
 * @version	2.1 - Changed main() to start() on 16/sep/14
 * @see ProjectGUI
 * @see Portfolio
 * @see Project
 * @see OngoingProject
 * @see FinishedProject
 * 
 */
public class ProjectUI {
	
	/** Scanner object used to get input from the user */
	private static Scanner input = new Scanner(System.in);
	/** Boolean object used to flag if there are unsaved changes */
	private static boolean saved=true;
	/** Integer used to get the user menu selection */
	private static int choice;
	/** Integer representing the index location of a project within the working Portfolio object*/
	private static int index;
	/** Working Portfolio object */
	private static Portfolio portfolio = new Portfolio();
	private static Settings config = new Settings();
	/** Project object used across this class to temporarily store Project information*/
	private static Project p = null;
	/** Default date format String for this application */
	public static final String DATE_FORMAT="dd-MM-yyyy";
	/** Default SimpleDateFormat object initialised with the default data format string */
	private static SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
	
	/** A String array containing all the menu options to be displayed */
	private static final String[] MENU = {
		"1. List all projects",
		"2. Add project",
		"3. Modify existing project",
		"4. Delete project",
		"--------------------------",
		"S. Save current status",
		"R. Restore to default data set",
		"Q. Quit"};
	
	/** A String array containing all the menu options' values */
	private static final  String[] MENU_VALUE = {"1","2","3","4","S","R","Q"};
	/** A String representing the name of the working file */
	private static final String WORKING_FILE = "portfolio.obj";
	/** A String representing the name of the default dataset file */
	private static final String DEFAULTS_FILE = "defaults.obj";

	public static void start(Settings s) {
		
		config = s;
		
		if(config.isUpdateDB()) {
			portfolio.initDB();
		} else {
			portfolio.init(WORKING_FILE);
		}
		splash();
		
		portfolio.listProjects();
		
		do {
			
			displayMenu();
			choice = getMenuOption();
			
			/* values for usrChoice:
			 * 0 = List all projects
			 * 1 = Add project
			 * 2 = Modify project
			 * 3 = Delete project
			 * 4 = Save
			 * 5 = Restore
			 * 6 = Quit
			 */
			
			switch (choice){
			case 0: //List projects
				portfolio.listProjects();
				break;
			case 1: //Add Project
				
				p = getNewProjectData();
				
				parseProject(p);
				
				if(confirm("Add this project?")) {
					if(!config.isUpdateDB()) {
						saved = false;
					}
					portfolio.add(p, config.isUpdateDB());
				}
				
				break;
			case 2: //Modify project
				
				index = projectCodeInput();
				
				if (index != -2) {
						
					parseProject(portfolio.get(index));
					
					if(confirm("Edit this project?")) {
						
						p=getExistingProjectData(portfolio.get(index));
		
						if (!portfolio.get(index).equals(p)) {
							parseProject(p);
							if(confirm("Is the new data correct?")) {
								if(!config.isUpdateDB()) {
									saved = false;
								}
								portfolio.replaceProject(p,index,config.isUpdateDB());
							} 
						}
					}
				}
				break;
			case 3: //Delete project
				
				index = projectCodeInput();
				
				if (index != -2) {
							
					parseProject(portfolio.get(index));
					
					if(confirm("Delete this project?")) {
						
						if(!config.isUpdateDB()) {
							saved = false;
						}
						portfolio.remove(index, config.isUpdateDB());
					}
				}
				
				break;
			case 4: //Save file
				
				saved = portfolio.save(WORKING_FILE);
				
				break;
			case 5: //Restore defaults
				
				if(confirm("Restoring to defaults will erase all you data\nAre you sure?")){
					portfolio.init(DEFAULTS_FILE);
				}
				break;
			case 6: //Quit
				choice = -1;
				if(!saved && confirm("Do you want to save your changes?")){
					saved = portfolio.save(WORKING_FILE);
				}
				System.out.println("Good Bye!\n");
				break;
			}
			
		} while (choice != -1);
		
	}
	
	/**
	 * Requests the user for confirmation. Displays a custom message in the following format
	 * <p><strong>msg + " (Y/N) "</strong></p>
	 * and returns the user choice.
	 * @param msg	A String with the message to be displayed to the user.
	 * @return	a Boolean value representing the user choice. True if user input is "Y"
	 */
	private static boolean confirm(String msg) {

		
		boolean confirm = false;
		
		System.out.print(msg + " (Y/N) ");
		String choice = input.nextLine();
		
		if (choice.toUpperCase().equals("Y")) confirm = true;
		
		return confirm;
	}
	
	/**
	 * Requests the necessary user input to create a new Project object. Validates the data before returning the object. 
	 * @return	A Project object with data entered by the user.
	 */
	private static Project getNewProjectData() {
		
		boolean valid=false;
		String choice;
		
		Project newProject = new OngoingProject();
		
		do {
			System.out.print("Select project type ([O]ngoing / [F]inished)? ");
			choice = input.nextLine();
			
			if (choice.toUpperCase().equals("O") || choice.toUpperCase().equals("F") ) {
				valid=true;
			} 
		} while(!valid);
		
		if (choice.toUpperCase().equals("F")) {
			
			newProject = new FinishedProject((OngoingProject)newProject); // Converts an OngoingProject to a FinishedProject
			
		}		
		
		String in;
		boolean exists;
		do {
			System.out.print("Project Code: ");
			in = input.nextLine();
			exists = false;
			if(portfolio.findByCode(in) == -1) { 
				newProject.setCode(in.toUpperCase());
			} else {
				exists = true;
				System.out.println("** Project already exists **");
			}
		} while (exists);
		
			System.out.print("Project Name: ");
			in = input.nextLine();
			newProject.setName(in);
							
			do {
				System.out.print("Start Date (" + DATE_FORMAT + "): ");
				in = input.nextLine();
			} while (!validateDate(in));
			
			try {
				newProject.setStartDate(dateFormat.parse(parseDate(in)));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.print("Client: ");
			in = input.nextLine();
			newProject.setClient(in);
		
		if (newProject instanceof OngoingProject) {	
			
			do {
				System.out.print("Deadline (" + DATE_FORMAT + "): ");
				in = input.nextLine();
			} while (!validateDate(in, newProject.getStartDate(), 1));
			
			try {
				((OngoingProject) newProject).setDeadline(dateFormat.parse(parseDate(in)));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			valid = false;			
			do {
				System.out.print("Budget: $");
				
				in = input.nextLine();
				
				try {
					((OngoingProject)newProject).setBudget(Double.parseDouble(in));
					valid = true;
				} catch (Exception ex) {
					valid = false;
					System.out.println("Invalid value!\n");
				}
				
			} while (!valid);
			
			
			valid = false;
			do {
				System.out.print("Completion % ");
				
				in = input.nextLine();
				
				try {
					((OngoingProject)newProject).setCompletion(Integer.parseInt(in));
					valid = true;
				} catch (Exception ex) {
					valid = false;
					System.out.println("Invalid value!\n");
				}
				
			} while (!valid);
	
		} else if (newProject instanceof FinishedProject) {
			
			do {
				System.out.print("End Date (" + DATE_FORMAT + "): ");
				in = input.nextLine();
			} while (!validateDate(in, newProject.getStartDate(), 1));
			
			try {
				((FinishedProject) newProject).setEndDate(dateFormat.parse(parseDate(in)));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			valid = false;			
			do {
				System.out.print("Total Cost: $");
				
				in = input.nextLine();
				
				try {
					((FinishedProject)newProject).setTotalCost(Double.parseDouble(in));
					valid = true;
				} catch (Exception ex) {
					valid = false;
					System.out.println("Invalid value!\n");
				}
				
			} while (!valid);
			
		}

		return newProject;
		
	} //End getNewProjectData method
	
	/**
	 * Returns a Project object with updated data by requesting input from user. Data is validated before returning the object
	 * @param p	The Project object that should be modified.
	 * @return	A Project object with data entered by the user.
	 */
	public static Project getExistingProjectData(Project p) {
	
		Project prj = null;
		String in;
		
		if (p instanceof OngoingProject) {
			prj = new OngoingProject(p);
		} else if (p instanceof FinishedProject) {
			prj = new FinishedProject(p);
		}
	
		boolean valid = false;
		System.out.println("\nPress enter to keep current value.");
		
		if (prj instanceof OngoingProject) {
			
			if (confirm("Is the project now finished?")){
				prj = new FinishedProject((OngoingProject)prj);
			}
		}
				
		boolean exists=false;
			do {	
				System.out.print("Project Code(current \""+prj.getCode()+"\"):");
				in = input.nextLine();
				exists = false;
				if (!in.isEmpty()){			
					if(portfolio.findByCode(in, portfolio.findByCode(prj.getCode())) == -1) { 
						prj.setCode(in.toUpperCase());
					} else {
						exists = true;
						System.out.println("** Project already exists **");
					}
				}
			}while (exists);

		
		System.out.print("Project Name(current \""+prj.getName()+"\"): ");
		in = input.nextLine();
		if (!in.isEmpty()){
			prj.setName(in);
		}
		
		do {
			System.out.print("Start Date(current \""+dateFormat.format(prj.getStartDate())+"\"): ");
			in = input.nextLine();
			if (in.isEmpty()) {
				break;
			}
		} while (!validateDate(in));
		
		if (!in.isEmpty()){
			try {
				prj.setStartDate(dateFormat.parse(parseDate(in)));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.print("Client:(current \""+prj.getClient()+"\"): ");
		in = input.nextLine();
		if (!in.isEmpty()){
			prj.setClient(in);
		}
			
		if (prj instanceof OngoingProject) {
				
			do {
				System.out.print("Deadline:(current \""+dateFormat.format(((OngoingProject)prj).getDeadline())+"\"): ");
				in = input.nextLine();
				if (in.isEmpty()) {
					break;
				}
			} while (!validateDate(in, prj.getStartDate(),1));
			
			
			if (!in.isEmpty()){
				try {
					((OngoingProject)prj).setDeadline(dateFormat.parse(parseDate(in)));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
			do {
				System.out.print("Budget(current \""+((OngoingProject)prj).getBudget()+"\"): $");
				in = input.nextLine();
				if (!in.isEmpty()){
				
					try {
						((OngoingProject)prj).setBudget(Double.parseDouble(in));
						valid = true;
					} catch (Exception ex) {
						valid = false;
						System.out.println("Invalid value!\n");
					}
				} else {
					valid = true;
				}
				
			} while (!valid);
		
			valid = false;
							
			do {
				System.out.print("Completion(current \""+((OngoingProject)prj).getCompletion()+"\") % ");
				in = input.nextLine();
				if (!in.isEmpty()){
					try {
						((OngoingProject)prj).setCompletion(Integer.parseInt(in));
						valid = true;
					} catch (Exception ex) {
						valid = false;
						System.out.println("Invalid value!\n");
					}
				}  else {
					valid = true;
				}
			} while (!valid);
			valid = false;
			
		} else if (prj instanceof FinishedProject) {
			
			do {
				System.out.print("End Date:(current \""+dateFormat.format(((FinishedProject)prj).getEndDate())+"\"): ");
				in = input.nextLine();
				if (in.isEmpty()) {
					break;
				}
			} while (!validateDate(in, prj.getStartDate(),1));
			
			if (!in.isEmpty()){
				try {
					((FinishedProject)prj).setEndDate(dateFormat.parse(parseDate(in)));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			do {
				System.out.print("Total Cost:(current \""+((FinishedProject)prj).getTotalCost()+"\"): $");
				in = input.nextLine();
				if (!in.isEmpty()){
					try {
						((FinishedProject)prj).setTotalCost(Double.parseDouble(in));
						valid = true;
					} catch (Exception ex) {
						valid = false;
						System.out.println("Invalid value!\n");
					}
				}  else {
					valid = true;
				}
			} while (!valid);
			valid = false;
			
			}
		
		return prj;
		
	} //End editProject method
		
	/**
	 * Requests a menu option from the user and checks if it is valid.
	 * @return 	an Integer with the menu option index.
	 */
	private static int getMenuOption() {
		
		String option;
		int index = -1; 
		
		while (index == -1) {
			System.out.print("\nSelect an option from the menu: ");
			option = input.nextLine();
			for(int i=0; i<MENU_VALUE.length;i++) {
				if(option.equalsIgnoreCase(MENU_VALUE[i])) {
					index = i;
					break;
				}
			}
			if(index == -1) System.out.println("** Invalid choice! **");
		}
			
		return index;
	} //End getUserInput method

	/**
	 * Displays the splash screen of the application
	 */
	public static void splash() {
		
		/*
		 * Splash screen method
		 */
			
		System.out.println(	"****************************\n"
				+ 			"* Welcome to JProject v2.1 *\n"
				+ 			"*--------------------------*\n"
				+ 			"* (c) Gabriel Skoropada    *\n"
				+ 			"****************************\n");
	
	
	} //End splash method
	
	/**
	 * Displays the menu options to the user and notifies of any unsaved changes.
	 */
	public static void displayMenu () {
				
		if (!saved) System.out.println("\nThere are unsaved changes.\n");
			
		for (int x = 0; x < MENU.length ;x++) {
			System.out.println(MENU[x]);
		}
		
	} //End displayMenu method
	
	/**
	 * Checks if a string is in a valid date format.
	 * @param date	a String representing a date value
	 * @return	true if the date is in a valid format
	 */
	public static boolean validateDate(String date) {
			
		boolean validDate=true;
		String[] dateParts = new String[3];
		int i = 0;
		
		dateParts[0]="";
		dateParts[1]="";
		dateParts[2]="";
		
		if (date.contains("/")) {
			for (String dp: date.split("/")) {
				if (i>2) {
					validDate=false;
					System.out.println("Invalid date format!\n");
					break;
					}
				dateParts[i]=dp;
				i++;
			}
		} else if (date.contains("-")) {
			for (String dp: date.split("-")) {
				if (i>2) {
					validDate=false;
					System.out.println("Invalid date format!\n");
					break;
					}
				dateParts[i]=dp;
				i++;
			}
		} else {
			System.out.println("Invalid date format!\n");
			return false;
			}
		
		String dd = dateParts[0];
		String mm = dateParts[1];
		String yy = dateParts[2];
		
		if (dd.length()>0 & dd.length()<=2) {
			if (Integer.parseInt(dd) < 1 | Integer.parseInt(dd) > 31 ) {
				validDate=false;
			}
		} else {
			validDate=false;
		}
		
		if (mm.length()>0 & mm.length()<=2) {
			if (Integer.parseInt(mm) < 1 | Integer.parseInt(mm) > 12 ) {
				validDate=false;
			}
		} else {
			validDate=false;
		}
		
		if (yy.length()==4 ) {
			if (Integer.parseInt(yy) < 2000 | Integer.parseInt(yy) > 2100 ) {
				validDate=false;
				System.out.println("Year must be between 2000 and 2100!\n");
			}
		} else if (yy.length()==2) {
			if (Integer.parseInt(yy) < 0 & Integer.parseInt(yy) > 99 ) {
				validDate=false;
				System.out.println("Year must be between 2000 and 2100!\n");
			}
		} else {
			validDate=false;
		}
		
		if (Integer.parseInt(mm)==11 | Integer.parseInt(mm)==4 | Integer.parseInt(mm)==6 | Integer.parseInt(mm)==9) {
			if (Integer.parseInt(dd) > 30) {
				validDate = false;
			}
		}
		
		if (Integer.parseInt(mm)==2) { //Checks leap year
			if ((Integer.parseInt(yy) % 4 == 0) && (Integer.parseInt(yy) % 100 != 0) || (Integer.parseInt(yy) % 400 == 0)  ) {
				if (Integer.parseInt(dd) > 29) {
					validDate = false;
				}
			} else {
				if (Integer.parseInt(dd) > 28) {
					validDate = false;
				}
			}
		}
		
		if (!validDate) {
			System.out.println("Invalid date!\n");
		}
		return validDate;
	} // End of validateDate method

	/**
	 * Checks if a String is in a valid Date format.
	 * Compares the 'date' parameter with the 'd' Date parameter according to the 'compare' parameter
	 * 
	 * @param date	a String representing a date value
	 * @param d		a Date object to be compared with the <i>date</i> parameter
	 * @param compare	an Integer value that indicates the comparison direction.
	 * 					<p>If <i>compare</i> is a negative number it checks if <i>date</i> &#60; <i>d</i></p>
	 * 					<p>If <i>compare</i> is 0 or a positive number it checks if <i>date</i> &#62; <i>d</i></p>
	 * @return	true if the <i>date</i> parameter is valid and fulfills the <i>compare</i> condition
	 */
	public static boolean validateDate(String date, Date d, int compare) {
		
		boolean validDate = false;
		Date dateComp = null;
		
		if(validateDate(date)){
			try {
				dateComp=dateFormat.parse(date);
				if(compare<0) {
					if (dateComp.before(d)) {
						validDate = true;
					} else {
						System.out.println("** " + dateFormat.format(dateComp) + " should be before " + dateFormat.format(d) + " **");
					}
				} else if(compare>0) {
					if(dateComp.after(d)) {
						validDate = true;
					} else {
						System.out.println("** " + dateFormat.format(dateComp) + " should be after " + dateFormat.format(d) + " **");
					}
				}
							
			} catch (ParseException e) {
				validDate = false;
				System.out.println("** Invalid Date **");
			}
		}
		
		return validDate;
	}
	/**
	 * Parses a valid string containing date information into the standard format for the system.<br>
	 * This method doesn't check the validity of the <i>date</i> parameter. Use <i>validateDate</i> for that.
	 * @param date	a String with a valid date information.
	 * @return	a String formatted according the default date format
	 * @see	validateDate
	 * 
	 */
	public static String parseDate (String date) {
		
		String[] dateParts = new String[3];
		int i = 0;
		String token="";
		
		if (date.contains("/")) {
			token = "/";
			for (String dp: date.split("/")) {
				dateParts[i]=dp;
				i++;
			}
		} else if (date.contains("-")) {
			token = "-";
			for (String dp: date.split("-")) {
				dateParts[i]=dp;
				i++;
			}
		}
		
		if (dateParts[2].length() == 2) {
			dateParts[2] = "20" + dateParts[2];
		}
		
		token = "-";
		
		return dateParts[0]+token+dateParts[1]+token+dateParts[2];
		
	}

	/** Converts the toString() method of Project objects to screen format 
	 * @param	prj	The Project object to be displayed on the screen
	 * */	
	public static void parseProject(Project prj) { 
			
		if (prj instanceof OngoingProject) {		
			System.out.println(" Prj Code  | Project Name             | Client              | Start Date | Deadline   | Budget      | Completion");
			System.out.println("-----------+--------------------------+---------------------+------------+------------+-------------+-------------");
		 																			   
			System.out.printf("%-10s", prj.getCode());
			System.out.print(" |");
			System.out.printf("%-25s", prj.getName());
			System.out.print(" |");
			System.out.printf("%-20s", prj.getClient());
			System.out.print(" |");
			System.out.printf("%11s", dateFormat.format(prj.getStartDate()));
			System.out.print(" |");
			System.out.printf("%11s", dateFormat.format(((OngoingProject)prj).getDeadline()));
			System.out.print(" |");
			System.out.printf("$ %10.2f", ((OngoingProject)prj).getBudget());
			System.out.print(" |");
			System.out.printf("%10d", ((OngoingProject)prj).getCompletion());
			System.out.print("%\n\n");
				
			}
			
			if (prj instanceof FinishedProject) {
				System.out.println(" Prj Code  | Project Name             | Client              | Start Date | End Date   | Total Cost");
				System.out.println("-----------+--------------------------+---------------------+------------+------------+-------------");
				 																						   
				System.out.printf("%-10s", prj.getCode());
				System.out.print(" |");
				System.out.printf("%-25s", prj.getName());
				System.out.print(" |");
				System.out.printf("%-20s", prj.getClient());
				System.out.print(" |");
				System.out.printf("%11s", dateFormat.format(prj.getStartDate()));
				System.out.print(" |");
				System.out.printf("%11s", dateFormat.format(((FinishedProject)prj).getEndDate()));
				System.out.print(" |");
				System.out.printf("$ %10.2f", ((FinishedProject)prj).getTotalCost());
				System.out.print("\n");
										
				}
		
	} //End parseProject Method
	/**
	* Requests a Project Code from the user and checks if that code is present in the
	* working Portfolio object.<br>
	* Allows the user to cancel this operation by entering "Q" as a value.
	* @return 0 if the project code is not present in the data set; -2 if the user cancels the operation 
	*/
	public static int projectCodeInput () {
		
		
		
		int index = 0;
		String code;
		
		do {
			
			System.out.print("Please, enter project a code or Q to cancel: ");
			code = input.nextLine();
			if (!code.toUpperCase().equals("Q")) {
				index = portfolio.findByCode(code);
				if (index == -1) System.out.println("** Invalid Project Code **");
			} else index = -2;
			
		} while (index == -1);
		
		return index;
	}

}
