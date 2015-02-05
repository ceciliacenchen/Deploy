/**
 * cenchen.2012
 */
package LR.Assignment;

import utility.CollectionHandler;
import utility.Commons;
import utility.Methods;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import com.google.common.base.Function;
import com.google.common.collect.Ordering;

/**
 * @author chencen
 *
 */
public class AssignmentSlave_KCoverage_Exact_V2 {
	public static int[][] nodeVisitedRes;
	public static int[][][] taskPenalizedRes;

	public static int[][] nodeVisitedResCase1;
	public static int[][][] taskPenalizedResCase1;

	public AssignmentSlave_KCoverage_Exact_V2() {
		super();
	}

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
	public static double assignModel(int noOfNodes, int noOfTaskNodes,
			int noOfRoutesPerK, int noOfPatrons, double[] taskUtility, double[][] routeProbability,
			double[][][] priceVector, int kCompletion) {
		long startTime = System.nanoTime();
		nodeVisitedRes = new int[noOfTaskNodes][noOfPatrons];
		taskPenalizedRes = new int[noOfTaskNodes][noOfPatrons][noOfRoutesPerK];

		double totalVal = 0;
		for (int i = 0; i < noOfTaskNodes; i++) {
			/*
			 * case one: if v_i==0. case two.
			 */
			double caseOneValue = 0;
			
			double caseTwoValue = -taskUtility[i];
			double[] agentValues = new double[noOfPatrons];
			int[][] agentRoutePenalized = new int[noOfPatrons][noOfRoutesPerK];
			int[] agentId = new int[noOfPatrons];
			for (int k = 0; k < noOfPatrons; k++) {
				double sum = 0;
				for (int m = 0; m < noOfRoutesPerK; m++) {
					// case 2:  
					double v = taskUtility[i] * routeProbability[k][m]/(kCompletion * 1.0) - priceVector[i][k][m];
					if (v < 0) {
						sum = sum + v;
						agentRoutePenalized[k][m]=1;
					} 
					sum=sum+priceVector[i][k][m];
				}
				agentValues[k]=sum;
				agentId[k]=k;
			}
			
			if(noOfPatrons>kCompletion) {
				//Selection algorithm to find k agents with smallest values 
				SelectionWithID.selectSmallest(agentValues, 0, agentValues.length-1, agentValues.length-kCompletion, agentId);	
			}
			for (int s=agentValues.length-kCompletion; s<=agentValues.length-1; s++) {
				int kId=agentId[s];
				caseTwoValue = caseTwoValue + agentValues[s];
				nodeVisitedRes[i][kId] = 1;
				for (int m = 0; m < noOfRoutesPerK; m++) {
					if(agentRoutePenalized[kId][m]==1) {
						taskPenalizedRes[i][kId][m]=1;
					}
				}
			}

			/*
			 * compare the objectives, if first one better, overwrite
			 */
			if (caseOneValue <=caseTwoValue) {
				totalVal =totalVal+ caseOneValue;
				for (int k = 0; k < noOfPatrons; k++) {
					nodeVisitedRes[i][k] = 0;
					for (int m = 0; m < noOfRoutesPerK; m++) {
						taskPenalizedRes[i][k][m]=0;
					}
				}

			} else {
				totalVal =totalVal+ caseTwoValue;
			}
		}

		long endTime = System.nanoTime();
		double duration = (endTime - startTime)/1000000000.0;
//		System.out.println("kCompletion" + kCompletion + ", noOfNodes "+ noOfNodes + ", noOfTaskNodes " + noOfTaskNodes);
//		System.out.println("This assignment run took " + duration + " s");
		return totalVal;
	}
	
	
}
