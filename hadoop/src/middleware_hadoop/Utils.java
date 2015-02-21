package middleware_hadoop;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.NamingException;

public class Utils {
	
	public static void main(String[] args) throws NamingException {
		List<String> records= Utils.getRecordsFromFile("./test.log");
		for (String record : records) {
		    System.out.println(record);

			getDomain(record);		
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
	
	public static String getVideo(String record){
		Pattern pattern = Pattern.compile("Star_Wars_Kid\\w*\\.wmv");
		Matcher matcher = pattern.matcher(record);
	    String matched=null;

		if (matcher.find())
		{
		    matched= matcher.group(0);	

		}
		
		return matched;
		
	
		
	}
	
	public static String getDate(String record){
		
		Pattern pattern = Pattern.compile("\\[\\d{2}/\\w+/\\d{4}:");
		Matcher matcher = pattern.matcher(record);
	    String matched=null;

		if (matcher.find())
		{
		    matched= matcher.group(0);
		    matched = matched.substring(1, matched.length()-1);
		}
		
		return matched;
		
	}
	
	public static String getDomain(String record){
		Pattern pattern = Pattern.compile("\"http://www(\\.\\w+)+/");
		Matcher matcher = pattern.matcher(record);
	    String matched=null;

		if (matcher.find())
		{
		    matched= matcher.group(0);
		    matched = matched.substring(1, matched.length()-1);
		}
		
		return matched;
		
	}

}
