// Copyright (C) 2012 Stevie Robinson, Eric Alford, Tara Mendoza, Blake Tucker, Anthony Sanchez, Davenn Mannix
//
// This file is part of CouchToSqlite.
//
// CouchToSqlite is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// CouchToSqlite is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with CouchToSqlite.  If not, see <http://www.gnu.org/licenses/>.

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
