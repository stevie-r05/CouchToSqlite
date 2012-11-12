import java.io.*;
import java.util.*;

public class Parse {
	public static void main (String[] args) throws IOException{
		Form form = new Form();
		Scanner scan = new Scanner(System.in);
		form.readFile(scan);
		System.out.println(form.toString());
	}
}
