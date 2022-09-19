package Util;

public class ResourceProbabilityPair {
	private int[] probabilities;
	
	private String[] resources;
	
	private int counter = 0;
	
	public ResourceProbabilityPair() {
		probabilities = new int[3];
		resources = new String[3];
	}
	
	public void add(int probability, String resource) {
		probabilities[counter] = probability;
		resources[counter] = resource;
		counter++;
	}
	public String[] getResources() {
		return resources;
	}
	
	public int[] getProbabilities() {
		return probabilities;
	}
}
