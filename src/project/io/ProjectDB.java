package project.io;

import java.sql.*;
import java.util.ArrayList;

import project.logic.*;

public class ProjectDB {

	private static final String DB_USR = "JavaUser";
	private static final String DB_NAME = "jproject";
	private static final String DB_PWD = "rmit1234!";
	private static Connection con;
	private static Statement sql;
	
	public static void connect() throws ClassNotFoundException, SQLException {
	
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		
		String connectionUrl = "jdbc:sqlserver://localhost:1433;" +
				   "databaseName="+DB_NAME+";user="+DB_USR+";password="+DB_PWD+";";
		
		con = DriverManager.getConnection(connectionUrl);
		sql = con.createStatement();	
	}
	
	public static ArrayList<Project> init() throws SQLException {
		ArrayList<Project> port = new ArrayList<Project>();
		
		String ongoingQuery = "SELECT PROJECT.ProjectCode, ProjectName, ProjectClient, StartDate, Deadline, Budget, Completion "
				+ "FROM PROJECT INNER JOIN ONGOING_PROJECT ON PROJECT.ProjectCode = ONGOING_PROJECT.ProjectCode";

		ResultSet rs = sql.executeQuery(ongoingQuery);
		
		while(rs.next()) {
			OngoingProject op = new OngoingProject(rs.getString("ProjectCode"), rs.getString("ProjectName"),
					rs.getDate("StartDate"),rs.getString("ProjectClient"),rs.getDate("Deadline"),
					rs.getDouble("Budget"), rs.getInt("Completion"));
			port.add(op);
		}
		
		String finishedQuery = "SELECT PROJECT.ProjectCode, ProjectName, ProjectClient, StartDate, EndDate, TotalCost "
				+ "FROM PROJECT INNER JOIN FINISHED_PROJECT ON PROJECT.ProjectCode = FINISHED_PROJECT.ProjectCode";

		rs = sql.executeQuery(finishedQuery);
		
		while(rs.next()) {
			FinishedProject fp = new FinishedProject(rs.getString("ProjectCode"), rs.getString("ProjectName"),
					rs.getDate("StartDate"),rs.getString("ProjectClient"),rs.getDate("EndDate"),
					rs.getDouble("TotalCost"));
			port.add(fp);
		}
		
		return port;
	}
	
	
	
}
