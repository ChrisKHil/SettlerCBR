package pieces;

import jade.util.leap.Serializable;
import player.Player;

public class Piece implements Serializable{

	private Player player;

	public Piece(Player player) {
		this.player = player;
	}
	
	public Player getPlayer() {
		return player;
	}
}
