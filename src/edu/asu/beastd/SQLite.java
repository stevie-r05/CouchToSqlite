package edu.asu.beastd;

import java.sql.Connection;  
import java.sql.DriverManager;  
import java.sql.SQLException;
import java.sql.Statement;

/**
 * SQLite is the class used for bridging to a SQLite file.
 * It provides the commands for actually executing SQL statements.
 * @author Team BEASTD
 */
public class SQLite {
	
	private Connection connection;  
    private Statement statement;  

    /**
     * Initializes connection.
     * @param database The location of the database to be edited. Created if it doesn't exist.
     * @throws SqliteException If there is an error connecting to the file location
     */
	public SQLite(String database) throws SqliteException {
		statement = null;
        try{
        	Class.forName("org.sqlite.JDBC");
        	connection = DriverManager.getConnection("jdbc:sqlite:" + database);
        } catch (Exception e) {  
            throw new SqliteException("Couldn't connect to file location: " + e.getMessage(), e.getCause()); 
        }
	}
	
	/**
	 * Executes any SQL statement.
	 * @param queryStatement The SQL statement to be executed.
	 * @throws SqliteException If there is an error executing the SQL code
	 */
	public void executeSql(String queryStatement) throws SqliteException {
		try {
			statement = connection.createStatement();
			statement.execute(queryStatement);
            statement.close(); 
		} catch (SQLException e) {
			throw new SqliteException("Problem executing SQL code: " + e.getMessage() , e.getCause());
		}  
	}
	
	/**
	 * Closes DB connection.
	 * @throws SqliteException If there is a problem closing the DB connection
	 */
	public void closeDatabase() throws SqliteException {
		 try {
			connection.close();
		} catch (SQLException e) {
			throw new SqliteException("Problem closing SQLite database: " + e.getMessage(), e.getCause());
		}
	}
}
