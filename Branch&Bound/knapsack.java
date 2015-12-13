package cisc365lab2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
/*Percy Teng 10122592 13spt1*/
//main class of the program
public class knapsack {
	static int K = 0;
	static int number = 2;
	static int size = 0;
	static MinHeap minheap = null;
	static int globalU;
	static int value;
	//This method reads the content of input file into an arraylist of items.
	public static ArrayList<item> readIntoArray(String txtName){
		ArrayList<item> arr = new ArrayList<item>();
		FileInputStream fil;
		try {
			fil = new FileInputStream(txtName);
			BufferedReader br = new BufferedReader(new InputStreamReader(fil));
			String line = null;
			line = br.readLine();
			line = br.readLine();
			String[] firstLine = line.split(" 	");
			K = Integer.parseInt(firstLine[0]);
			minheap = new MinHeap();
			size = Integer.parseInt(firstLine[1]);
			while ((line = br.readLine()) != null) {
				String[] tmp = line.split(" 	");
				item ele = new item(Integer.parseInt(tmp[1]), Integer.parseInt(tmp[2]), Integer.parseInt(tmp[0]));
				arr.add(ele);
			}
			br.close(); 
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
            System.err.println("Unable to read the file");
		}
		
		return sortArrayList(arr);
	}
	//This method sorts the arraylist
	public static ArrayList<item> sortArrayList(ArrayList<item> paths){
        Collections.sort(paths, new Comparator<item>() {
            private static final int INDEX = 1;
            @Override
            public int compare(item o1, item o2) {
                return Double.compare(o2.ratio, o1.ratio);
            }
        });
        return paths;
	}
	//This method computes the value of CSF.
	public static int computeCSF(String instance, ArrayList<item> arr){
		int CSF = 0;
		int length = instance.length();
		for (int i = 0; i < length; i++){
			if (instance.charAt(i) == '0'){
				CSF += arr.get(i).value;
			}
		}
		return CSF;
	}
	//This method computes the value of GFC = CSF + the sum of items that are too big to fit + other guaranteed future costs as 
	//described in class.
	public static int computeGFC1(String instance, ArrayList<item>arr){
		int count = 0;
		int length = instance.length();
		int GFC = 0;
		for (int i = 0; i < length; i++){
			if (instance.charAt(i) == '1'){
				count += arr.get(i).mass;
			}
		}
		int left = K-count;
		int counting = 0;
		int minMass = Integer.MAX_VALUE;
		int minValue = 0;
		for (int j = 0; j < arr.size(); j++){
			if (instance.length() < (j+1)){
				if (arr.get(j).mass > left)
					GFC+= arr.get(j).value;
				else{
					if (counting <= left){
						if (arr.get(j).mass <= minMass){
							minMass = arr.get(j).mass;
							minValue = arr.get(j).value;
							counting += arr.get(j).mass;
							if (j == arr.size()-1 && counting > left)
								GFC += minValue;
						}
						else
							counting += arr.get(j).mass;
					}
					else{
						GFC += minValue;
						counting = arr.get(j).mass;
						minMass = arr.get(j).mass;
						minValue = arr.get(j).value;
					}
				}
			}
		}
		return GFC;
	} 
	//	//This method computes the value of GFC by getting the sum of items that are too big to fit and CSF
	public static int computeGFC(String instance, ArrayList<item>arr){
		int count = 0;
		int length = instance.length();
		for (int i = 0; i < length; i++){
			if (instance.charAt(i) == '1'){
				count += arr.get(i).mass;
			}
		}
		int left = K-count;
		int GFC = 0;
		for (int j = length; j < arr.size(); j++){
			if (arr.get(j).mass > left)
				GFC += arr.get(j).value;
		}
		return GFC;
	}
	//This method computes the value of FFC with greedy heuristic
	public static int computeFFC1(String instance, ArrayList<item>arr){
		int count = 0;
		int length = instance.length();
		for (int i = 0; i < length; i++){
			if (instance.charAt(i) == '1'){
				count += arr.get(i).mass;
			}
		}
		int left = K-count;
		int accumulate = 0;
		int FFC = 0;
		for (int j = arr.size()-1; j >= length; j--){
			if ((accumulate + arr.get(j).mass) <= left){
				accumulate += arr.get(j).mass;
			}
			else{
				FFC += arr.get(j).value;
			}
		}
		return FFC;
	}
	// this method computes the value of FFC by adding elements reversely.
	public static int computeFFC(String instance, ArrayList<item>arr){
		int count = 0;
		int length = instance.length();
		for (int i = 0; i < length; i++){
			if (instance.charAt(i) == '1'){
				count += arr.get(i).mass;
			}
		}
		int left = K-count;
		int accumulate = 0;
		int FFC = 0;
		for (int j = length; j < arr.size(); j++){
			if ((accumulate + arr.get(j).mass) <= left){
				accumulate += arr.get(j).mass;
			}
			else{
				FFC += arr.get(j).value;
			}
		}
		return FFC;
	}
	//This method computes the value of GloabalU
	public static int computeGlobalU(ArrayList<item>arr){
		int count = 0;
		int index = 0;
		int bound = 0;
		for (int i = 0; i < arr.size(); i++){
			if ((arr.get(i).mass + count) <= K){
				count += arr.get(i).mass;
				index++;
			}
			else
				bound += arr.get(i).value;
		}
		return bound;
	}
	//This method compute the value and lower bound and upper bound and create a partial solution
	public static partialSolution compute(String instance, ArrayList<item> arr){
//		for (int i = 0; i < arr.size(); i++){
//			System.out.println(arr.get(i).mass);
//		}
		int CSF,FFC,GFC,GlobalUpperB, LowerB, UpperB;
		CSF = computeCSF(instance, arr);
		FFC = computeFFC(instance, arr);
		GFC = computeGFC1(instance, arr);
		GlobalUpperB = computeGlobalU(arr);
		LowerB = CSF + GFC;
		UpperB = CSF + FFC;
		partialSolution PS = new partialSolution (instance, LowerB, UpperB);
		return PS;
//		System.out.println(CSF);
//		System.out.println(GFC);
//		System.out.println(FFC);
//		System.out.println(UpperB);
	}
	//this method check if the partial solution has less total mass than the limit
	public static boolean checkIfValid(String instance, ArrayList<item> items){
		int length = instance.length();
		int count = 0;
		for (int i = 0; i < length; i++){
			if (instance.charAt(i) == '1')
				count += items.get(i).mass;
		}
		if (count > K)
			return false;
		else
			return true;
	}
	//this is the main algorithm to get the optimal solution
	public static partialSolution algorithm(ArrayList<item> items){
		partialSolution current = minheap.Heap[1];
		while (true){
			current = minheap.Heap[1];
			if (current.instance.length() == size){
				break;
			}
			else{
				partialSolution top = minheap.remove();
				partialSolution firstSolution = compute(top.instance + "1", items);
				partialSolution secondSolution = compute(top.instance + "0", items);
				if (checkIfValid(firstSolution.instance, items)){
					if (firstSolution.lowerBound <= globalU){
						number++;
						minheap.insert(firstSolution);
					}
					if (firstSolution.upperBound < globalU){
						globalU = firstSolution.upperBound;
					}
				}
				if (checkIfValid(secondSolution.instance,items)){
					if (secondSolution.lowerBound <= globalU){
						number++;
						minheap.insert(secondSolution);
					}
					if (secondSolution.upperBound < globalU)
						globalU = secondSolution.upperBound;
				}
			}
		}
		return current;
	}
	//this method transfer the result in the form of sorted item list into an arraylist with the index of selected items in a sorted order
	public static ArrayList<Integer> transfer(String instance, ArrayList<item> arr){
		ArrayList<Integer> lis = new ArrayList<Integer>();
		for (int i = 0; i < arr.size(); i++){
			if (instance.charAt(i) == '1'){
				lis.add(arr.get(i).index);
				value+= arr.get(i).value;
			}
		}
		Collections.sort(lis);
		return lis;
	}
	//this method displays the arrayList in a list form.
	public static String displayList(ArrayList<Integer> lis){
		String output = " ";
		for (int i = 0; i < lis.size(); i++){
			output += lis.get(i) + ", ";
		}
		return output;
	}
	//this method writes the output to a file called output.txt
	public static void writeOut(String txtName, String instance, ArrayList<Integer> lis, String readingTxt){
		try {
			File file = new File(txtName);
			// if file doesnt exists, create it
			if (!file.exists()) {
				file.createNewFile();
			}
			String string5 = "The test case is " + readingTxt + ".";
			String string1 = "The instance is + " + instance + ".";
			String string2 = "The value of the solution is " + Integer.toString(value) +".";
			String string3 = "The list of items are: " + displayList(lis) + ".";
			String string4 = "The number of partial solutions I generated is " + Integer.toString(number) + ".";
			FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.newLine();
			bw.write(string5);
			bw.newLine();
			bw.newLine();
			bw.write(string1);
			bw.newLine();
			bw.newLine();
			bw.write(string2);
			bw.newLine();
			bw.newLine();
			bw.write(string3);
			bw.newLine();
			bw.newLine();
			bw.write(string4);
			bw.newLine();
			bw.newLine();
			bw.write("------------------------------------------------------------------------------------");
			bw.newLine();
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void main(String args[]){
		for (int i = 20; i < 51; i++){
			String txtname = "B+B 2015 Data 20151124 1226  Size " + i + ".txt";
			ArrayList<item> arr = readIntoArray(txtname);
			globalU = computeGlobalU(arr);
			partialSolution ps1 = compute("0", arr);
			partialSolution ps2 = compute("1", arr);
			minheap.insert(ps1);
			minheap.insert(ps2);
			partialSolution solution = algorithm(arr);
			ArrayList<Integer> lis = transfer(solution.instance, arr);
			writeOut("output.txt", solution.instance, lis, txtname);
		}
	}
}
