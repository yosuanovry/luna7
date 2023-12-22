package utils;

import java.io.FileReader;
import java.util.Properties;

public class MyUtil {
	
	public static String getProperty(String name) throws Exception {
		String configFile = "config.properties";
		
	    FileReader reader = new FileReader(configFile);
	    Properties props = new Properties();
	    props.load(reader);
	    String value = props.getProperty(name);
	    reader.close();
	    
	    return value;
	}
	
	public static void println(String s) {
		System.out.println(s);
	}

}
