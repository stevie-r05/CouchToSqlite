package edu.asu.beastd;

import com.fourspaces.couchdb.*;
import java.io.*;
import java.util.*;

/**
 * The CouchToSqlite class can take in a SQLite file location and/or CouchDB database location in 
 * its constructor and successfully convert CouchDB into relational SQLite databases.<br><br>
 * If no CouchDB instance is specified in the constructor, the class defaults to localhost:5984, and if no output file is
 * specified, it defaults to "TestDB.db" as a location.
 * @author BEASTD
 */
public class CouchToSqlite {
	
	private String dbPath;
	private String couchHost;
	private int couchPort;
	private SQLite sqlite;
	private Session couchSession;
	
	/**
	 * Constructor for default SQLite location ("TestDB.db") and default CouchDB location (localhost:5984).
	 * @throws CouchException If there is an error accessing the CouchDB instance
	 * @throws SqliteException If there is an error accessing the SQLite .db location
	 * @author BEASTD
	 */
	public CouchToSqlite() throws CouchException, SqliteException {
		this("TestDB.db", "localhost", 5984);
	}
	
	/**
	 * Constructor for a specific SQLite .db file output location and default CouchDB location (localhost:5984).
	 * @param dbFileOutput Output location of the .db SQLite file generated.
	 * @throws CouchException If there is an error accessing the CouchDB instance
	 * @throws SqliteException If there is an error accessing the SQLite .db location
	 * @author BEASTD
	 */
	public CouchToSqlite(String dbPath) throws CouchException, SqliteException {
		this(dbPath, "localhost", 5984);
	}
	
	/**
	 * Constructor for default SQLite location ("TestDB.db") and a specific CouchDB location.
	 * @param couchHost The host location of the CouchDB instance
	 * @param couchPort The port to access CouchDB on (typically the default 5984)
	 * @throws CouchException If there is an error accessing the CouchDB instance
	 * @throws SqliteException If there is an error accessing the SQLite .db location
	 * @author BEASTD
	 */
	public CouchToSqlite(String couchHost, int couchPort) throws CouchException, SqliteException {
		this("TestDB.db", couchHost, couchPort);
	}
	
	/**
	 * Constructor for a specific SQLite .db file output location and specific CouchDB location.
	 * @param dbPath Output location of the .db SQLite file generated
	 * @param couchHost The host location of the CouchDB instance
	 * @param couchPort The port to access CouchDB on (typically the default 5984)
	 * @throws CouchException If there is an error accessing the CouchDB instance
	 * @throws SqliteException If there is an error accessing the SQLite .db location
	 * @author BEASTD
	 */
	public CouchToSqlite(String dbPath, String couchHost, int couchPort) throws CouchException, SqliteException {
		this.dbPath = dbPath;
		this.couchHost = couchHost;
		this.couchPort = couchPort;
		try {
			this.sqlite = new SQLite(dbPath);
		} catch (Exception e){
			throw new SqliteException("Couldn't connect to file location: " + e.getMessage(), e.getCause());
		}
		try {
			this.couchSession = new Session(couchHost, couchPort);
		} catch (Exception e) {
			throw new CouchException("Couldn't connect to Couch instance: " + e.getMessage(), e.getCause());
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void main (String[] args) throws IOException{
		
		// Prompt user for SQLite output location
		System.out.println("Please indicate an output file for the SQLite DB file");
		Scanner scan = new Scanner(System.in);
		String dbPath = scan.next();
		scan.close();

		Session session = null;
		List<String> databaseList = null;
		SQLite sqlite = new SQLite(dbPath);
		
		// Make our CouchDB connection
		try
		{
			session = new Session("localhost", 5984);
			databaseList = session.getDatabaseNames();
		} catch (Exception ex) {
			System.out.println("Problem connection to CouchDB instance.");
			System.exit(1);
		}
		
		// Verifies the database names are retrieved correctly (for debug)
		for(int i=0;i<databaseList.size();i++)
		{
			System.out.println(session.getDatabase(databaseList.get(i)).getName());
		}
		
		/* For each db in databaseList, get the documents, create a table for each db, add in
		 * documents to each table. 
		 * TODO: Split this further into methods/classes
		 */
		for(int i=0;i<databaseList.size();i++)
		{
			// Acquire each database and get its documents
			Database db = session.getDatabase(databaseList.get(i));
			ViewResults docList = db.getAllDocuments();
			
			// Get first document to initialize the table structure. docFields holds the column values.
			// fieldIt allows us to iterate through each field in order.
			Document doc = db.getDocument(docList.getResults().get(0).getId());
			Set<String> docFields = doc.keySet();
			Iterator<String> fieldIt = docFields.iterator();
			
			// Drops the table if it exists. Ignores otherwise.
			sqlite.executeSql("DROP TABLE " + db.getName());

			// Creates the table for the particular database. Initializes all columns to the structure of the
			// first document.
			// TODO: Somebody add in type checking, so we don't only add in strings.
			sqlite.executeSql("CREATE TABLE " + db.getName() + "(" + fieldIt.next() + " VARCHAR(100000000))");
			while (fieldIt.hasNext())
			{
				sqlite.executeSql("ALTER TABLE " + db.getName() + " ADD COLUMN " + fieldIt.next() + " VARCHAR(100000000)");
			}
			
			fieldIt = docFields.iterator(); // reset fieldIt for adding values into doc.
			
			// Build keySetString and valueSetString to inject into SQLite command.
			// We do this by moving through the fields and adding values to the string
			String keySetString = new String();
			while(fieldIt.hasNext())
			{
				keySetString += fieldIt.next() + ",";
			}
			keySetString = keySetString.substring(0, keySetString.length()-1); //remove extra comma
			fieldIt = docFields.iterator();
			String valueSetString = new String();
			while(fieldIt.hasNext())
			{
				// replaceAll is to add escape character for single quote
				valueSetString += "'" + doc.getString(fieldIt.next()).replaceAll("'", "''") + "',";
			}
			valueSetString = valueSetString.substring(0, valueSetString.length()-1);
			sqlite.executeSql("INSERT INTO " + db.getName() + "( " + keySetString + ") VALUES (" + valueSetString + ")");
			
			// Now, move through each document in a particular DB to add more rows
			// TODO: Merge in with table creation to avoid redundant code
			for(int j=1;j<docList.getResults().size();j++)
			{
				doc = db.getDocument(docList.getResults().get(j).getId());
				docFields = doc.keySet();
				fieldIt = docFields.iterator();
				keySetString = "";
				while(fieldIt.hasNext())
				{
					keySetString += fieldIt.next() + ",";
				}
				keySetString = keySetString.substring(0, keySetString.length()-1);
				fieldIt = docFields.iterator();
				valueSetString = "";
				while(fieldIt.hasNext())
				{
					valueSetString += "'" + doc.getString(fieldIt.next()).replaceAll("'", "''") + "',";
				}
				valueSetString = valueSetString.substring(0, valueSetString.length()-1);
				sqlite.executeSql("INSERT INTO " + db.getName() + "( " + keySetString + ") VALUES (" + valueSetString + ")");
			} //end docList for loop
			
		} //end db for loop
	} //end main

}