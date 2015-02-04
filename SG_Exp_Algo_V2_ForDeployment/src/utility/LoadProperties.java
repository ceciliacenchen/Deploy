/**
 * 
 */
package utility;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author cenchen.2012
 *
 */
public class LoadProperties {
	public static Properties properties;
	public static final String PROP_NAME="config.properties";
	
	public static void load() {
		properties = new Properties();
	    try {
	        properties.load(new FileInputStream(PROP_NAME));
	    } catch (IOException e) { 
	    }
	}

}
