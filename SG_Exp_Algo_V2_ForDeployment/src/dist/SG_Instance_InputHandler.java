package dist;

import dbHandler.ConnectDB;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import org.apache.commons.collections15.Factory;

import smart.data.objects.E;
import smart.data.objects.V;
import utility.LoadProperties;

public class SG_Instance_InputHandler {

	public static Graph<V, E> g;    
	private static final String CURRENT_INSTANCE_DIR = System.getProperty("user.dir");

	/**
	 * Create a new InputHandler object.
	 */
	public SG_Instance_InputHandler() {
		super();
	}

	public static void readInstance() throws SQLException {	
		Statement statement = null;
		Connection dbConnection=ConnectDB.connect(LoadProperties.properties);
		
		HashSet<Integer> nodes= new HashSet<Integer>();
		ArrayList<int[]> edges= new ArrayList<int[]>();
		ArrayList<Double> distance= new ArrayList<Double>();
		
		
		String selectTableSQL = "SELECT node1,node2,weight FROM thivyak.selected_edges;";
		statement =dbConnection.createStatement();
		System.out.println(selectTableSQL);
		ResultSet rs = statement.executeQuery(selectTableSQL);
		while (rs.next()) {
			int[] v= new int[3];
			v[0]=Integer.parseInt(rs.getString("node1").trim());
			v[1]=Integer.parseInt(rs.getString("node2").trim());
			v[2]=(int) Math.ceil(Double.parseDouble(rs.getString("weight").trim()));
			int x = Integer.parseInt(rs.getString("node1").trim());
			int y = Integer.parseInt(rs.getString("node2").trim());
			double dis= (int) Math.ceil(Double.parseDouble(rs.getString("weight").trim()));
			distance.add(dis);
			nodes.add(x); nodes.add(y);
			int[] e= {x,y}; 
			edges.add(e);
		}	
		
		System.out.println(edges.size());
		g = new UndirectedSparseGraph<V, E>();
		for(int n: nodes) {
			V v = new V(n);
			g.addVertex(v);
		}
		Factory<E> edgeFactory = new Factory<E>() {
			int n = 0;
			public E create() {
				return new E(n++);
			}
		};
		for(int r=0; r<edges.size(); r++) {
			E de = edgeFactory.create();
			V v= getVertex(edges.get(r)[0], g);
			V v2=getVertex(edges.get(r)[1], g);
			de.setDis(distance.get(r));
			de.setV1(v);
			de.setV2(v2);
			g.addEdge(de, v, v2);
			if(v.getId()==11 && v2.getId()==11) {
				System.out.println(v.id+" "+v2.id+" "+de.getDis());
			}
			//System.out.println(v.id+" "+v2.id+" "+de.getDis());
		}
		statement.close();
		dbConnection.close();
	}

	public static V getVertex(int id, Graph<V, E> g) {
		for (V v : g.getVertices()) {
			if (v.id == id) {
				return v;
			}
		}
		return null;
	}
}
