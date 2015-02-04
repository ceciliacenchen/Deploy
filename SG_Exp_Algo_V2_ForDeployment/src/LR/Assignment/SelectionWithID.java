package LR.Assignment;

public class SelectionWithID {
	private static final int TOP_K = 3;

	public static void main(String args[]) {
		double[] array = new double[] { 9, 7, 8, 2, 1, 3, 5, 6, 4 };
		int[] ids = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		
		if (array.length == 0) {
			System.out.println("Empty Array");
			System.exit(0);
		} else if (array.length == 1) {
			System.out.println("Only one element, Array already sorted");
		} else {
			selectBiggest(array, 0, array.length - 1, array.length-TOP_K, ids);
		}
		for (int i = array.length - TOP_K; i <= array.length - 1; i++) {
			System.out.println("id:"+ids[i]+" -v"+array[i]);
		}
	}

	public static int partitionSmallest(double[] input, int p, int q, int[] ids) {
		double x = input[p];
		int i = p;
		for (int j = (p + 1); j <= q; j++) {
			// select with smalllest values
			if (input[j] >= x) {
				i = i + 1;
				if (i < j) {
					double temp = input[j];
					input[j] = input[i];
					input[i] = temp;
					
					//sort ids
					int temp2 = ids[j];
					ids[j] = ids[i];
					ids[i] = temp2;
				}
			}
		}
		double temp1 = input[p];
		input[p] = input[i];
		input[i] = temp1;
		
		int temp3 = ids[p];
		ids[p] = ids[i];
		ids[i] = temp3;
		return i;
	}

	public static void selectSmallest(double[] list, int left, int right, int k, int[] ids) {
		int pivotIndex = partitionSmallest(list, left, right,ids);
		if (pivotIndex == k) {
			return;
		} else if (k < pivotIndex) {
			selectSmallest(list, left, pivotIndex - 1, k,ids);
		} else {
			selectSmallest(list, pivotIndex + 1, right, k,ids);
		}
	}
	
	public static int partitionBiggest(double[] input, int p, int q, int[] ids) {
		double x = input[p];
		int i = p;
		for (int j = (p + 1); j <= q; j++) {
			 //select with biggest values
			 if (input[j] <= x) {
				i = i + 1;
				if (i < j) {
					double temp = input[j];
					input[j] = input[i];
					input[i] = temp;
					
					int temp2 = ids[j];
					ids[j] = ids[i];
					ids[i] = temp2;
				}
			}
		}
		double temp1 = input[p];
		input[p] = input[i];
		input[i] = temp1;
		
		int temp3 = ids[p];
		ids[p] = ids[i];
		ids[i] = temp3;
		return i;
	}

	public static void selectBiggest(double[] list, int left, int right, int k, int[] ids) {
		int pivotIndex = partitionBiggest(list, left, right,ids);
		if (pivotIndex == k) {
			return;
		} else if (k < pivotIndex) {
			selectBiggest(list, left, pivotIndex - 1, k,ids);
		} else {
			selectBiggest(list, pivotIndex + 1, right, k,ids);
		}
	}
}