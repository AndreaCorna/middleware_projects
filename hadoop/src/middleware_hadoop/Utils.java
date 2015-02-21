package middleware_hadoop;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.naming.NamingException;

public class Utils {
	
	public static void main(String[] args) throws NamingException {
		List<String> records= Utils.getRecordsFromFile("test.log");
		for (String record : records) {
			getDate(record);		
		}
		
		
		
	}
	
	/**
	 * Load urls from a file
	 * @return list of string of url sites
	 */
	private  static List<String> getRecordsFromFile(String filePath){
		Charset charset = Charset.forName("UTF-8");
		Path path = Paths.get(filePath);	
		List<String> urlsList = null;	
		try {
			 urlsList = Files.readAllLines(path,charset);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return urlsList;
		
	}
	
	public static String getDate(String record){
		
		System.out.println(record);
		return record;
		
	}

}
