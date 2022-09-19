package listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import dialogue.TradeDialogue;

public class TradeCancleListener implements ActionListener{

	private TradeDialogue parent;
	
	public TradeCancleListener(TradeDialogue dialogue) {
		parent = dialogue;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		parent.setCancled(true);
		parent.setVisible(false);
	}

}
