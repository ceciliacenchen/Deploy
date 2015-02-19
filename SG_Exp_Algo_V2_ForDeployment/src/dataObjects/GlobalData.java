/**
 * 
 */
package dataObjects;
import java.util.ArrayList;

/**
 * @author chencen
 *
 */
public class GlobalData {
	private String instanceID;
	private int noOfPatrons;
	private int noOfNodes; //include entrance and exit node
	private int noOfTaskNodes;
	private ArrayList<ArrayList<Integer>> noOfRoutineNode; //[patrons][routeRange]
	  
	private double[][] walkingTimes;//walkingTimes[nodes][nodes]
	private double[] taskUtility; //taskUtility[taskNodesRange]
	private int[] taskNodes; //taskNodes[taskNodesRange]
	
	private ArrayList<ArrayList<Double>> routeProbability; //routeProbability[patrons][routeRange]
	private ArrayList<ArrayList<Double>> detourTime; //detourTime[patrons][routeRange]
	private ArrayList<ArrayList<Integer>> startNode; //startNode[patrons][routeRange]
	private ArrayList<ArrayList<Integer>> endNode; //endNode[patrons][routeRange]
	private ArrayList<ArrayList<Route>> visitingRoutineNodesSequence; //visitingRoutineNodesSequence[patrons][routeRange]
	/**
	 * 
	 */
	public GlobalData() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public GlobalData(String instanceID, int noOfPatrons, int noOfNodes,
			int noOfTaskNodes, ArrayList<ArrayList<Integer>> noOfRoutineNode,
			double[][] walkingTimes, double[] taskUtility, int[] taskNodes,
			ArrayList<ArrayList<Double>> routeProbability,
			ArrayList<ArrayList<Double>> detourTime,
			ArrayList<ArrayList<Integer>> startNode,
			ArrayList<ArrayList<Integer>> endNode,
			ArrayList<ArrayList<Route>> visitingRoutineNodesSequence) {
		super();
		this.instanceID = instanceID;
		this.noOfPatrons = noOfPatrons;
		this.noOfNodes = noOfNodes;
		this.noOfTaskNodes = noOfTaskNodes;
		this.noOfRoutineNode = noOfRoutineNode;
		this.walkingTimes = walkingTimes;
		this.taskUtility = taskUtility;
		this.taskNodes = taskNodes;
		this.routeProbability = routeProbability;
		this.detourTime = detourTime;
		this.startNode = startNode;
		this.endNode = endNode;
		this.visitingRoutineNodesSequence = visitingRoutineNodesSequence;
	}
	public ArrayList<ArrayList<Integer>> getNoOfRoutineNode() {
		return noOfRoutineNode;
	}

	public void setNoOfRoutineNode(ArrayList<ArrayList<Integer>> noOfRoutineNode) {
		this.noOfRoutineNode = noOfRoutineNode;
	}

	public ArrayList<ArrayList<Double>> getRouteProbability() {
		return routeProbability;
	}

	public void setRouteProbability(ArrayList<ArrayList<Double>> routeProbability) {
		this.routeProbability = routeProbability;
	}

	public ArrayList<ArrayList<Double>> getDetourTime() {
		return detourTime;
	}

	public void setDetourTime(ArrayList<ArrayList<Double>> detourTime) {
		this.detourTime = detourTime;
	}

	public ArrayList<ArrayList<Integer>> getStartNode() {
		return startNode;
	}

	public void setStartNode(ArrayList<ArrayList<Integer>> startNode) {
		this.startNode = startNode;
	}

	public ArrayList<ArrayList<Integer>> getEndNode() {
		return endNode;
	}

	public void setEndNode(ArrayList<ArrayList<Integer>> endNode) {
		this.endNode = endNode;
	}

	public ArrayList<ArrayList<Route>> getVisitingRoutineNodesSequence() {
		return visitingRoutineNodesSequence;
	}

	public void setVisitingRoutineNodesSequence(
			ArrayList<ArrayList<Route>> visitingRoutineNodesSequence) {
		this.visitingRoutineNodesSequence = visitingRoutineNodesSequence;
	}

	public String getInstanceID() {
		return instanceID;
	}
	public void setInstanceID(String instanceID) {
		this.instanceID = instanceID;
	}
	public int getNoOfPatrons() {
		return noOfPatrons;
	}
	public void setNoOfPatrons(int noOfPatrons) {
		this.noOfPatrons = noOfPatrons;
	}
	public int getNoOfNodes() {
		return noOfNodes;
	}
	public void setNoOfNodes(int noOfNodes) {
		this.noOfNodes = noOfNodes;
	}
	public int getNoOfTaskNodes() {
		return noOfTaskNodes;
	}
	public void setNoOfTaskNodes(int noOfTaskNodes) {
		this.noOfTaskNodes = noOfTaskNodes;
	}

	public double[][] getWalkingTimes() {
		return walkingTimes;
	}
	public void setWalkingTimes(double[][] walkingTimes) {
		this.walkingTimes = walkingTimes;
	}
	public double[] getTaskUtility() {
		return taskUtility;
	}
	public void setTaskUtility(double[] taskUtility) {
		this.taskUtility = taskUtility;
	}
	public int[] getTaskNodes() {
		return taskNodes;
	}
	public void setTaskNodes(int[] taskNodes) {
		this.taskNodes = taskNodes;
	}
}
