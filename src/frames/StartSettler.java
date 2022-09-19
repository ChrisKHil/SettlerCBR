package frames;

import java.util.List;

import player.Player;

public class StartSettler {
	
	private static Player activePlayer;
	
	private static List<Player> player;
	
	public static void main(String[] args) {
		
		boolean gameRunning = true;
		//If we allow for more players, this will be used to add them 
//		int playerCount = 2;
//		player = new ArrayList<Player>();
		//Munally doing it for two as this should be plenty for testing. SHOULD ALSO BE DEPLETED
//		player.add(new Player(Color.RED));
//		player.add(new Player(Color.BLUE));
		// ALL THE GAME STARTING STUFF IS HANDELD BY THE FRAME (Retrospective not pretty but working i guess)
		MainFrame frame = new MainFrame();
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			//here we handle ending the whole JADE and MyCBR Stuff as to not leave it runing after closing the window
			//Also seems to be working without explicit shutdown
			public void run () {
			}
		});
		
//		determineFirstPlayer();
//		doFirstTurn();
	}
	//PROBS DEPRECATED
	private static void determineFirstPlayer() {
		int rot = Util.DieUtils.rollTowDie();
		int blue = Util.DieUtils.rollTowDie();
		while (rot == blue) {
			rot = Util.DieUtils.rollTowDie();
			blue = Util.DieUtils.rollTowDie();
		}
		if (rot < blue) {
			activePlayer = player.get(1);
		} else if (blue > rot) {
			activePlayer = player.get(0);
		}
	}
	
	private static void doFirstTurn() {
		
	}
}
