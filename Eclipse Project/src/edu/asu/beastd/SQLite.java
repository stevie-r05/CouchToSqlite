package edu.asu.beastd;

import java.sql.Connection;  
import java.sql.DriverManager;  
import java.sql.ResultSet;  
import java.sql.SQLException;
import java.sql.Statement;

public class SQLite {
	Connection connection;  
    ResultSet resultSet;  
    Statement statement;  

	public SQLite(String databse){
		resultSet = null;
		statement = null;
        try{
        	Class.forName("org.sqlite.JDBC");
        	connection = DriverManager.getConnection("jdbc:sqlite:" + databse);
        } catch (Exception e) {  
            e.printStackTrace();  
        }
	}
	
	public void insertQuery(String queryStatement){
		try {
			statement = connection.createStatement();
			statement.executeQuery(queryStatement);
			resultSet.close();  
            statement.close(); 
		} catch (SQLException e) {
			e.printStackTrace();
		}  
	}
	
	public void closeDatabase(){
		 try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
