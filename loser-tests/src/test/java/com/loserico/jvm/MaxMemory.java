package com.loserico.jvm;

public class MaxMemory {
	
	public static void main(String[] args) {
		Runtime rt = Runtime.getRuntime();
		long totalMem = rt.totalMemory();
		long maxMem = rt.maxMemory();
		long freeMem = rt.freeMemory();
		double megs = 1048576.0;

		System.out.println("Total Memory: " + totalMem + " (" + (totalMem / megs) + " MiB)");
		System.out.println("Max Memory:   " + maxMem + " (" + (maxMem / megs) + " MiB)");
		System.out.println("Free Memory:  " + freeMem + " (" + (freeMem / megs) + " MiB)");
	}
}