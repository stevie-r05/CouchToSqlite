package edu.asu.beastd;

import java.io.*;
import java.util.*;

import org.json.simple.JSONObject;

/**
 * This is where everything is actually put together.
 * @author BEASTD
 *
 */
public class Parse {
	
	public static void main (String[] args) throws IOException{
		
		Form form = new Form();
		
		// This block takes in the location of the JSON file and puts it in a string.
		String jsonPath;
		System.out.println("Input JSON location: ");
		Scanner scan = new Scanner(System.in);
		jsonPath = scan.next();
		
		// Convert to JSONObject type
		JSONObject jsonObject = form.JsonParsing(jsonPath);
		System.out.println(jsonObject.toJSONString());
		
		// Initialize Object arrays for keys and values of JSONObject
		Object[] keySetObj = jsonObject.keySet().toArray();
		Object[] valueSetObj = jsonObject.values().toArray();
		
		// Create strings to put into insert statement
		String keySetString = new String();
		for(int i=0;i<keySetObj.length-1;i++){
			keySetString += keySetObj[i].toString() + ",";
		}
		keySetString += keySetObj[keySetObj.length-1].toString();
		
		String valueSetString= new String();
		for(int i=0;i<valueSetObj.length-1;i++){
			valueSetString += "'" + valueSetObj[i].toString() + "'" + ",";
		}
		valueSetString += "'" + valueSetObj[valueSetObj.length-1].toString() + "'";
		
		// Prints column values for bug check
		for(int i=0;i<keySetObj.length;i++){
			System.out.println(keySetObj[i].toString());
		}

		// Initialize database connection
		System.out.println("Input DB location: ");
		String dbPath = scan.next();
		SQLite sqlite = new SQLite(dbPath);
		scan.close();
		
		// Initializes the table with proper column names.
		// TODO: Dynamically obtain the table name from user
		sqlite.executeSql("CREATE TABLE Forms(" + keySetObj[0] + " VARCHAR(100000000))");
		for(int i=1;i<keySetObj.length;i++){
			sqlite.executeSql("ALTER TABLE Forms ADD COLUMN " + keySetObj[i] + " VARCHAR(100000000)");
		}
		
		// Adds in data from CouchDB file
		sqlite.executeSql("INSERT INTO Forms (" + keySetString + ") VALUES (" + valueSetString + ")");
		
	}
}
