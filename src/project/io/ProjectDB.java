package project.io;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import project.logic.*;
/**
 * This class handles all database interaction for the application.
 * @author Gabriel Skoropada
 * @version 1.0
 * @see Portfolio
 */
public class ProjectDB {

	/** String constant holding the DB user */
	private static final String DB_USR = "JavaUser";
	/** String constant holding the DB name */
	private static final String DB_NAME = "jproject";
	/** String constant holding the password for DB_USR */
	private static final String DB_PWD = "rmit1234!";
	/** Connection object used across this class */
	private static Connection con;
	/** Statement object used across this class */
	private static Statement sql;
	/** SimpleDateFormat object used to format Date objects into SQL Server valid strings */
	private static SimpleDateFormat df = new SimpleDateFormat("yyyMMdd");
	
	/**
	 * Initialises the working connection object in this class.
	 */
	public static void connect(){
	
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("** DB Driver not found **");
			e.printStackTrace();
		}
		
		String connectionUrl = "jdbc:sqlserver://localhost:1433;" +
				   "databaseName="+DB_NAME+";user="+DB_USR+";password="+DB_PWD+";";
		
		try {
			con = DriverManager.getConnection(connectionUrl);
			sql = con.createStatement();	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("** Could not connect to the DB **");
			e.printStackTrace();
		}
	}
	
	/**
	 * Reads all the records in the database and returns them as an ArrayList 
	 * @return An ArrayList&#60;Project&#62; with the data from the DB
	 */
	public static ArrayList<Project> init() {
		ArrayList<Project> port = new ArrayList<Project>();
		
		String ongoingQuery = "SELECT PROJECT.ProjectCode, ProjectName, ProjectClient, StartDate, Deadline, Budget, Completion "
				+ "FROM PROJECT INNER JOIN ONGOING_PROJECT ON PROJECT.ProjectCode = ONGOING_PROJECT.ProjectCode";

		ResultSet rs;
		try {
			rs = sql.executeQuery(ongoingQuery);
			while(rs.next()) {
				OngoingProject op = new OngoingProject(rs.getString("ProjectCode"), rs.getString("ProjectName"),
						rs.getDate("StartDate"),rs.getString("ProjectClient"),rs.getDate("Deadline"),
						rs.getDouble("Budget"), rs.getInt("Completion"));
				port.add(op);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String finishedQuery = "SELECT PROJECT.ProjectCode, ProjectName, ProjectClient, StartDate, EndDate, TotalCost "
				+ "FROM PROJECT INNER JOIN FINISHED_PROJECT ON PROJECT.ProjectCode = FINISHED_PROJECT.ProjectCode";

		try {
			rs = sql.executeQuery(finishedQuery);
			while(rs.next()) {
				FinishedProject fp = new FinishedProject(rs.getString("ProjectCode"), rs.getString("ProjectName"),
						rs.getDate("StartDate"),rs.getString("ProjectClient"),rs.getDate("EndDate"),
						rs.getDouble("TotalCost"));
				port.add(fp);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return port;
	}
	
	/**
	 * Adds a new record in the database from a Project object.
	 * @param p The Project object to be added in the database.
	 */
	public static void add(Project p){
		String query = "";
		
		if(p instanceof OngoingProject) {
			query = "INSERT INTO PROJECT (ProjectCode, ProjectName, StartDate, ProjectClient, ProjectStatus) "
					+ "VALUES ('"+p.getCode()+"','"+p.getName()+"','"+df.format(p.getStartDate())+"','"
					+ p.getClient()+"','O');"
					+ "INSERT INTO ONGOING_PROJECT (ProjectCode, Deadline, Budget, Completion) "
					+ "VALUES ('"+p.getCode()+"','"+df.format(((OngoingProject) p).getDeadline())
					+ "',"+((OngoingProject)p).getBudget()+","+((OngoingProject)p).getCompletion()+");";
		} else if(p instanceof FinishedProject) {
			query = "INSERT INTO PROJECT (ProjectCode, ProjectName, StartDate, ProjectClient, ProjectStatus) "
					+ "VALUES ('"+p.getCode()+"','"+p.getName()+"','"+df.format(p.getStartDate())+"','"
					+ p.getClient()+"','F');"
					+ "INSERT INTO FINISHED_PROJECT (ProjectCode, EndDate, TotalCost) "
					+ "VALUES ('"+p.getCode()+"','"+df.format(((FinishedProject) p).getEndDate())
					+ "',"+((FinishedProject)p).getTotalCost()+");";
		}

		try {
			sql.executeUpdate(query);
			System.out.println("** Record added **");
		} catch (Exception e) {
			System.out.println("** Could not add record **");
			e.printStackTrace();
		}

	}

	/**
	 * Updates a record in the database with data from a Project object.
	 * @param p The project object with the new data to be updated in the database.
	 * @param change A boolean value indicating if the Project object changed type. 
	 */
	public static void update(Project p, boolean change) {
		String query = "UPDATE PROJECT "
				+ "SET ProjectName = '" + p.getName() + "'"
				+ ", ProjectClient = '" + p.getClient() + "'"
				+ ", StartDate = '" + df.format(p.getStartDate())+ "'";
		
		if(p instanceof OngoingProject) {
			query += ", ProjectStatus = 'O' WHERE ProjectCode = '" + p.getCode()+"';";
			query += "UPDATE ONGOING_PROJECT "
					+ "SET Deadline = '" + df.format(((OngoingProject)p).getDeadline()) + "'"
					+ ", Budget = " + ((OngoingProject)p).getBudget()
					+ ", Completion = " + ((OngoingProject)p).getCompletion()
					+ " WHERE ProjectCode = '" + p.getCode() + "';";
			
		}else if(p instanceof FinishedProject) {
			query += ", ProjectStatus = 'F' WHERE ProjectCode = '" + p.getCode()+"';";
			if(change) {
				query += "INSERT INTO FINISHED_PROJECT (EndDate, TotalCost, ProjectCode) "
						+ "VALUES ('" + df.format(((FinishedProject)p).getEndDate()) + "'"
						+ "," + ((FinishedProject)p).getTotalCost()
						+ ", '" + p.getCode() + "');";
				
				query += "DELETE FROM ONGOING_PROJECT WHERE ProjectCode = '" + p.getCode() + "';";
			
			} else {
				query += "UPDATE FINISHED_PROJECT "
						+ "SET EndDate = '" + df.format(((FinishedProject)p).getEndDate()) + "'"
						+ ", TotalCost = " + ((FinishedProject)p).getTotalCost()
						+ " WHERE ProjectCode = '" + p.getCode() + "';";
			}
		}
		
		try {
			sql.executeUpdate(query);
			System.out.println("** Record Updated **");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("** Could not update record **");
			e.printStackTrace();
		}
		
	}

	/**
	 * Deletes a Prroject record from the database.
	 * @param project The Project object to be deleted.
	 */
	public static void delete(Project project) {
		String query = "DELETE FROM PROJECT WHERE ProjectCode = '"+project.getCode()+"';";
		if(project instanceof OngoingProject) {
			query += "DELETE FROM ONGOING_PROJECT WHERE ProjectCode = '"+project.getCode()+"';";
		} else if(project instanceof FinishedProject) {
			query += "DELETE FROM FINISHED_PROJECT WHERE ProjectCode = '"+project.getCode()+"';";
		}
		
		try {
			sql.executeUpdate(query);
			System.out.println("** Record deleted **");
		} catch (SQLException e) {
			System.out.println("** Could not delete record **");
			e.printStackTrace();
		}
		
	}
	
}
