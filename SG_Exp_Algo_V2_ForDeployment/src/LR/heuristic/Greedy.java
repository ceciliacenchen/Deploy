/**
 * 
 */
package LR.heuristic;

import java.util.ArrayList;
import java.util.Arrays;

import datahandler.InputDBHandler;
import utility.CollectionHandler;
import utility.LoadProperties;

/**
 * @author cenchen.2012
 *
 */
public class Greedy {
	public static int[] value;
	public static int noOfIter=100;
	public static boolean debug=false;
	
	public static ArrayList<Integer> r;
	
	public Greedy() {
		super();
	}
	public double routing(int noOfNodes,int noOfTaskNodes, double[] priceVector,double[][] walkingTimes,
			ArrayList<Integer> taskList,ArrayList<Integer> visitingRoutineNodesSequence, double detourTime,double[] taskUtility, int k,int m,double serviceTime)  {
		long startTime = System.nanoTime();
		ArrayList<Integer> deep=CollectionHandler.deepCopyArrayList(visitingRoutineNodesSequence);
		
		double totalVal = 0;
		/**
		 * Greedy construction
		 * insert task according to highest utility/detour
		 */
		r= CollectionHandler.deepCopyArrayList(visitingRoutineNodesSequence);
		detourTime=detourTime+getTotalT(taskList,r, walkingTimes);

		boolean notExist=true;
		do {
			notExist=greedy(detourTime,priceVector, walkingTimes, taskList,taskUtility,serviceTime);
		} while(notExist);
		
		/*
		 * save results
		 */
		value=new int[noOfTaskNodes];
		for(int i=0;i<taskList.size();i++) {
			if(r.contains(taskList.get(i))) {
				totalVal=totalVal+priceVector[i];
				value[i]=1;
			} 
		}
		
		if(debug && totalVal>0) {
			System.out.println(k+"-"+m+"**********");
			System.out.println(deep);
			long endTime = System.nanoTime();
			double duration = (endTime - startTime)/1000000000.0;
			System.out.println(Arrays.toString(priceVector));
			System.out.println(k+"-"+m+"****Final route:"+r+" -v"+(-totalVal));
			System.out.println("This routing run took " + duration + " s");
			System.out.println();
		}
		return -totalVal;
	}
	
	private boolean greedy(double detourTime,double[] price, 
			double[][] walkingTimes, ArrayList<Integer> taskList,double[] taskUtility,double serviceTime) {
		boolean notExit=true;
		double t=getTotalT(taskList,r, walkingTimes);

		int bestTask=-1;
		int bestPos=-1;
		double bestMarginalUtility=0;
		
		for(int i=0; i<taskList.size();i++) {
			int task=taskList.get(i);
			if(!r.contains(task)) {
				for(int p=1;p<r.size();p++) {
					double dist=0;

					int taskNodeIndex=InputDBHandler.locationIdToDistIndex.get(InputDBHandler.taskPosToLocationId.get(i));
					int node1=(taskList.contains(r.get(p-1)))? 
						InputDBHandler.locationIdToDistIndex.get(InputDBHandler.taskPosToLocationId.get(InputDBHandler.taskIdToTaskPos.get(r.get(p-1)))):
						r.get(p-1);
					int node2=(taskList.contains(r.get(p)))?
							InputDBHandler.locationIdToDistIndex.get(InputDBHandler.taskPosToLocationId.get(InputDBHandler.taskIdToTaskPos.get(r.get(p)))):
						r.get(p);
					dist=walkingTimes[node1][taskNodeIndex]+walkingTimes[taskNodeIndex][node2]
							-walkingTimes[node1][node2]+serviceTime;
					
					double detourIncurred=dist+t;
					if(dist+t<=detourTime) {
						double marginalUtility= price[i]*taskUtility[i]/detourIncurred;
						if(bestMarginalUtility<marginalUtility) {
							bestMarginalUtility=marginalUtility;
							bestTask=task;
							bestPos=p;
						}
					}
				}
			}
		}
			
		if(bestPos!=-1 && bestMarginalUtility!=0 && bestTask!=-1) {
			r.add(bestPos, bestTask);
		}else {
			notExit=false;
		}
		return notExit;
	}
	

	private double getTotalT(ArrayList<Integer> taskList,ArrayList<Integer> route,double[][] walkingTimes) {
		double total=0;
		for(int i=0;i<route.size()-1;i++) {
			double dist=0;
			int id1=(taskList.contains(route.get(i)))? 
					InputDBHandler.locationIdToDistIndex.get(InputDBHandler.taskPosToLocationId.get(InputDBHandler.taskIdToTaskPos.get(route.get(i))))
					:route.get(i);
			int id2=(taskList.contains(route.get(i+1)))? 
					InputDBHandler.locationIdToDistIndex.get(InputDBHandler.taskPosToLocationId.get(InputDBHandler.taskIdToTaskPos.get(route.get(i+1))))
					:route.get(i+1);
			dist=walkingTimes[id1][id2];
			total=total+dist;
		}
		return total;
	}
}
