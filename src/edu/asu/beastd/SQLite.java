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
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import com.sun.istack.internal.logging.Logger;

/**
 * SQLite is the class used for bridging to a SQLite file.
 * It provides the commands for actually executing SQL statements.
 * @author Team BEASTD
 */
public class SQLite {
	
	public static final Logger LOG = Logger.getLogger(SQLite.class);
	
	private Connection connection;  
    private Statement statement;  
    private String databaseLocation;

    /**
     * Initializes connection.
     * @param database The location of the database to be edited. Created if it doesn't exist.
     * @throws SqliteException If there is an error connecting to the file location
     */
	public SQLite(String database) throws SqliteException {
		statement = null;
        try{
        	databaseLocation = "jdbc:sqlite:" + database;
        	Class.forName("org.sqlite.JDBC");
        	connection = DriverManager.getConnection(databaseLocation);
        } catch (Exception e) { 
        	LOG.logException(e, Level.WARNING);
            throw new SqliteException("Couldn't connect to file location: " + e.getMessage(), e.getCause()); 
        } finally {
        	try {
				connection.close();
			} catch (SQLException ex) { 
				LOG.logSevereException(ex); // Should theoretically never be reached
			}
        }
	}
	
	/**
	 * Used to check if a column already exists in a given table.
	 * @param columnName The column name to check for
	 * @param tableName The table to be checked against
	 * @return <code>true</code> if the column exists, <code>false</code> if it doesn't.
	 * @throws SqliteException If there is an error executing the query.
	 */
	public boolean hasColumn(String columnName, String tableName) throws SqliteException {
		try {
			connection = DriverManager.getConnection(databaseLocation);
			try { 
				statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName);
				ResultSetMetaData metadata = resultSet.getMetaData();
				
				//NOTE: JDBC indexing starts at 1.
				for(int i=1;i<=metadata.getColumnCount();i++)
				{
					if (columnName.equals(metadata.getColumnLabel(i)))
						return true;
				}
				return false;
			} catch (SQLException e) {
				LOG.logException(e, Level.WARNING);
				throw new SqliteException("Problem searching columns: " + e.getMessage(), e.getCause());
			} finally {
				try {
					statement.close();
				} catch (SQLException ex) {
					LOG.logSevereException(ex); // Theoretically should never happen
				}
			}
		} catch (SQLException e) {
			LOG.logException(e, Level.WARNING);
			throw new SqliteException("Problem getting connection: " + e.getMessage(), e.getCause());
		}
		finally {
			try {
				connection.close();
			} catch (SQLException ex) {
				LOG.logSevereException(ex); // Theoretically should never happen
			}
		}
	}
	
	/**
	 * Executes any SQL statement.
	 * @param queryStatement The SQL statement to be executed.
	 * @throws SqliteException If there is an error executing the SQL code
	 */
	public void executeSql(String queryStatement) throws SqliteException {
		try {
			connection = DriverManager.getConnection(databaseLocation);
			try { 
				statement = connection.createStatement();
				statement.execute(queryStatement); 
			} catch (SQLException e) {
				LOG.logException(e, Level.WARNING);
				throw new SqliteException("Problem executing SQL code: " + queryStatement + ": " + e.getMessage() , e.getCause());
			} finally {
				try {
					statement.close();
				} catch (SQLException ex) {
					LOG.logSevereException(ex); // Should theoretically never happen
				}
			}
		} catch (SQLException e) {
			LOG.logException(e, Level.WARNING);
			throw new SqliteException("Problem getting connection: " + e.getMessage(), e.getCause());
		} finally {
			try {
				connection.close();
			} catch (SQLException ex) {
				LOG.logSevereException(ex); // Should theoretically never happen
			}
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
			LOG.logException(e, Level.WARNING);
			throw new SqliteException("Problem closing SQLite database: " + e.getMessage(), e.getCause());
		}
	}
}
