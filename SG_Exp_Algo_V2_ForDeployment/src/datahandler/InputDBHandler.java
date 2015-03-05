package datahandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import utility.CollectionHandler;
import utility.LoadProperties;
import dataObjects.GlobalData;
import dataObjects.Route;
import dbHandler.ReadDB;

public class InputDBHandler {
	private static boolean debug = true;
	public static GlobalData data= new GlobalData();
	
	public static HashMap<Integer, Integer> locationIdToDistIndex= new HashMap<Integer, Integer>();
	public static HashMap<Integer, Integer> distIndexToLocationID= new HashMap<Integer, Integer>();
	
	public static HashMap<Integer,Integer> taskPosToTaskDatabase= new HashMap<Integer,Integer>(); //pos-->taskId in the database, mapping back
	public static HashMap<Integer,Integer> taskIdToTaskPos= new HashMap<Integer,Integer>(); //taskId-->pos 
	public static HashMap<Integer,Integer> taskPosToLocationId= new HashMap<Integer,Integer> ();//pos-->locationId
	
	public static HashMap<String, Integer> uIdToUIndex = new HashMap<String, Integer> (); //userId in the datebase to userIndex
	public static HashMap<Integer, String> uIndexToUId= new HashMap<Integer, String> (); //userIndex to userId in the datebase
	
	public static void readInstance() {
		//set instance id
		Date d= new Date();
		data.setInstanceID(d.getTime()+"");
		ReadDB.resetDb();
		//read locations 
		//locationId in the database to location id in the program
		locationIdToDistIndex =ReadDB.readLocations(distIndexToLocationID);  
		ReadDB.readDisMatrix(locationIdToDistIndex,data); // set distance matrix
				
		//taskNodes: pos-->taskId in the program
		ReadDB.readTasks(locationIdToDistIndex.keySet().size(),
				taskPosToTaskDatabase,taskIdToTaskPos,
				taskPosToLocationId,data); //nodeId, locationId
		
		System.out.println("location: "+locationIdToDistIndex.keySet().size()+" tasks:"+taskPosToTaskDatabase.keySet().size());		
		data.setNoOfNodes(locationIdToDistIndex.keySet().size()+taskPosToTaskDatabase.keySet().size());
		
		//set users, routes
		ReadDB.readRoutes(data, uIndexToUId, uIdToUIndex,locationIdToDistIndex);
		
		//detour
		ReadDB.readDetour( data, uIdToUIndex);
		
		if (debug) {
			System.out.println("numAgents: " + data.getNoOfPatrons());
			System.out.println("numOfNodes: " + data.getNoOfNodes());
			System.out.println();
			System.out.println("numOfTask: " + data.getNoOfTaskNodes());
			System.out.print("tasksNodes: ");
			System.out.println(taskPosToTaskDatabase.values());
			System.out.print("tasksIndex: ");
			CollectionHandler.printArrays(data.getTaskNodes());
			System.out.print("taskUtility: ");
			CollectionHandler.printArrays(data.getTaskUtility());
			System.out.println();
			System.out.println("routeProbability: ");
			for(ArrayList<Double> s:data.getRouteProbability()) {
				System.out.println(s);
			}
			System.out.println("visitingRoutineNodesSequence: ");
			for(ArrayList<Route> s:data.getVisitingRoutineNodesSequence()) {
				for(Route r:s) {
					System.out.println(r.getPath());
				}
			}
			System.out.println("detourTime: ");
			for(ArrayList<Double> s:data.getDetourTime()) {
				System.out.println(s);
			}
//			System.out.println("Distance matrix");
//			for(double[] s:data.getWalkingTimes()) {
//				System.out.println(Arrays.toString(s));
//			}
		}
	}
}
