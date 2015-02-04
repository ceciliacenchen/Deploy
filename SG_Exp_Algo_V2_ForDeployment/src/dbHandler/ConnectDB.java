/**
 * 
 */
package dbHandler;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;




import utility.LoadProperties;

import com.mysql.jdbc.Connection;

/**
 * @author cenchen.2012
 *
 */
public class ConnectDB {
	
	
	public static Connection connect(Properties properties) {
		String className=LoadProperties.properties.getProperty("classname").trim();
		String connectionUrl=LoadProperties.properties.getProperty("connection").trim();
		String user=LoadProperties.properties.getProperty("user").trim();
		String password=LoadProperties.properties.getProperty("password").trim();
		
		System.out.println("-------- MySQL JDBC Connection ------------");	 
		System.out.println("className: "+className);
		try {
			Class.forName(className);
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
			System.out.println("Need correct MySQL JDBC Driver.");
			e.printStackTrace();
			return null;
		}
	 
		System.out.println("MySQL JDBC Driver Registered.");
		Connection connection = null;
	 
		try {
			connection = (Connection) DriverManager.getConnection(connectionUrl,user,password);
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return null;
		}
	 
		if (connection != null) {
			System.out.println("Done.");
			return connection;
		} else {
			System.out.println("Failed to make connection.");
			return null;
		}
	}

}
