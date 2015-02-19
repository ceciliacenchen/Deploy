/**
 * 
 */
package LR.Assignment;
import datahandler.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import utility.CollectionHandler;
import utility.Methods;

/**
 * @author chencen
 *
 */
public class PrimalMIP_KCoverage_Exact {
	public static int[][] currentAssignment;

	/**
	 * @param cplex
	 * @param noOfNodes
	 * @param noOfTaskNodes
	 * @param noOfRoutineNode
	 * @param noOfRoutesPerK
	 * @param noOfPatrons
	 * @param taskNodes
	 * @param taskUtility
	 * @param routeProbability
	 * @param priceVector
	 */
	// int[][][] slaveValues;//i, k, m
	public static double primalModel(int noOfNodes, int noOfTaskNodes, int noOfPatrons,
			double[] taskUtility, ArrayList<ArrayList<Double>> routeProbability,
			ArrayList<ArrayList<ArrayList<Integer>>> slaveValuesIKM, int kCompletion) {
		long startTime = System.nanoTime();
		double totalVal = 0;
		currentAssignment = new int[noOfPatrons][noOfTaskNodes];
		for (int i = 0; i < noOfTaskNodes; i++) {
			double[] agentValues = new double[noOfPatrons];
			int[] agentId = new int[noOfPatrons];
			boolean checkAssign = false;
			for (int k = 0; k < noOfPatrons; k++) {
				double vForK = 0;
				for (int m = 0; m < routeProbability.get(k).size(); m++) {
					vForK = vForK +  routeProbability.get(k).get(m)*slaveValuesIKM.get(i).get(k).get(m);
					if(checkAssign==false && slaveValuesIKM.get(i).get(k).get(m)>0) {
						checkAssign = true;
					}
				}
				agentValues[k] = vForK;
				agentId[k] = k;
			}
			
			if (checkAssign) {
				if (noOfPatrons > kCompletion) {
					// Selection algorithm to find k agents with smallest values
					SelectionWithID.selectBiggest(agentValues, 0,agentValues.length-1, agentValues.length-kCompletion, agentId);
				}
				// task is assigned to n agents
				double innerV = 0;
				for (int s = agentValues.length-kCompletion; s <agentValues.length; s++) {
					currentAssignment[agentId[s]][i] = 1;
					for (int m = 0; m < routeProbability.get(agentId[s]).size(); m++) {
						innerV = innerV + routeProbability.get(agentId[s]).get(m)* (1-slaveValuesIKM.get(i).get(agentId[s]).get(m));
					}
				}
				innerV=innerV /(kCompletion * 1.0);
				totalVal = totalVal + taskUtility[i]* (innerV-1.0);
			}
		}

		long endTime = System.nanoTime();
		double duration = (endTime - startTime) / 1000000000.0;
//		System.out.println("This primal run took " + duration + " s");
		return totalVal;
	}

}
