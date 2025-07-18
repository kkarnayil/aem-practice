package com.aempactice.core.models;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SampleClass {
	public static void main(String[] args) {
		int[] arr = { 6, 3, 2, 1, 4, 5 };

		for (int i = 0; i < arr.length - 1; i++) {
			for (int j = 0; j < arr.length - i - 1; j++) {
				if (arr[j] > arr[j+1]) {
					int temp = arr[j];
					arr[j] = arr[j+1];
					arr[j+1] = temp;
				}
			}
		}
		for (int i = 0; i < arr.length; i++) {
			System.out.println(arr[i]);
		}
		
		List<String> numbers =  new ArrayList<>();
		numbers.add("1");
		numbers.add("44");
		numbers.add("6");
		numbers.add("31");
		numbers.add("50");
		
		//To filter list with only even numbers
		//using for loop
		List<String> filteredList = new ArrayList<>();
		for(int i=0;i<numbers.size();i++) {
			if(Integer.valueOf(numbers.get(i))%2==0) {
				filteredList.add(numbers.get(i));
			}
		}
		System.out.println(filteredList);
		
		//using foreach
		for(String num : numbers) {
			if(Integer.valueOf(num)%2==0) {
				filteredList.add(num);
			}
		}
		System.out.println(filteredList);
		
		//using stream
		filteredList = numbers.stream().filter(num -> Integer.valueOf(num)%2==0).collect(Collectors.toList());
		System.out.println(filteredList);
	}
}
