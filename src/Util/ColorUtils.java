package Util;

import java.awt.Color;

public class ColorUtils {

	
	public static String colorToString(Color color) {
		String name = "";
		if (color.getRed() == 255&& color.getGreen() == 0) {
			name = "Red";
		} else if (color.getBlue() == 255) {
			name = "Blue";
		} else if (color.getGreen() == 200) {
			name = "Orange";
		}
		return name;
	}
}
