package listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import dialogue.TradeDialogue;

public class TradeAcceptRecive implements ActionListener {

	private TradeDialogue dialogue;
	
	public TradeAcceptRecive(TradeDialogue dialogue) {
		this.dialogue = dialogue;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		dialogue.setAccepted(true);
		dialogue.setVisible(false);
	}

}
