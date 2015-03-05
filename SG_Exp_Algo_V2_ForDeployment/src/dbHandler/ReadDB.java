/**
 * 
 */
package dbHandler;

import java.sql.ResultSet;
import java.sql.SQLException;







import com.mysql.jdbc.Connection;

import org.joda.time.DateTime;
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
			DateTime curTime = new DateTime();
			String hour ="9";
			if(Integer.parseInt(curTime.hourOfDay().getAsText()) < 9)
				hour = "9";
			else if (Integer.parseInt(curTime.hourOfDay().getAsText()) < 12)
				hour = "12";
			else hour = "15";
			Connection dbConnection=ConnectDB.connect(LoadProperties.properties);
			String selectTableSQL = "SELECT taskId,location_id,incentive,HOUR(timeFrom) as h FROM task where location_id "
					+ "in (select location_id from locationmapping) and HOUR(timeFrom)="+hour+";";
			statement =dbConnection.createStatement();
			System.out.println(selectTableSQL);
			// execute select SQL stetement
			ResultSet rs = statement.executeQuery(selectTableSQL);
			int i=0;
			while (rs.next()) {
				String taskId = rs.getString("taskId").trim();
				String locationId = rs.getString("location_id").trim();
				String utility = rs.getString("incentive").trim();
				//System.out.println(rs.getString("h").trim());
				taskPosToTaskDatabase.put(i, Integer.parseInt(taskId));
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

	public static void resetDb(){
		Statement statement = null;
		try {
			Connection dbConnection = ConnectDB.connect(LoadProperties.properties);
			statement =dbConnection.createStatement();
			String updateTableSQL = "UPDATE user_info set isfirsttime=1 ; ";
			statement.execute(updateTableSQL);
			updateTableSQL = "UPDATE user_info set completedtasks=0";
			statement.execute(updateTableSQL);
			//ResultSet rs = statement.executeQuery(updateTableSQL);
			statement.close();
			dbConnection.close();
		}
		catch(SQLException e){
			e.printStackTrace();
		}

	}
	
	public static void readDisMatrix(HashMap<Integer, Integer> locations,GlobalData data){
		Statement statement = null;
		double[][] distMatrix = new double[locations.size()][locations.size()];	
		try {
			Connection dbConnection=ConnectDB.connect(LoadProperties.properties);
			String selectTableSQL = "SELECT * FROM distance;";
			statement =dbConnection.createStatement();
			System.out.println(selectTableSQL);
			// execute select SQL stetement
			ResultSet rs = statement.executeQuery(selectTableSQL);
			while (rs.next()) {
				String node1 = rs.getString("node1").trim();
				String node2 = rs.getString("node2").trim();
				String dist = rs.getString("dist").trim();
				System.out.println("["+node1+" "+node2+"="+dist+"]");
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
			String selectTableSQL = "SELECT distinct section_id FROM locationmapping;";
			statement =dbConnection.createStatement();
			System.out.println(selectTableSQL);
			// execute select SQL stetement
			ResultSet rs = statement.executeQuery(selectTableSQL);
			int index=0;
			while (rs.next()) {
				String node = rs.getString("section_id").trim();
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
			String selectTableSQL="SELECT distinct u_id, count(distinct route_id) as count "
					+ " FROM routeprediction " +
					"where u_id in (select distinct u.androidId from user_info u)"
					+ "group by u_id;";
			
			statement =dbConnection.createStatement();
			System.out.println(selectTableSQL);// execute select SQL stetement
			ResultSet rs = statement.executeQuery(selectTableSQL);
			int uindex=0;
			HashMap<Integer, Integer> u_IndexToM=new HashMap<Integer, Integer>();
			while (rs.next()) {
				String u_id = rs.getString("u_id").trim();
				int rcount = Integer.parseInt(rs.getString("count").trim());
				uIdToUIndex.put(u_id, uindex);
				uIndexToUId.put(uindex,u_id);
				u_IndexToM.put(uindex, rcount);
				uindex++;
			}
			statement.close();
			data.setNoOfPatrons(uIdToUIndex.keySet().size());
			
			selectTableSQL="SELECT u_id,route_id,probability, count(distinct sequence_id) as count " +
					"FROM routeprediction " +
					"where date_time=(select max(date_time) from routeprediction) "+
					"and u_id in (select u.androidId from user_info u) "+
					"group by u_id,route_id;";
			statement =dbConnection.createStatement();
			System.out.println(selectTableSQL);
			
			ArrayList<ArrayList<Double>> routeProbability = new ArrayList<ArrayList<Double>>();
			ArrayList<ArrayList<Integer>> counts = new ArrayList<ArrayList<Integer>>();
			ArrayList<ArrayList<Route>> visitingRoutineNodesSequence= new ArrayList<ArrayList<Route>>();
			for(int k=0;k<data.getNoOfPatrons();k++) {
				routeProbability.add(new ArrayList<Double>());
				counts.add(new ArrayList<Integer>());
				visitingRoutineNodesSequence.add(new ArrayList<Route>());
				for(int m=0;m<u_IndexToM.get(k);m++) {
					routeProbability.get(k).add(0.0);
					counts.get(k).add(0);
					visitingRoutineNodesSequence.get(k).add(null);
				}
			}
			rs = statement.executeQuery(selectTableSQL);
			double[] sumProb=new double[data.getNoOfPatrons()];
			while (rs.next()) {
				String u_id = rs.getString("u_id").trim();
				String route_id = rs.getString("route_id").trim();
				String probability = rs.getString("probability").trim();
				String count = rs.getString("count").trim();
				int uIndex=uIdToUIndex.get(u_id);
//				System.out.println("u_id:"+u_id+" uIndex:"+uIndex+" route:"+route_id+" probability:"+probability+ " nCount:"+count);
				
				routeProbability.get(uIndex).set(Integer.parseInt(route_id), Double.parseDouble(probability));
				counts.get(uIndex).set(Integer.parseInt(route_id), Integer.parseInt(count));
				sumProb[uIndex]=sumProb[uIndex]+Double.parseDouble(probability);
			}
			for(int k=0;k<data.getNoOfPatrons();k++) {
				for(int m=0;m<u_IndexToM.get(k);m++) {
					routeProbability.get(k).set(m, Methods.round(routeProbability.get(k).get(m)/sumProb[k],4));
				}
			}
			
			statement.close();
			data.setRouteProbability(routeProbability);

			//read routes
			selectTableSQL = "SELECT u_id,location_id,sequence_id,route_id " +
					"FROM routeprediction " +
					"where date_time=(select max(date_time) from routeprediction) "+
					"and u_id in (select u.androidId from user_info u) "+
					"order by u_id;";
			statement =dbConnection.createStatement();
			System.out.println(selectTableSQL);					
			for(int k=0;k<data.getNoOfPatrons();k++) {
				for(int m=0;m<u_IndexToM.get(k);m++) {
					visitingRoutineNodesSequence.get(k).set(m, new Route(counts.get(k).get(m)));
				}	
			}
			rs = statement.executeQuery(selectTableSQL);
			while (rs.next()) {
				String u_id = rs.getString("u_id").trim();
				String location_id = rs.getString("location_id").trim();
				String sequence_id = rs.getString("sequence_id").trim();
				String route_id = rs.getString("route_id").trim();
				Route r= visitingRoutineNodesSequence.get(uIdToUIndex.get(u_id)).get(Integer.parseInt(route_id));
				//System.out.println("u:"+u_id+" r:"+route_id+" rsize:"+r.getPath().size()+" seq_id:"+sequence_id+" loc_id:"+location_id);
				//map locationId into nodeIndex and store
				r.getPath().set(Integer.parseInt(sequence_id),
						locationIdToDistIndex.get(Integer.parseInt(location_id)));
			}
			//Remove duplicates
			for(int k=0;k<data.getNoOfPatrons();k++) {
				for(int m=0;m<u_IndexToM.get(k);m++) {
					Route r= visitingRoutineNodesSequence.get(k).get(m);
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
			String selectTableSQL = "SELECT count(date_updated) as count FROM detour " +
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
				String query = "TRUNCATE TABLE detour;";	
				statement.execute(query);
				dbConnection.commit();
				System.out.println("Truncate detour table.");

				query ="insert into detour(u_id, remaining_detour, date_updated) " +
						"SELECT androidId,tolerance,localtimestamp() FROM user where androidId " +
						"in (select distinct r.u_id FROM routeprediction r);";
				statement.execute(query);
				dbConnection.commit();
				System.out.println("Insert detour table.");
			}
			
			//select the detour from the table
			ArrayList<ArrayList<Double>> detourTime=new ArrayList<ArrayList<Double>>();
			for(int k=0;k<data.getNoOfPatrons();k++) {
				detourTime.add(new ArrayList<Double>());
				for(int m=0;m<data.getRouteProbability().get(k).size();m++) {
					detourTime.get(k).add(0.0);
				}	
			}
			selectTableSQL = "SELECT u_id,remaining_detour FROM detour;";
			statement =dbConnection.createStatement();
			System.out.println(selectTableSQL);					
			rs = statement.executeQuery(selectTableSQL);
			while (rs.next()) {
				String u_id = rs.getString("u_id").trim();
				String detour = rs.getString("remaining_detour").trim();
				int uindex=uIdToUIndex.get(u_id);
				for(int m=0;m<data.getRouteProbability().get(uindex).size();m++) {
					detourTime.get(uindex).set(m, Double.parseDouble(detour));
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
