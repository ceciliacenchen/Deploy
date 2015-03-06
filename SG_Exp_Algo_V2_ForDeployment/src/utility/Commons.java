/**
 * 
 */
package utility;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * @author chencen
 *
 */
public class Commons {
	
	/**
	 * @param integer
	 * @param taskNodes
	 * @return
	 */
	public static boolean isPresentInArrayList(Integer a,
			ArrayList<Integer> arr) {
		 if(arr == null) {
			 return false;
		 }
		 for(int i = 0;i < arr.size();i++) {
			 if(arr.get(i) == a){
				 return true;
			 }
		 }
		 return false;
	}

	 public static boolean isPresentInArray(int a, int[] arr) {
		 if(arr == null) {
			 return false;
		 }
		 for(int i = 0;i < arr.length;i++) {
			 if(arr[i] == a){
				 return true;
			 }
		 }
		 return false;
	 }
	 
	 public static int indexOfElement(int a, int[] arr) {
		 if(arr == null) {
			 return 0;
		 }
		 for(int i = 0;i < arr.length;i++)  {
			 if(arr[i] == a) return i;
		 }
		 return -1;
	 }
	 
//	 public static int[] convertArrayListToArray(ArrayList<Integer> list) {
//	     int[] array = new int[list.size()];
//	     for(int i=0;i<list.size();i++) {
//	    	 array[i]=list.get(i);
//	     }
//		 return array;
//	 }

	public static double[] convertArrayListToArray(ArrayList<Double> list) {
		double[] array = new double[list.size()];
	     for(int i=0;i<list.size();i++) {
	    	 array[i]=list.get(i);
	     }
		 return array;
	}

}
