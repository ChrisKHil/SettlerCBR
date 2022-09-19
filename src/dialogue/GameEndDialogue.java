package dialogue;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import frames.MainFrame;

public class GameEndDialogue extends JDialog {
	
	private MainFrame frame;
	
	private JLabel label;
	
	private JButton endButton;
	
	private JPanel mainPanel;
	
	public GameEndDialogue(MainFrame frame) {
		this.frame = frame;
		init();
		
		this.pack();
	}
	
	private void init() {
		mainPanel = new JPanel();
		mainPanel.setLayout(new FlowLayout());
		
		label = new JLabel(("Player: " + frame.getActivePlayer().getColor().toString()) + " won.");
		mainPanel.add(label); 
		
		endButton = new JButton();
		endButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
			}
		});
		mainPanel.add(endButton);
	}
}
