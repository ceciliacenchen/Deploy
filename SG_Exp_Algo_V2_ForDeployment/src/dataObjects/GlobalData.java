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
	private int noOfRoutesPerK; 
	private int[][] noOfRoutineNode; //[patrons][routeRange]
	  
	private double[][] walkingTimes;//walkingTimes[nodes][nodes]
	private double[] taskUtility; //taskUtility[taskNodesRange]
	private int[] taskNodes; //taskNodes[taskNodesRange]
	private double[][] routeProbability; //routeProbability[patrons][routeRange]
	 
	private double[][] detourTime; //detourTime[patrons][routeRange]
	private int[][] startNode; //startNode[patrons][routeRange]
	private int[][] endNode; //endNode[patrons][routeRange]
	private Route[][] visitingRoutineNodesSequence; //visitingRoutineNodesSequence[patrons][routeRange]
	/**
	 * 
	 */
	public GlobalData() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param instanceID
	 * @param noOfPatrons
	 * @param noOfNodes
	 * @param noOfTaskNodes
	 * @param noOfRoutesPerK
	 * @param noOfRoutineNode
	 * @param walkingTimes
	 * @param taskUtility
	 * @param taskNodes
	 * @param routeProbability
	 * @param detourTime
	 * @param startNode
	 * @param endNode
	 * @param visitingRoutineNodesSequence
	 */
	public GlobalData(String instanceID, int noOfPatrons, int noOfNodes,
			int noOfTaskNodes, int noOfRoutesPerK, int[][] noOfRoutineNode,
			double[][] walkingTimes, double[] taskUtility, int[] taskNodes,
			double[][] routeProbability, double[][] detourTime,
			int[][] startNode, int[][] endNode,
			Route[][] visitingRoutineNodesSequence) {
		super();
		this.instanceID = instanceID;
		this.noOfPatrons = noOfPatrons;
		this.noOfNodes = noOfNodes;
		this.noOfTaskNodes = noOfTaskNodes;
		this.noOfRoutesPerK = noOfRoutesPerK;
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

	/**
	 * @return the noOfRoutineNode
	 */
	public int[][] getNoOfRoutineNode() {
		return noOfRoutineNode;
	}

	/**
	 * @param noOfRoutineNode the noOfRoutineNode to set
	 */
	public void setNoOfRoutineNode(int[][] noOfRoutineNode) {
		this.noOfRoutineNode = noOfRoutineNode;
	}

	/**
	 * @return the instanceID
	 */
	public String getInstanceID() {
		return instanceID;
	}
	/**
	 * @param instanceID the instanceID to set
	 */
	public void setInstanceID(String instanceID) {
		this.instanceID = instanceID;
	}
	/**
	 * @return the noOfPatrons
	 */
	public int getNoOfPatrons() {
		return noOfPatrons;
	}
	/**
	 * @param noOfPatrons the noOfPatrons to set
	 */
	public void setNoOfPatrons(int noOfPatrons) {
		this.noOfPatrons = noOfPatrons;
	}
	/**
	 * @return the noOfNodes
	 */
	public int getNoOfNodes() {
		return noOfNodes;
	}
	/**
	 * @param noOfNodes the noOfNodes to set
	 */
	public void setNoOfNodes(int noOfNodes) {
		this.noOfNodes = noOfNodes;
	}
	/**
	 * @return the noOfTaskNodes
	 */
	public int getNoOfTaskNodes() {
		return noOfTaskNodes;
	}
	/**
	 * @param noOfTaskNodes the noOfTaskNodes to set
	 */
	public void setNoOfTaskNodes(int noOfTaskNodes) {
		this.noOfTaskNodes = noOfTaskNodes;
	}
	/**
	 * @return the noOfRoutesPerK
	 */
	public int getNoOfRoutesPerK() {
		return noOfRoutesPerK;
	}
	/**
	 * @param noOfRoutesPerK the noOfRoutesPerK to set
	 */
	public void setNoOfRoutesPerK(int noOfRoutesPerK) {
		this.noOfRoutesPerK = noOfRoutesPerK;
	}
	/**
	 * @return the walkingTimes
	 */
	public double[][] getWalkingTimes() {
		return walkingTimes;
	}
	/**
	 * @param walkingTimes the walkingTimes to set
	 */
	public void setWalkingTimes(double[][] walkingTimes) {
		this.walkingTimes = walkingTimes;
	}
	/**
	 * @return the taskUtility
	 */
	public double[] getTaskUtility() {
		return taskUtility;
	}
	/**
	 * @param taskUtility the taskUtility to set
	 */
	public void setTaskUtility(double[] taskUtility) {
		this.taskUtility = taskUtility;
	}
	/**
	 * @return the taskNodes
	 */
	public int[] getTaskNodes() {
		return taskNodes;
	}
	/**
	 * @param taskNodes the taskNodes to set
	 */
	public void setTaskNodes(int[] taskNodes) {
		this.taskNodes = taskNodes;
	}
	/**
	 * @return the routeProbability
	 */
	public double[][] getRouteProbability() {
		return routeProbability;
	}
	/**
	 * @param routeProbability the routeProbability to set
	 */
	public void setRouteProbability(double[][] routeProbability) {
		this.routeProbability = routeProbability;
	}
	/**
	 * @return the detourTime
	 */
	public double[][] getDetourTime() {
		return detourTime;
	}
	/**
	 * @param detourTime the detourTime to set
	 */
	public void setDetourTime(double[][] detourTime) {
		this.detourTime = detourTime;
	}
	/**
	 * @return the startNode
	 */
	public int[][] getStartNode() {
		return startNode;
	}
	/**
	 * @param startNode the startNode to set
	 */
	public void setStartNode(int[][] startNode) {
		this.startNode = startNode;
	}
	/**
	 * @return the endNode
	 */
	public int[][] getEndNode() {
		return endNode;
	}
	/**
	 * @param endNode the endNode to set
	 */
	public void setEndNode(int[][] endNode) {
		this.endNode = endNode;
	}
	/**
	 * @return the visitingRoutineNodesSequence
	 */
	public Route[][]  getVisitingRoutineNodesSequence() {
		return visitingRoutineNodesSequence;
	}
	/**
	 * @param visitingRoutineNodesSequence the visitingRoutineNodesSequence to set
	 */
	public void setVisitingRoutineNodesSequence(
			Route[][]  visitingRoutineNodesSequence) {
		this.visitingRoutineNodesSequence = visitingRoutineNodesSequence;
	}

	

}
