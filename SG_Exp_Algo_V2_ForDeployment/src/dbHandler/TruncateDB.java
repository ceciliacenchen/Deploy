/**
 * 
 */
package dbHandler;

import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.jdbc.Connection;

/**
 * @author chencen
 *
 */
public class TruncateDB {
	public static void truncate(Connection dbConnection,String db) {
		Statement statement = null;
		try {
			dbConnection.setAutoCommit(false);
			statement = dbConnection.createStatement();
			String query = "TRUNCATE TABLE "+db+";";	
			statement.execute(query);
			dbConnection.commit();
			System.out.println("Truncate "+db+" table.");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (dbConnection != null) {
				try {
					dbConnection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
