/**
 * 
 */
package dbHandler;

import java.sql.ResultSet;
import java.sql.SQLException;







import com.mysql.jdbc.Connection;

import dataObjects.GlobalData;
import dataObjects.Route;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;

import org.apache.commons.collections15.Factory;

import smart.data.objects.E;
import smart.data.objects.V;
import utility.LoadProperties;
import utility.Methods;


/**
 * @author cenchen.2012
 *
 */
public class ReadDB {
	public static final double DISNATCEN_BASE=60;
	
	public static void readTasks(int startIndex,HashMap<Integer,Integer> taskPosToTaskDatabase,
			HashMap<Integer,Integer> taskIdToTaskPos,
			HashMap<Integer,Integer> taskPosToLocationId,GlobalData data) {
		Statement statement = null;
		HashMap<Integer, Integer> list = new HashMap<Integer, Integer>();
		HashMap<Integer,Double> taskPosToIncentive = new HashMap<Integer,Double>();
		try {
			Connection dbConnection=ConnectDB.connect(LoadProperties.properties);
			String selectTableSQL = "SELECT taskId,location_id,incentive FROM thivyak.task where location_id "
					+ "in (select location_id from locationmapping);";
			statement =dbConnection.createStatement();
			System.out.println(selectTableSQL);
			// execute select SQL stetement
			ResultSet rs = statement.executeQuery(selectTableSQL);
			int i=0;
			while (rs.next()) {
				String taskId = rs.getString("taskId").trim();
				String locationId = rs.getString("location_id").trim();
				String utility = rs.getString("incentive").trim();
				taskPosToTaskDatabase.put(i,Integer.parseInt(taskId));
				taskPosToLocationId.put(i, Integer.parseInt(locationId));
				taskPosToIncentive.put(i, Double.parseDouble(utility));
				i++;
			}
			statement.close();
			dbConnection.close();
			
			int[] taskNodes= new int[taskPosToTaskDatabase.keySet().size()];
			double[] taskUtility= new double[taskPosToTaskDatabase.keySet().size()];
			int index=startIndex;
			for(int tPos:taskPosToTaskDatabase.keySet()) {
				taskNodes[tPos]=index;
				taskUtility[tPos]=taskPosToIncentive.get(tPos);
				taskIdToTaskPos.put(index, tPos);
				index++;
			}
			data.setNoOfTaskNodes(taskPosToTaskDatabase.keySet().size());
			data.setTaskNodes(taskNodes);
			data.setTaskUtility(taskUtility);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void readDisMatrix(HashMap<Integer, Integer> locations,GlobalData data){
		Statement statement = null;
		double[][] distMatrix = new double[locations.size()][locations.size()];	
		try {
			Connection dbConnection=ConnectDB.connect(LoadProperties.properties);
			String selectTableSQL = "SELECT * FROM thivyak.distance;";
			statement =dbConnection.createStatement();
			System.out.println(selectTableSQL);
			// execute select SQL stetement
			ResultSet rs = statement.executeQuery(selectTableSQL);
			
			while (rs.next()) {
				String node1 = rs.getString("node1").trim();
				String node2 = rs.getString("node2").trim();
				String dist = rs.getString("dist").trim();
				//System.out.println("["+node1+" "+node2+"="+dist+"]");
				//location id to location index
				int id1=locations.get(Integer.parseInt(node1));
				int id2=locations.get(Integer.parseInt(node2));
				distMatrix[id1][id2]=Methods.round(Double.parseDouble(dist),2);
			}
			statement.close();
			dbConnection.close();
			data.setWalkingTimes(distMatrix);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static HashMap<Integer, Integer> readLocations(HashMap<Integer, Integer> locationIndexToID) {
		Statement statement = null;
		HashMap<Integer, Integer> list = new HashMap<Integer, Integer>();
		try {
			Connection dbConnection=ConnectDB.connect(LoadProperties.properties);
			String selectTableSQL = "SELECT location_id FROM thivyak.locationmapping;";
			statement =dbConnection.createStatement();
			System.out.println(selectTableSQL);
			// execute select SQL stetement
			ResultSet rs = statement.executeQuery(selectTableSQL);
			int index=0;
			while (rs.next()) {
				String node = rs.getString("location_id").trim();
				list.put(Integer.parseInt(node), index);
				locationIndexToID.put(index, Integer.parseInt(node));
				index++;
			}
			statement.close();
			dbConnection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static void readRoutes(GlobalData data, HashMap<Integer, String> uIndexToUId, HashMap<String, Integer> uIdToUIndex,
			HashMap<Integer, Integer> locationIdToDistIndex) {
		Statement statement = null;
		try {
			Connection dbConnection=ConnectDB.connect(LoadProperties.properties);
			String selectTableSQL="SELECT distinct u_id FROM thivyak.routeprediction " +
					"where u_id in (select distinct u.androidId from thivyak.user u);";
			
			statement =dbConnection.createStatement();
			System.out.println(selectTableSQL);// execute select SQL stetement
			ResultSet rs = statement.executeQuery(selectTableSQL);
			int uindex=0;
			while (rs.next()) {
				String u_id = rs.getString("u_id").trim();
				uIdToUIndex.put(u_id, uindex);
				uIndexToUId.put(uindex,u_id);
				uindex++;
			}
			statement.close();
			data.setNoOfPatrons(uIdToUIndex.keySet().size());
			
			selectTableSQL="SELECT u_id,route_id,probability, count(distinct sequence_id) as count " +
					"FROM thivyak.routeprediction " +
					"where date_time=(select max(date_time) from thivyak.routeprediction) "+
					"and u_id in (select u.androidId from thivyak.user u) "+
					"group by u_id,route_id;";
			statement =dbConnection.createStatement();
			System.out.println(selectTableSQL);
			double[][] routeProbability = new double[data.getNoOfPatrons()][data.getNoOfRoutesPerK()];
			int[][] counts= new int[data.getNoOfPatrons()][data.getNoOfRoutesPerK()];
			rs = statement.executeQuery(selectTableSQL);
			double[] sumProb=new double[data.getNoOfPatrons()];
			while (rs.next()) {
				String u_id = rs.getString("u_id").trim();
				String route_id = rs.getString("route_id").trim();
				String probability = rs.getString("probability").trim();
				String count = rs.getString("count").trim();
				int uIndex=uIdToUIndex.get(u_id);
//				System.out.println("u_id:"+u_id+" uIndex:"+uIndex+" route:"+route_id+" probability:"+probability+ " nCount:"+count);
				
				routeProbability[uIndex][Integer.parseInt(route_id)]=Double.parseDouble(probability);
				counts[uIndex][Integer.parseInt(route_id)]=Integer.parseInt(count);
				sumProb[uIndex]=sumProb[uIndex]+Double.parseDouble(probability);
			}
			for(int i=0;i<data.getNoOfPatrons();i++) {
				for(int m=0;m<data.getNoOfRoutesPerK();m++) {
					routeProbability[i][m]=Methods.round(routeProbability[i][m]/sumProb[i],4);
				}
			}
			
			statement.close();
			data.setRouteProbability(routeProbability);

			//read routes
			selectTableSQL = "SELECT u_id,location_id,sequence_id,route_id " +
					"FROM thivyak.routeprediction " +
					"where date_time=(select max(date_time) from thivyak.routeprediction) "+
					"and u_id in (select u.androidId from thivyak.user u) "+
					"order by u_id;";
			statement =dbConnection.createStatement();
			System.out.println(selectTableSQL);					
			Route[][] visitingRoutineNodesSequence= new Route[data.getNoOfPatrons()][data.getNoOfRoutesPerK()];
			for(int i=0;i<data.getNoOfPatrons();i++) {
				for(int j=0;j<data.getNoOfRoutesPerK();j++) {
					visitingRoutineNodesSequence[i][j]=new Route(counts[i][j]);
				}	
			}
			rs = statement.executeQuery(selectTableSQL);
			while (rs.next()) {
				String u_id = rs.getString("u_id").trim();
				String location_id = rs.getString("location_id").trim();
				String sequence_id = rs.getString("sequence_id").trim();
				String route_id = rs.getString("route_id").trim();
				Route r= visitingRoutineNodesSequence[uIdToUIndex.get(u_id)]
						[Integer.parseInt(route_id)];
				//System.out.println("u:"+u_id+" r:"+route_id+" rsize:"+r.getPath().size()+" seq_id:"+sequence_id+" loc_id:"+location_id);
				//map locationId into nodeIndex and store
				r.getPath().set(Integer.parseInt(sequence_id),
						locationIdToDistIndex.get(Integer.parseInt(location_id)));
			}
			//Remove duplicates
			for(int k=0;k<data.getNoOfPatrons();k++) {
				for(int m=0;m<data.getNoOfRoutesPerK();m++) {
					Route r= visitingRoutineNodesSequence[k][m];
					ArrayList<Integer> route= new ArrayList<Integer>();
					route.add(r.getPath().get(0));
					for(int i=1;i<r.getPath().size();i++) {
						if(r.getPath().get(i)!=r.getPath().get(i-1)) {
							route.add(r.getPath().get(i));
						}
					}
					r.setPath(route);
				}
			}
			
			statement.close();
			dbConnection.close();
			data.setVisitingRoutineNodesSequence(visitingRoutineNodesSequence);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static HashMap<Integer, Integer> readDetour(GlobalData data,HashMap<String, Integer> uIdToUIndex) {
		Statement statement = null;
		HashMap<Integer, Integer> list = new HashMap<Integer, Integer>();
		try {
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat today = new SimpleDateFormat("yyyy-MM-dd");
			Connection dbConnection=ConnectDB.connect(LoadProperties.properties);
			String selectTableSQL = "SELECT count(date_updated) as count FROM thivyak.detour " +
					"where date_updated > '"+today+"' ;";
			statement =dbConnection.createStatement();
			System.out.println(selectTableSQL);
			// execute select SQL stetement
			ResultSet rs = statement.executeQuery(selectTableSQL);
			boolean flush=false;
			while (rs.next()) {
				String c = rs.getString("count").trim();
				//flush and insert detours
				if(Integer.parseInt(c)==0) {
					flush=true;
				} 
			}
			
			if(flush) {
				dbConnection.setAutoCommit(false);
				statement = dbConnection.createStatement();
				String query = "TRUNCATE TABLE thivyak.detour;";	
				statement.execute(query);
				dbConnection.commit();
				System.out.println("Truncate thivyak.detour table.");

				query ="insert into thivyak.detour(u_id, remaining_detour, date_updated) " +
						"SELECT androidId,tolerance,localtimestamp() FROM thivyak.user where androidId " +
						"in (select distinct r.u_id FROM thivyak.routeprediction r);";
				statement.execute(query);
				dbConnection.commit();
				System.out.println("Insert thivyak.detour table.");
			}
			
			//select the detour from the table
			double[][] detourTime=new double[data.getNoOfPatrons()][data.getNoOfRoutesPerK()];
			selectTableSQL = "SELECT u_id,remaining_detour FROM thivyak.detour;";
			statement =dbConnection.createStatement();
			System.out.println(selectTableSQL);					
			rs = statement.executeQuery(selectTableSQL);
			while (rs.next()) {
				String u_id = rs.getString("u_id").trim();
				String detour = rs.getString("remaining_detour").trim();
				for(int i=0;i<data.getNoOfRoutesPerK();i++) {
					detourTime[uIdToUIndex.get(u_id)][i]=Double.parseDouble(detour);
				}
			}
			data.setDetourTime(detourTime);
			statement.close();
			dbConnection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
}
