import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
/*
 * Percy Teng 10122592 CISC 365 Lab 1
 * This lab reads a file containing the shift time of given projects
 * and analyzes the shifts to get a list of optimally selected projects
 * according to the purpose of minimum size set of projects
 * In addition, this lab also analyzes the selected projects and 
 * partition them into two parts where each part is worth around half
 * of the total shift length. 
 * */

public class SpaceSpyLab1 {
	public static int STARTVALUE, ENDVALUE, MAXNUM; // Global varibales for recording start value, end value and maximum value;
	public static ArrayList<Integer> selected = new ArrayList<Integer>();//An arraylist of selected projects
	public static ArrayList<Integer> firstHalf = new ArrayList<Integer>();// An arraylist of group 1 selected projects
	public static ArrayList<Integer> secondHalf = new ArrayList<Integer>();// An arraylist of group 2 selected projects
	public static int firstTotal = 0;//An int value to store the sum of the lengths of group 1 projects 
	public static int secondTotal = 0;//An int value to store the sum of the lengtsh of group 2 projects
	//This method takes a string as parameter, read the content of 
	//the input file into an arraylist and call sortArray method to
	//sort the arrylist.
	public static ArrayList<int[]> readIntoArray(String txtName){
		ArrayList<int[]> arr = new ArrayList<int []>();
		FileInputStream fil;
		try {
			fil = new FileInputStream(txtName);
			BufferedReader br = new BufferedReader(new InputStreamReader(fil));
			String line = null;
			line = br.readLine();
			String [] temp = line.split("	");
			STARTVALUE = Integer.parseInt(temp[0]);
			ENDVALUE = Integer.parseInt(temp[1]);
			line = br.readLine();
			MAXNUM = Integer.parseInt(line);
			while ((line = br.readLine()) != null) {
				String[] tmp = line.split("	");
				int [] numbers = new int[3];
				numbers[0] = Integer.parseInt(tmp[0]);
				numbers[1] = Integer.parseInt(tmp[1]);
				numbers[2] = Integer.parseInt(tmp[2]);
				arr.add(numbers);
			}
			br.close(); 
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
            System.err.println("Unable to read the file");
		}
		return sortArrayList(arr);
	}
	//This method takes an arraylist of integer array as parameter and sort it
	//by comparing the second element of each int list
	public static ArrayList<int[]> sortArrayList(ArrayList<int[]> paths){
        Collections.sort(paths, new Comparator<int[]>() {
            private static final int INDEX = 1;
            @Override
            public int compare(int[] o1, int[] o2) {
                return Integer.compare(o1[INDEX], o2[INDEX]);
            }
        });
        //print the result of the sorted list.
        /*for (int[] is : paths) { 
            System.out.println(Arrays.toString(is));
        }*/
        return paths;
	}
	//This is the main algorithm that takes an arraylist of integer lists
	// as parameter and create an optimal set of projects for the problem
	public static Map<Integer,Integer> algorithm(ArrayList<int[]> arr){
		Map<Integer,Integer> map = new TreeMap<Integer,Integer>();
		int start = STARTVALUE;
		int temp = start;// a temporary integer to store the value of start index.
		int index = 0;// an integer representing the current position in the iteration of the given array list.
		while (start < ENDVALUE){
			int bestLength = 0;// an integer representing the length of the selected project
			int bestIndex = 0;// an integer representing the index of projects which is a part of an optimal solution
			int bestShift = 0;// an integer representing a calculated value of the most worthy project.
			ArrayList<int[]> possible = new ArrayList<int[]>();
			// this while loop appends all the possible projects
			// for shift 1 into another arraylist of int lists
			while (index < MAXNUM &&(arr.get(index))[1] <= start){
				if((arr.get(index))[2] > start)
					possible.add(arr.get(index));
				index++;
			}
			//this loop goes throught the possible list and select one
			//best project which will then be stored in a map
			for (int i = 0; i < possible.size(); i++){
				int [] current = possible.get(i);
				int shift = current[2] - temp - (temp-current[1]);
				if (shift > bestShift){
					bestIndex = current[0];
					bestShift = shift;
					start = current[2];
					bestLength = current[2] - current[1];
				}
			}
			map.put(bestIndex, bestLength);
		}
		return map;
	}
	//This method sorts the TreeMap by values reversely
	public static  <Integer, Integer1 extends Comparable<Integer1>> Map<Integer1, Integer1> sortTree(Map<Integer1,Integer1> map){
		Comparator<Integer> valueComparator = 
	             new Comparator<Integer>() {
	      public int compare(Integer k1, Integer k2) {
	        int compare = 
	              map.get(k2).compareTo(map.get(k1));
	        if (compare == 0) 
	          return 1;
	        else 
	          return compare;
	      }
	    };
	 
	    Map<Integer, Integer1> sortedByValues = 
	      new TreeMap<Integer, Integer1>(valueComparator);
	    sortedByValues.putAll((Map<? extends Integer, ? extends Integer1>) map);
	    return (Map<Integer1, Integer1>) sortedByValues;
	}
	// This method partitions a map of integers into two arraylists which
	//only containes the keys of the map representing the index of the selected projects
	public static void partition(Map<Integer,Integer> map){
		int sum = 0;
		int currentSum = 0;
		Set set = map.entrySet();
	    Iterator i = set.iterator();
	    // this loop gets the sum of the values of all the elements in the map
	    while(i.hasNext()) {
	      Map.Entry me = (Map.Entry)i.next();
	      sum += (Integer)me.getValue();
	    }
	    Iterator j = set.iterator();
	    // this loop divides the elements into two arraylist of integers
	    // representing the partitions
	    while (j.hasNext()){
		  Map.Entry me = (Map.Entry)j.next();
		  if (currentSum + (Integer)me.getValue() <= sum/2){
			  firstHalf.add((Integer)me.getKey());
			  currentSum += (Integer)me.getValue();
		  }
		  else
			  secondHalf.add((Integer)me.getKey());
	    }
	    firstTotal = currentSum;
	    secondTotal = sum-currentSum;
	}
	// this method writes out the result into a file called "ProjectLists.txt"
	public static void writeOut(String txtName){
		try {
			String selectedProjects = Integer.toString(selected.get(0))+ "	";
			for (int i = 1; i < selected.size(); i++){
				selectedProjects += selected.get(i) + "	";
			}
			String group1 = Integer.toString(firstHalf.get(0)) + "	";
			for (int j = 1; j < firstHalf.size(); j++){
				group1 += firstHalf.get(j) + "	";
			}
			String group2 = Integer.toString(secondHalf.get(0)) + "	";
			for (int k = 1; k < secondHalf.size(); k++){
				group2 += secondHalf.get(k) + "	";
			}
			String line1 = "Selected Projects:  " + selectedProjects;
			String line2 = "Group 1 Projects: " + group1 + "		" + "Total Time = " + Integer.toString(firstTotal);
			String line3 = "Group 2 Projects: " + group2 + "	" + "Total Time = " + Integer.toString(secondTotal);
			File file = new File(txtName);
			// if file doesnt exists, create it
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(line1);
			bw.newLine();
			bw.newLine();
			bw.write(line2);
			bw.newLine();
			bw.write(line3);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void main(String args[]){
		String inTxtName = "SpaceStation.txt";
		String outTxtName = "ProjectList.txt";
		ArrayList<int[]> arr = readIntoArray(inTxtName);
		Map<Integer,Integer> result = algorithm(arr);
		result = sortTree(result);
		partition(result);
		selected.addAll(firstHalf);
		selected.addAll(secondHalf);
		writeOut(outTxtName);
		/*
		 Set set = result.entrySet();
		    Iterator i = set.iterator();
		    while(i.hasNext()) {
		      Map.Entry me = (Map.Entry)i.next();
		      System.out.print(me.getKey() + ": ");
		      System.out.println(me.getValue());
		    }*/
		System.out.println("Done");
	}
}
