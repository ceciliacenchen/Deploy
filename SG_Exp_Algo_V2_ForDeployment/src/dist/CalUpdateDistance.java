package dist;


import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.collections15.Transformer;

import smart.data.objects.E;
import smart.data.objects.V;
import utility.LoadProperties;

import com.mysql.jdbc.Connection;

import dbHandler.ConnectDB;
import dbHandler.TruncateDB;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.Graph;

public class CalUpdateDistance {
	
	public static void main(String[] args) throws SQLException {
		long startTime = System.currentTimeMillis();
		LoadProperties.load();
		SG_Instance_InputHandler i=new SG_Instance_InputHandler();
		i.readInstance();
		System.out.println("No. of nodes: "+i.g.getVertexCount());
		System.out.println("No. of edges: "+i.g.getEdgeCount());
		
		batchInsertLocationsIntoTable(ConnectDB.connect(LoadProperties.properties),i.g.getVertices());

		Transformer<E, Double> wtTransformer = new Transformer<E,Double>() {
			public Double transform(E link) {
				return link.dis;
			}
		};
		DijkstraShortestPath<V,E> alg = new DijkstraShortestPath(i.g, wtTransformer);

		double[][] dis=new double[i.g.getVertexCount()][i.g.getVertexCount()];
		int[] nodelist=new int[i.g.getVertexCount()];
		HashSet<Integer> locations= new HashSet<Integer>();
		int index1=0;
		for(V v: i.g.getVertices()) {
			locations.add(v.id);
			nodelist[index1]=v.id;		
			int index2=0;
//			System.out.print("index-"+index1+"|");
			for(V w: i.g.getVertices()) {
				List<E> l = alg.getPath(v, w);
				Number dist = alg.getDistance(v, w);
				dis[index1][index2]=(Double) dist==null?9999:(Double)dist*1.0/60.0;
//				System.out.print(v.id + " to " + w.id + " | dis:"+dist);
				index2++;
			}
//			System.out.println();
			index1++;
		}
		System.out.println("Dist calculation done.");
		System.out.println(locations.size());

		//update the distance into databse
		TruncateDB.truncate(ConnectDB.connect(LoadProperties.properties), "distance");
		batchInsertDistIntoTable(ConnectDB.connect(LoadProperties.properties), dis,nodelist);
		
		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime;
		System.out.println("Graph Build and Distance Matrix Calculation takes:" + duration/1000.0 + " s");
	}

	public static void print(List<E> l) {
		for(E e:l) {
			System.out.print("["+e.getV1().id+" "+e.getV2().id+"]");
		}
		System.out.println();
	}
	
	public static void batchInsertLocationsIntoTable(Connection dbConnection,
			Collection<V> locations) {
		Statement statement = null;
		try {
			statement = dbConnection.createStatement();
			dbConnection.setAutoCommit(false);
			for(V v: locations) {
				int id=v.getId();
				String insertTableSQL1 = "INSERT INTO locationmapping"
						+ "(location_id) VALUES("
						+ id+");";
				statement.addBatch(insertTableSQL1);	
			}
			statement.executeBatch();
			dbConnection.commit();
			System.out.println("Records are inserted into LOCATION_MAPPING table.");
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

	public static void batchInsertDistIntoTable(Connection dbConnection,double[][] dis,int[] nodelist)  { 
		Statement statement = null;
		try {
			statement = dbConnection.createStatement();
			for(int i=0;i<nodelist.length;i++) {
	     		String insertTableSQL1="INSERT INTO DISTANCE"
						+ "(NODE1, NODE2, DIST) VALUES ";
				for(int j=0;j<nodelist.length;j++) {
					if(j==nodelist.length-1) {
						int x=nodelist[i];
						int y=nodelist[j];
						double dist=dis[i][j];
						insertTableSQL1 =insertTableSQL1 + "("+ x+","+y+","+dist+");";
					} else {
						int x=nodelist[i];
						int y=nodelist[j];
						double dist=dis[i][j];
						insertTableSQL1 =insertTableSQL1 + "("+ x+","+y+","+dist+"), ";
					}	
				}
//				System.out.println(insertTableSQL1);
				System.out.println("inserting..."+i);
				statement.execute(insertTableSQL1);
			}
			System.out.println("Records are inserted into DISTANCE table.");
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
