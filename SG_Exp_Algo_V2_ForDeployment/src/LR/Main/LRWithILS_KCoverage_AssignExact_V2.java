package LR.Main;
import dataObjects.GlobalData;
import datahandler.InputDBHandler;
import dbHandler.ConnectDB;

import java.io.File;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.mysql.jdbc.Connection;

import utility.CollectionHandler;
import utility.FileManager;
import utility.LoadProperties;
import utility.Methods;
import LR.heuristic.*;
import LR.Assignment.*;


/**
 * @author chencen
 *
 */
public class LRWithILS_KCoverage_AssignExact_V2 {		
	private static ArrayList<ArrayList<ArrayList<Double>>> priceVectorAll;//[i in taskNodesRange][k in patrons][m in routeRange]
	private static ArrayList<ArrayList<ArrayList<Integer>>> slaveValues;//i, k, 
	private static int[][] bestAssignment; //[k][i]
	
	public static void main2(String[] args) throws SQLException{
		LoadProperties.load();
		
		ArrayList<String> dirs = new ArrayList<String>();
			GlobalData data=init(); 
			long startTime = System.nanoTime();
			mainFlow(data);
			long endTime = System.nanoTime();
			double duration = (endTime - startTime)/1000000000.0;
			System.out.println("Main Flow took " + duration+ " s");
	}

	private static GlobalData init()throws SQLException {
		GlobalData data = null;
		InputDBHandler r = new InputDBHandler();
		r.readInstance();
		data = r.data;
		return data;
	}

	private static void mainFlow(GlobalData data) {
		double serviceTime =Double.parseDouble(LoadProperties.properties.get("servicetime").toString().trim());
		double scale =Double.parseDouble(LoadProperties.properties.get("scale").toString().trim());
		int iterationToHalving=Integer.parseInt(LoadProperties.properties.get("iterationToHalving").toString().trim());
		int kCompletion=Integer.parseInt(LoadProperties.properties.get("kCompletion").toString().trim());
		double dualityGapLimit=Double.parseDouble(LoadProperties.properties.get("dualityGapLimit").toString().trim());
		int noImprovementToStop=Integer.parseInt(LoadProperties.properties.get("noImprovementToStop").toString().trim());
		
		bestAssignment=null;
		priceVectorAll=new ArrayList<ArrayList<ArrayList<Double>>>();	
		System.out.println("kCompletion:"+kCompletion+", noOfNodes:"+data.getNoOfNodes()+", noOfTaskNodes:"+ data.getNoOfTaskNodes());
    	ArrayList<Integer>  taskList = new ArrayList<Integer>(data.getTaskNodes().length);
        for (int i = 0; i < data.getTaskNodes().length; i++) {
        	priceVectorAll.add(new ArrayList<ArrayList<Double>>());
			for(int k=0; k<data.getNoOfPatrons();k++) {
				priceVectorAll.get(i).add(new ArrayList<Double>());
				for(int  m=0; m<data.getDetourTime().get(k).size();m++) {
					priceVectorAll.get(i).get(k).add(0.0);
				}
        	}
        	taskList.add(data.getTaskNodes()[i]);
        }	
		long startTime = System.currentTimeMillis();
		//arrays to store the UB, LB, scale and step values at each iteration
		ArrayList<Double> dualObj = new ArrayList<Double>();
		ArrayList<Double> primalObj = new ArrayList<Double>();
		ArrayList<Double> primalBestObj = new ArrayList<Double>();
		ArrayList<Double>dualBestObj = new ArrayList<Double>();
		ArrayList<Double> time = new ArrayList<Double>();
		/************************************************************************
		 * beginning the lagrangian calculation here... 
		 ************************************************************************/
		// begin the lagrangian calculation here   
		System.out.println();
		System.out.println(" beginning the lagrangian calculation here... ");

		// executes LowerBound and UpperBound model 
		double dualObjV;
		double primalObjective;
		int counterNoChange=0;
		int t=0;
		int countNoImprovement=0;
		double gap=0;
		boolean continueRun=true;
		do {
			slaveValues=new ArrayList<ArrayList<ArrayList<Integer>>>();	
	        for (int i = 0; i < data.getTaskNodes().length; i++) {
	        	slaveValues.add(new ArrayList<ArrayList<Integer>>());
				for(int k=0; k<data.getNoOfPatrons();k++) {
					slaveValues.get(i).add(new ArrayList<Integer>());
					for(int  m=0; m<data.getDetourTime().get(k).size();m++) {
						slaveValues.get(i).get(k).add(0);
					}
	        	}
	        }

			dualObjV=0.0;
			System.out.println();
			System.out.println(" ITERATION:  " +t);  

			/************************************************************************
			 * setup model for assignment slaves: AssignmentSlave
			 ************************************************************************/
			AssignmentSlave_KCoverage_Exact_V2 a = new AssignmentSlave_KCoverage_Exact_V2();
			double assingmentSlaveObjective=a.assignModel(data.getNoOfNodes(), data.getNoOfTaskNodes(),
					data.getNoOfPatrons(),data.getTaskUtility(),
					data.getRouteProbability(), priceVectorAll,kCompletion);
			if(assingmentSlaveObjective!=Double.MAX_VALUE) {
				System.out.println("Assignment objective-exactV2: "+assingmentSlaveObjective);
				dualObjV=dualObjV+assingmentSlaveObjective;
			}
			
			for(int k=0; k<data.getNoOfPatrons();k++) {
				for(int  m=0; m<data.getDetourTime().get(k).size();m++) {
					/************************************************************************
					 * setup model for routing slaves: RoutingSlave
					 ************************************************************************/
					double[] priceVector= new double[data.getNoOfTaskNodes()];
					for(int i=0; i<data.getNoOfTaskNodes();i++) {
						priceVector[i]=priceVectorAll.get(i).get(k).get(m);
					}	

					String heuristic=LoadProperties.properties.get("heuristic").toString().trim();
					
					if(heuristic.equalsIgnoreCase("LS")) {
						ILS rt= new ILS();
						double routingSlaveObjective = rt.routing(data.getNoOfNodes(), 
								data.getNoOfTaskNodes(),priceVector,
								data.getWalkingTimes(),taskList,
								data.getVisitingRoutineNodesSequence().get(k).get(m).getPath(),
								data.getDetourTime().get(k).get(m),data.getTaskUtility(),k,m,serviceTime);

						dualObjV=dualObjV+routingSlaveObjective;
						for(int i=0;i<data.getNoOfTaskNodes();i++) {  
							slaveValues.get(i).get(k).set(m, rt.value[i]);
						}
					} else if(heuristic.equalsIgnoreCase("Greedy")) {
						Greedy rt= new Greedy();
						double routingSlaveObjective = rt.routing(data.getNoOfNodes(), 
								data.getNoOfTaskNodes(),priceVector,
								data.getWalkingTimes(),taskList,
								data.getVisitingRoutineNodesSequence().get(k).get(m).getPath(),
								data.getDetourTime().get(k).get(m),data.getTaskUtility(),k,m,serviceTime);

						dualObjV=dualObjV+routingSlaveObjective;
						for(int i=0;i<data.getNoOfTaskNodes();i++) {  
							slaveValues.get(i).get(k).set(m, rt.value[i]);
						}	
					}				
				}   	        
			}
			
			/************************************************************************
			 * setup model primal problem
			 ************************************************************************/
			PrimalMIP_KCoverage_Exact primal=new PrimalMIP_KCoverage_Exact();
			primalObjective=primal.primalModel(data.getNoOfNodes(), data.getNoOfTaskNodes(),data.getNoOfPatrons(),data.getTaskUtility(),
					data.getRouteProbability(),slaveValues,kCompletion);
			if(primalObjective!=Double.MAX_VALUE) {
				System.out.println("Primal objective-exact: "+primalObjective);
			} 
			
			System.out.println("*****************"); 

			dualObj.add(dualObjV);
			primalObj.add(primalObjective);
			/************************************************************************
			 *Adjust stepsize here
			 ************************************************************************/
			if(t==0) {
				dualBestObj.add(dualObj.get(t));
				primalBestObj.add(primalObj.get(t));
			}else {	
				double largestDual=Double.NEGATIVE_INFINITY;
				double smallestPrimal=Double.POSITIVE_INFINITY;
		        for(int i =0;i<dualObj.size();i++) {
		            if(dualObj.get(i)> largestDual) {
		            	largestDual =dualObj.get(i);
		            }
		            if(primalObj.get(i)< smallestPrimal) {
		            	smallestPrimal =primalObj.get(i);
		            }
		        }
				dualBestObj.add(largestDual);
				primalBestObj.add(smallestPrimal);
			}
			if(t==0){
				bestAssignment=primal.currentAssignment;
			} else if(primalBestObj.get(t)==primalObjective) {
				bestAssignment=primal.currentAssignment;
			}    
			//no improvement for dual, the dual value should increase			
			if(t>=1) {//did increase
				if(dualObjV<=dualObj.get(t-1)) {
					counterNoChange++;				      	
				} else {
					counterNoChange=0;
				}	
				if(counterNoChange==iterationToHalving) {
					scale=scale*0.5;
					counterNoChange=0;
				} 
			} else {
				counterNoChange++;
			}
			/************************************************************************
			 *Update lagrangian multipliers here
			 ************************************************************************/
			//obtain the best primal solution as the dual UB 
			ArrayList<ArrayList<ArrayList<Double>>> deltas=new ArrayList<ArrayList<ArrayList<Double>>>();
			for(int i=0; i<data.getNoOfTaskNodes();i++) {  	 
				deltas.add(new ArrayList<ArrayList<Double>>());
				for(int k=0; k<data.getNoOfPatrons();k++) {
					deltas.get(i).add(new ArrayList<Double>());
					for(int m=0; m<data.getDetourTime().get(k).size();m++) {
						deltas.get(i).get(k).add(0.0);
					}
				}
			}
			double norm=0;
			for(int i=0; i<data.getNoOfTaskNodes();i++) {  
				for(int k=0; k<data.getNoOfPatrons();k++) {
					for(int m=0; m<data.getDetourTime().get(k).size();m++) {
						double v=(double) a.nodeVisitedRes[i][k]-a.taskPenalizedRes.get(i).get(k).get(m)-slaveValues.get(i).get(k).get(m);
						deltas.get(i).get(k).set(m,v);
						norm=norm+Math.pow(deltas.get(i).get(k).get(m),2);	 
					}
				}
			} 
			double UB = primalBestObj.get(t);
			double step = scale*((UB-dualObjV) / norm);  

			System.out.println("Primal="+primalObj.get(t)+" Dual="+dualObj.get(t)); 
			System.out.println("PrimalBest=" + UB + " DualBest=" + dualBestObj.get(t) + " Norm=" + norm + " Scale=" + scale+" Step="+step);
			for(int k=0; k<data.getNoOfPatrons();k++) {
				for(int m=0; m<data.getDetourTime().get(k).size();m++) {
					for(int i=0; i<data.getNoOfTaskNodes();i++) {   	    	      	    	  	
//						System.out.println("pricevector for (k,m,i)=("+k+","+m+","+i+") scale="+scale+" delta="+delta+" step="+step);			
						priceVectorAll.get(i).get(k).set(m, priceVectorAll.get(i).get(k).get(m)+ step*deltas.get(i).get(k).get(m));
						if(priceVectorAll.get(i).get(k).get(m)<=0) {
							priceVectorAll.get(i).get(k).set(m,0.0);
						}	 
					}
				}
			}	
			time.add(((System.currentTimeMillis() - startTime)/1000.0));
			gap=(primalBestObj.get(t)==0)?Double.POSITIVE_INFINITY:
					Methods.round((dualBestObj.get(t)-primalBestObj.get(t))*100/primalBestObj.get(t),4);
			double gap2=(primalBestObj.get(t)==0)?Double.POSITIVE_INFINITY:
				Methods.round((dualObj.get(t)-primalObj.get(t))*100/primalObj.get(t),4);
			System.out.println("duality gap: "+gap+"%  -  "+gap2+"%");
			t++; 
			
			countNoImprovement=0;
			double lastbest=primalBestObj.get(primalBestObj.size()-1);
			out:
			for (int i=primalBestObj.size()-2;i>=0;i--)  {
				if(primalBestObj.get(i)==lastbest) {
					countNoImprovement++;
				}else{
					break out;
				}
			}	
			if(gap>dualityGapLimit) {
				if(countNoImprovement>noImprovementToStop) {
					continueRun=false;
				}	
			} else {
				continueRun=false;
			}		
		} while(continueRun);//end of main "for loop"	
			
		printProgress( dualObj,primalObj, primalBestObj,dualBestObj, time,data.getNoOfPatrons());
		writeRes(data.getNoOfTaskNodes(),data.getNoOfPatrons());
	}
	
	public static void printProgress(ArrayList<Double> dualObj,ArrayList<Double> primalObj,ArrayList<Double> primalBestObj,
			ArrayList<Double>dualBestObj,ArrayList<Double> time, int noofPatrons) {
		System.out.println();
		System.out.print("primalObj = ");
		for (int i=0;i<primalObj.size();i++)  {
			System.out.print(primalObj.get(i)+" ");
		}
		System.out.println();  
		
		System.out.print("primalBestObj = ");
		for (int i=0;i<primalObj.size();i++)  {
			System.out.print(primalBestObj.get(i)+" ");
		}
		System.out.println();
		
		System.out.print("dualObj = ");
		for (int i=0;i<primalObj.size();i++)  {
			System.out.print(dualObj.get(i)+" ");
		}
		System.out.println();
		
		System.out.print("dualBestObj = ");
		for (int i=0;i<primalObj.size();i++)  {
			System.out.print(dualBestObj.get(i)+" ");
		}
		System.out.println();
		
		System.out.print("time = ");
		for (int i=0;i<primalObj.size();i++)  {
			System.out.print(time.get(i)+" ");
		}
		System.out.println();
	}
	
	public static void writeRes(int noofTask, int noofNopatrons) {
		Timestamp t = new java.sql.Timestamp(new Date().getTime());
		Connection dbConnection=ConnectDB.connect(LoadProperties.properties);
		Statement statement = null;
		try {
			statement = dbConnection.createStatement();
			dbConnection.setAutoCommit(false);
			for(int k=0;k<noofNopatrons;k++) {
				for(int i=0;i<noofTask;i++) {
					if(bestAssignment[k][i]==1) {
						String uid=InputDBHandler.uIndexToUId.get(k);
						int task=InputDBHandler.taskPosToTaskDatabase.get(i);
						System.out.println("User: "+uid+" -t:"+task);
						String truncateTableSQL1 ="truncate table recommendation;";
						statement.execute(truncateTableSQL1);
						System.out.println(truncateTableSQL1);
						String insertTableSQL1 = "INSERT INTO recommendation"
								+ "(User_id, task_id,tolerance, date_record) VALUES ( "
								+ "(SELECT userId from user_info u where u.androidId='"+ uid+"'),"
								+task+","+ "(SELECT u.tolerance from user_info u where u.androidId='"+ uid+"'), '"
								+t+"');";
						System.out.println(insertTableSQL1);
						statement.execute(insertTableSQL1);
					}
				}
			}
			dbConnection.commit();
			System.out.println("Records are inserted into recomendation table.");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (dbConnection != null) {
				try {
					dbConnection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
