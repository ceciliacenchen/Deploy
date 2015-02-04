/**
 * 
 */
package LR.Assignment;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloNumExpr;
import ilog.cplex.IloCplex;
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
	public static double primalModel(int noOfNodes, int noOfTaskNodes,
			int noOfRoutesPerK, int noOfPatrons,
			double[] taskUtility, double[][] routeProbability,
			int[][][] slaveValuesIKM, int kCompletion) {
		long startTime = System.nanoTime();

		double totalVal = 0;
		currentAssignment = new int[noOfPatrons][noOfTaskNodes];
		for (int i = 0; i < noOfTaskNodes; i++) {
			double[] agentValues = new double[noOfPatrons];
			int[] agentId = new int[noOfPatrons];
			boolean checkAssign = false;
			for (int k = 0; k < noOfPatrons; k++) {
				double vForK = 0;
				for (int m = 0; m < noOfRoutesPerK; m++) {
					vForK = vForK +  routeProbability[k][m]*slaveValuesIKM[i][k][m];
					if(checkAssign==false && slaveValuesIKM[i][k][m]>0) {
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
					for (int m = 0; m < noOfRoutesPerK; m++) {
						innerV = innerV + routeProbability[agentId[s]][m]* (1-slaveValuesIKM[i][agentId[s]][m]);
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
