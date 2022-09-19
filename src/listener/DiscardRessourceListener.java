package listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import dialogue.DiscardCardsToThievesDialogue;

public class DiscardRessourceListener implements ActionListener {

	private JComboBox<Integer> combobox;
	
	private DiscardCardsToThievesDialogue parent;
	
	public DiscardRessourceListener(DiscardCardsToThievesDialogue parent, JComboBox<Integer> combobox) {
		this.combobox = combobox;
		this.parent = parent;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("			>Actionlistener is fiering");
		parent.fitRest(combobox);
	}
}
