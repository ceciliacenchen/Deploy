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
	public static ArrayList<ArrayList<ArrayList<Integer>>> taskPenalizedRes;
	
	public AssignmentSlave_KCoverage_Exact_V2() {
		super();
	}

	/**

	 * @param noOfNodes
	 * @param noOfTaskNodes

	 * @param noOfPatrons

	 * @param taskUtility
	 * @param routeProbability
	 * @param priceVector
	 */
	public static double assignModel(int noOfNodes, int noOfTaskNodes,
			int noOfPatrons, double[] taskUtility, ArrayList<ArrayList<Double>> routeProbability,
			ArrayList<ArrayList<ArrayList<Double>>> priceVector, int kCompletion) {
		long startTime = System.nanoTime();
		nodeVisitedRes = new int[noOfTaskNodes][noOfPatrons];
		taskPenalizedRes = new ArrayList<ArrayList<ArrayList<Integer>>>();

		double totalVal = 0;
		for (int i = 0; i < noOfTaskNodes; i++) {
			taskPenalizedRes.add(new ArrayList<ArrayList<Integer>>());
			/*
			 * case one: if v_i==0. case two.
			 */
			double caseOneValue = 0;
			
			double caseTwoValue = -taskUtility[i];
			double[] agentValues = new double[noOfPatrons];
			ArrayList<ArrayList<Integer>> agentRoutePenalized =new  ArrayList<ArrayList<Integer>>();
			int[] agentId = new int[noOfPatrons];
			for (int k = 0; k < noOfPatrons; k++) {
				taskPenalizedRes.get(i).add(new ArrayList<Integer>());
				
				agentRoutePenalized.add(new ArrayList<Integer>());
				double sum = 0;
				for (int m = 0; m < routeProbability.get(k).size(); m++) {
					taskPenalizedRes.get(i).get(k).add(0);
					
					agentRoutePenalized.get(k).add(m);
					// case 2:  
					double v = taskUtility[i] *  routeProbability.get(k).get(m)/(kCompletion * 1.0) - priceVector.get(i).get(k).get(m);
					if (v < 0) {
						sum = sum + v;
						agentRoutePenalized.get(k).set(m, 1);
					} 
					sum=sum+priceVector.get(i).get(k).get(m);
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
				for (int m = 0; m < routeProbability.get(kId).size(); m++) {
					if(agentRoutePenalized.get(kId).get(m)==1) {
						taskPenalizedRes.get(i).get(kId).set(m, 1);
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
					for (int m = 0; m < routeProbability.get(k).size(); m++) {
						taskPenalizedRes.get(i).get(k).set(m, 0);
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
