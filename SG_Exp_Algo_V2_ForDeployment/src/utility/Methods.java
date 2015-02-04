package utility;

import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;



public class Methods {
	public static double vectorMutilply(double[] v1, double[]v2) {
		double value=v1[0]*v2[0]+v1[0]*v2[0];
		return value;
	}
	
	
	public static int randomPickIdFromProb(int[] ids, double[] probs) {
		int index=-1;
		double rd=random_numberDouble(0.0,1,4);
		double lower=0;
		double upper=probs[0];
		out:
		for(int i=0;i<probs.length;i++) {
			if(rd>=lower && rd<upper) {
				index=i;
				break out;
			} 
			if(i==probs.length-2) {
				lower=lower+probs[i];
				upper=upper+probs[i+1];
			}
		}
		return (index>-1)? ids[index]:-1;
	}
	
	public static double random_numberDouble(double kmin, double kmax, int digit){
		double j;
		Random r= new Random();
		int i= r.nextInt((int)Math.pow(10, digit)+1);
		double v =i/Math.pow(10, digit);
		j=(kmax-kmin)*v+ kmin;
		return j;
	}

	public static double round( double val) {
		if( val < 0 ) return Math.ceil(val - 0.5);
		return Math.floor(val + 0.5);
	}

	public static int random_number(int kmin, int kmax) {
		int j;	
		Random r= new Random();
		int i= r.nextInt(kmax-kmin+1);
		j=kmin+i; 
		return j ;
	}
	
	public static void createAndWritefile(String filename, String line) throws IOException {
		System.out.println("Write to: "+filename);
		File file = new File(filename);
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(line);
		bw.close();
		System.out.println("Write successful!");
	}

	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
}
