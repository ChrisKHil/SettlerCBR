package listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import frames.ControlFrame;

public class RollDiceListener implements ActionListener {

	private ControlFrame frame;
	
	public RollDiceListener(ControlFrame frame) {
		this.frame = frame;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		switch (frame.getMainFrame().getStage()) {
			case ROLL_DICE:
				frame.rollDice();
				
				//Pass number to board to distribute ressources
				//frame.changeTurnEndButtonEnable();
				break;
			case TRADE:
				break;
			default:
				break;
		}
	}

}
