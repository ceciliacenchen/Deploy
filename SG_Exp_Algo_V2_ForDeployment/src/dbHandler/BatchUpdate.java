/**
 * 
 */
package dbHandler;

import java.sql.SQLException;
import com.mysql.jdbc.Connection;
import java.sql.Statement;

/**
 * @author cenchen.2012
 *
 */
public class BatchUpdate {
	
	/*
	 * Updates the results into database
	 */
	public static void batchInsertRoutesIntoTable(Connection dbConnection) { 
		Statement statement = null;
		try {
			statement = dbConnection.createStatement();
			dbConnection.setAutoCommit(false);
			for(int i=0;i<10;i++) {
				String insertTableSQL1 = "INSERT INTO DBUSER"
						+ "(USER_ID, USERNAME, CREATED_BY, CREATED_DATE) " + "VALUES"
						+ "(101,'mkyong','system'";
				statement.addBatch(insertTableSQL1);	
			}
			statement.executeBatch();
			dbConnection.commit();
			System.out.println("Records are inserted into ASSIGNMENT table.");
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
	
	public static void batchInsertDistIntoTable(Connection dbConnection,double[][] dis,int[] nodelist)  { 
		Statement statement = null;
		try {
			statement = dbConnection.createStatement();
			dbConnection.setAutoCommit(false);
			for(int i=0;i<nodelist.length;i++) {
				for(int j=0;j<nodelist.length;j++) {
				int x=nodelist[i];
				int y=nodelist[j];
				double dist=dis[i][j];
				String insertTableSQL1 = "INSERT INTO DISTANCE"
						+ "(NODE1, NODE2, DIST) VALUES("
						+ x+","+y+","+dist+");";
				statement.addBatch(insertTableSQL1);	
				}
			}
			statement.executeBatch();
			dbConnection.commit();
			System.out.println("Records are inserted into DISTANCE table.");
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
