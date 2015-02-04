package utility;

import edu.uci.ics.jung.graph.Graph;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Vector;


public class CollectionHandler {
	public static HashSet<ArrayList<Integer>> deepCopy(HashSet<ArrayList<Integer>> from) {
		HashSet<ArrayList<Integer>> copy = new HashSet<ArrayList<Integer>> ();
		for(ArrayList<Integer> r: from) {
			ArrayList<Integer> a=deepCopyArrayList(r);
			copy.add(a);
		}
		return copy;
	}

    /**
     * Create a new CollectionHandler.java object.
     */
    public CollectionHandler() {
        // TODO Auto-generated constructor stub
    }

    public static double[] copyDoubleVectorIntoArray(Vector scores) {
        double[] a = new double[scores.size()];
        for (int i = 0; i < scores.size(); i++) {
            a[i] = ((Double) scores.elementAt(i)).doubleValue();
        }
        return a;
    }

    public static Vector intersect(Vector v1, Vector v2) {
        Object obj;
        Vector v = new Vector();

        for (int i = 0; i < v1.size(); i++) {
            obj = v1.elementAt(i);
            if (v2.contains(obj)) {
                v.add(obj);
            }

        }

        return v;
    }

    public static Integer[] rank(final double[] data) {
        int len = data.length;
        final Integer[] order = new Integer[len];

        for (int i = 0; i < len; i++) {
            order[i] = new Integer(i);
        }

        Arrays.sort(order, new Comparator<Integer>() {
            @Override
            public int compare(final Integer o1, final Integer o2) {
                return Double.compare(data[o1], data[o2]);
            }
        });

        return order;
    }

    public static Integer[] rank(final int[] data) {
        int len = data.length;
        final Integer[] order = new Integer[len];

        for (int i = 0; i < len; i++) {
            order[i] = new Integer(i);
        }

        Arrays.sort(order, new Comparator<Integer>() {
            @Override
            public int compare(final Integer o1, final Integer o2) {
                return Double.compare(data[o1], data[o2]);
            }
        });

        return order;
    }

    public static void printArrays(int[] a) {
        for (int i = 0; i < a.length; i++) {
            System.out.print(a[i] + " ,");
        }
        System.out.println(" ");
    }
    public static void printArrays(double[] a) {
        for (int i = 0; i < a.length; i++) {
            System.out.print(a[i] + " ,");
        }
        System.out.println(" ");
    }

    public static HashSet<Integer> deepCopyHashSet(HashSet<Integer> temp) {
        HashSet<Integer> copy = new HashSet<Integer>();
        for (int i : temp) {
            copy.add(i);
        }
        return copy;
    }
    
    public static int[] deepCopyArray(int[] temp) {
    	int[] copy = new int[temp.length];
        for (int i = 0; i < temp.length; i++) {
        	int x=temp[i];
            copy[i]=x;
        }
        return copy;
    }
    
    public static double[] deepCopyArray(double[] temp) {
    	double[] copy = new double[temp.length];
        for (int i = 0; i < temp.length; i++) {
        	double x=temp[i];
            copy[i]=x;
        }
        return copy;
    }

    public static ArrayList<Integer> deepCopyArrayList(ArrayList<Integer> orig) {
        int size = orig.size();
        ArrayList<Integer> copy = new ArrayList<Integer>(size);
        for (int i = 0; i < size; i++) {
            copy.add(orig.get(i));
        }
        return copy;
    }
    
//    public static ArrayList<Integer> deepCopyArrayList2(ArrayList<Integer> orig) {
//        int size = orig.size();
//        ArrayList<Integer> copy = new ArrayList<Integer>(size);
//        for (int i = 0; i < size; i++) {
//            copy.add(new Integer(orig.get(i)));
//        }
//        return copy;
//    }
    
    public static int findIndexOfArray(int[] tasks, int ChosenT) {
        int index=-1;
        for(int i=0;i<tasks.length;i++) {
            if(ChosenT==tasks[i]) {
                index=i;
            }
        }
        return index;
    }
    
    public static ArrayList<Integer> deepCopyArrayToArrayList(int[] temp) {
    	ArrayList<Integer>  copy = new ArrayList<Integer>();
        for (int i = 0; i < temp.length; i++) {
            copy.add(temp[i]);
        }
        return copy;
    }

}
