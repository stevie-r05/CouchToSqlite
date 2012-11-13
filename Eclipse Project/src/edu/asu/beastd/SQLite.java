package edu.asu.beastd;

import java.sql.Connection;  
import java.sql.DriverManager;  
import java.sql.ResultSet;  
import java.sql.SQLException;
import java.sql.Statement;

/**
 * SQLite is the class used for bridging to a SQLite file.
 * It provides the commands for actually executing SQL statements.
 * @author BEASTD
 */
public class SQLite {
	Connection connection;  
    ResultSet resultSet;  
    Statement statement;  

    /**
     * Initializes connection.
     * @param database The location of the database to be edited. Created if it doesn't exist.
     * @author BEASTD
     */
	public SQLite(String database){
		resultSet = null;
		statement = null;
        try{
        	Class.forName("org.sqlite.JDBC");
        	connection = DriverManager.getConnection("jdbc:sqlite:" + database);
        } catch (Exception e) {  
            e.printStackTrace();  
        }
	}
	
	/**
	 * Executes any SQL statement.
	 * @param queryStatement The SQL statement to be executed.
	 * @author BEASTD
	 */
	public void executeSql(String queryStatement){
		try {
			statement = connection.createStatement();
			statement.execute(queryStatement);
            statement.close(); 
		} catch (SQLException e) {
			// add log?
			e.printStackTrace();
		}  
	}
	
	/**
	 * Closes DB connection.
	 * @author BEASTD
	 */
	public void closeDatabase(){
		 try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
