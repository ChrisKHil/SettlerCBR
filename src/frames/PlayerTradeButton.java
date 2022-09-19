package frames;

import javax.swing.JButton;

import Util.ColorUtils;
import player.Player;

public class PlayerTradeButton extends JButton {

	private Player player;
	
	public PlayerTradeButton(Player player) {
		this.player = player;
		this.setText(ColorUtils.colorToString(player.getColor()));
	}
	
	public Player getPlayer() {
		return player;
	}
	
}
