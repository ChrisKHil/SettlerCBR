package listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import frames.ControlFrame;

public class TurnChangeListener implements ActionListener {
	
	private ControlFrame frame;
	
	public TurnChangeListener(ControlFrame frame) {
		this.frame = frame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		frame.getMainFrame().advanceStage();
	}
}
