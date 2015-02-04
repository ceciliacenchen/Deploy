/**
 * 
 */
package LR.heuristic;

import java.util.ArrayList;
import java.util.Arrays;

import datahandler.InputDBHandler;
import utility.CollectionHandler;

/**
 * @author cenchen.2012
 *
 */
public class ILS {
	public static int[] value;
	public static int[][]linkResult;
	public static int noOfIter=100;
	public static boolean debug=false;
	
	public static ArrayList<Integer> r;
	
	public ILS() {
		super();
	}
	public double routing(int noOfNodes,int noOfTaskNodes, double[] priceVector,double[][] walkingTimes,
			ArrayList<Integer> taskList,ArrayList<Integer> visitingRoutineNodesSequence, double detourTime,double[] taskUtility, int k,int m)  {
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
			notExist=greedy(detourTime,priceVector, walkingTimes, taskList,taskUtility);
//			System.out.println(getTotalT(taskList,r, walkingTimes,readFormat)+""+r);
		} while(notExist);
	
		/*
		 * ILS 
		 */
		int count=0;
		notExist=true;
		do {
			int countchanged=0;
			if(exchange(walkingTimes,taskList)){
				countchanged++;
//				System.out.println("exchanged:"+getTotalT(taskList,r, walkingTimes,readFormat)+""+r);
			} 
			if( replace( detourTime,priceVector, walkingTimes, taskList,taskUtility)){
				countchanged++;
//				System.out.println("replaced:"+getTotalT(taskList,r, walkingTimes,readFormat)+""+r);
			}
			
			if(greedy( detourTime,priceVector, walkingTimes, taskList,taskUtility)) {
				countchanged++;
//				System.out.println("added:"+getTotalT(taskList,r, walkingTimes,readFormat)+""+r);
			}
			if(countchanged==0){
				notExist=false;
			}
			count++;
		}while(count<noOfIter &&  notExist);
		
		/*
		 * save results
		 */
		value=new int[noOfTaskNodes];
		linkResult=new int[noOfNodes][noOfNodes];
		for(int i=0;i<taskList.size();i++) {
			if(r.contains(taskList.get(i))) {
				totalVal=totalVal+priceVector[i];
				value[i]=1;
//				System.out.println(taskList.get(i)+":"+priceVector[i]);
			} 
		}

		for(int i=0;i<r.size()-1;i++) {
			linkResult[r.get(i)][r.get(i+1)]=1;
		}
		
		if(debug && totalVal>0) {
			System.out.println(k+"-"+m+"**********");
			System.out.println(deep);
			long endTime = System.nanoTime();
			double duration = (endTime - startTime)/1000000000.0;
			System.out.println(Arrays.toString(priceVector));
			System.out.println(k+"-"+m+"****Final route:"+r+" -v"+(-totalVal));
//			getTotalTPrintDist(taskList,r, walkingTimes,readFormat);
			System.out.println("This routing run took " + duration + " s");
			System.out.println();
		}

		return -totalVal;
	}

	private boolean exchange(double[][] walkingTimes,ArrayList<Integer> taskList) {
		boolean routechanged=false;
		double t=getTotalT(taskList,r, walkingTimes);	
		
		int besti=-1;
		int bestj=-1;
		double bestT=t;
		for(int i=0;i<r.size();i++ ) {
			for(int j=i+1;j<r.size();j++ ) {
				if(taskList.contains(r.get(i)) && taskList.contains(r.get(j))) {
					ArrayList<Integer> temp= CollectionHandler.deepCopyArrayList(r);
					temp.set(i, r.get(j));
					temp.set(j, r.get(i));
					double tempT=getTotalT(taskList,temp, walkingTimes);
					if(tempT<bestT) {
						besti=i;
						bestj=j;
						bestT=tempT;
					}
				}
			}
		}
		if(besti!=-1 && bestj!=-1) {
			int taski=r.get(bestj);
			int taskj=r.get(besti);
			r.set(besti,taski);
			r.set(bestj, taskj);
			routechanged=true;
		}
		return routechanged;
	}
	
	private boolean replace(double detourTime,double[] price, 
			double[][] walkingTimes, ArrayList<Integer> taskList, double[] taskUtility) {
		boolean routechanged=false;
		int besti=-1;
		int bestj=-1;
		double marginal=0;
		
		double baseBaforeT=getTotalT(taskList,r, walkingTimes);
		for(int i=0;i<r.size();i++ ) {
			int pos=taskList.indexOf(r.get(i));
			if(pos!=-1) {
				double tDifferece=getTDiff(r.get(i), taskList, i,r, walkingTimes);
				double baseT=baseBaforeT-tDifferece;
				double currentTMarginal=price[pos] * price[pos] / tDifferece;
				
				for(int j=0;j<taskList.size();j++ ) {
					//if route r does not contain task j
					if(!r.contains(taskList.get(j))) {						
						double tDifferenceReplacde=getTDiff(taskList.get(j), taskList,i, r,walkingTimes);						
						if(tDifferenceReplacde+baseT<=detourTime) {
							double tempMarginal=price[j]*taskUtility[j] / tDifferenceReplacde;
							if (tempMarginal >= currentTMarginal) {
								if (tempMarginal> marginal) {
									marginal = tempMarginal;
									besti = i;
									bestj = j;
								}
							}
						}
					}
				}
			}
		}
		if(besti!=-1 && bestj!=-1) {
//			System.out.println("replaced: "+taskList.get(besti)+" to "+taskList.get(bestj));
			r.set(besti, taskList.get(bestj));	
			routechanged=true;
		}
		return routechanged;
	}
	
	private double getTDiff(int node, ArrayList<Integer>taskList,int i,ArrayList<Integer> route,
			double[][] walkingTimes) {
		double dist=0;		
		int taskNodeIndex=(taskList.contains(node))? 
				InputDBHandler.locationIdToDistIndex.get(InputDBHandler.taskPosToLocationId.get(InputDBHandler.taskIdToTaskPos.get(node))):node;
		int node1=(taskList.contains(route.get(i)))? 
				InputDBHandler.locationIdToDistIndex.get(InputDBHandler.taskPosToLocationId.get(InputDBHandler.taskIdToTaskPos.get(route.get(i)))):
			route.get(i);
		int node2=(taskList.contains(route.get(i+1)))?					
				InputDBHandler.locationIdToDistIndex.get(InputDBHandler.taskPosToLocationId.get(InputDBHandler.taskIdToTaskPos.get(route.get(i+1)))):
			route.get(i+1);
		dist=walkingTimes[node1][taskNodeIndex]+walkingTimes[taskNodeIndex][node2]
				-walkingTimes[node1][node2];
		return dist;
	}

	
	
	private boolean greedy(double detourTime,double[] price, 
			double[][] walkingTimes, ArrayList<Integer> taskList,double[] taskUtility) {  
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
//					int taskNodeIndex=InputDBHandler.locationIdToDistIndex.get(InputDBHandler.taskPosToLocationId.get(i));
//					int node1=(taskList.contains(r.get(p-1)))? 
//						InputDBHandler.locationIdToDistIndex.get(InputDBHandler.taskPosToLocationId.get(InputDBHandler.taskIdToTaskPos.get(r.get(p-1)))):
//						r.get(p-1);
//					int node2=(taskList.contains(r.get(p)))?
//							InputDBHandler.locationIdToDistIndex.get(InputDBHandler.taskPosToLocationId.get(InputDBHandler.taskIdToTaskPos.get(r.get(p)))):
//						r.get(p);
//					dist=walkingTimes[node1][taskNodeIndex]+walkingTimes[taskNodeIndex][node2]
//							-walkingTimes[node1][node2];
					dist=getTDiff(task,taskList,p-1,r, walkingTimes);
				
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
