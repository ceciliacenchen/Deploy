/**
 * 
 */
package dataObjects;

import java.util.ArrayList;

/**
 * @author chencen
 *
 */
public class Route {
	private ArrayList<Integer> path;

	/**
	 * @param path
	 */
	public Route(ArrayList<Integer> path) {
		super();
		this.path = path;
	}

	/**
	 * 
	 */
	public Route(int noOfNodes) {
		super();
		path=new ArrayList<Integer>();
		for(int i=0;i<noOfNodes;i++) {
			path.add(0);
		}
	}
	
	public Route() {
		super();
		path=new ArrayList<Integer>();
	}

	/**
	 * @return the path
	 */
	public ArrayList<Integer> getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(ArrayList<Integer> path) {
		this.path = path;
	}
	
	

}
