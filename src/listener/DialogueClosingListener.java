package listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import dialogue.TradingCardDialogue;

public class DialogueClosingListener implements ActionListener{

	private TradingCardDialogue dialogue;
	
	public DialogueClosingListener(TradingCardDialogue dialogue) {
		this.dialogue = dialogue;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		dialogue.setPick();
		dialogue.setVisible(false);
	}
}
