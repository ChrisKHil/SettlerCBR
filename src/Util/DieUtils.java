package Util;

import java.util.Random;

public class DieUtils {
	
	public static int rollTowDie() {
		int die = 0;
		Random rand  = new Random();
		//shoul return between 1 and 6, for 6 is exclueded, thus being 0 to 5.  
		die += 1 + rand.nextInt(6);
		die += 1 + rand.nextInt(6);
		return die;
	}

}
