package dialogue;

import javax.swing.JDialog;

import frames.MainFrame;

public abstract class TradingCardDialogue extends JDialog {
	TradingCardDialogue(MainFrame frame, boolean modal) {
		super(frame, modal);
	}
	/**
	 * For the Button to call, will be different for each nessesary dialogue
	 * Will set a attribute in the concrete Class that can be called from another method
	 */
	public abstract void setPick();
}
