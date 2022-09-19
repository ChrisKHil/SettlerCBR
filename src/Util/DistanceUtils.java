package Util;

public class DistanceUtils {
	
	public static boolean contains(int x1, int x2, int y1, int y2, int radius) {
		return (Math.sqrt((Math.pow((x1- x2), 2) + Math.pow((y1 - y2),2))) < radius);
	}
}
