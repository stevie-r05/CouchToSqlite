package edu.asu.beastd;

import java.io.*;
import java.util.*;

import org.json.simple.JSONObject;

public class Parse {
	public static void main (String[] args) throws IOException{
		Form form = new Form();
		String filepath;
		Scanner scan = new Scanner(System.in);
		filepath = scan.next();
		JSONObject jsonObject = form.JsonParsing(filepath);
		System.out.println(jsonObject.toJSONString());
		scan.close();
	}
}
