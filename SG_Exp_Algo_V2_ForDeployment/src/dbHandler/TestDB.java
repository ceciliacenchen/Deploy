/**
 * 
 */
package dbHandler;

import utility.LoadProperties;

/**
 * @author cenchen.2012
 *
 */
public class TestDB {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LoadProperties.load();
		ConnectDB.connect(LoadProperties.properties);
	}

}
