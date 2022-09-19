package listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import dialogue.TradeDialogue;

public class TradeDeniedRecive implements ActionListener {
	
	private TradeDialogue parent;
	
	public TradeDeniedRecive(TradeDialogue dialogue) {
		parent = dialogue;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		parent.setCancled(false);
		parent.setVisible(false);
	}
}
