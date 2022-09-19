package dialogue;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import enums.LandType;
import frames.MainFrame;
import listener.DialogueClosingListener;

public class OneFromAllDialogue extends TradingCardDialogue {

	private JPanel mainPanel;
	
	private JComboBox<LandType> first;
	
	private final LandType[] TYPES = {LandType.CLAY, LandType.CORN, LandType.LUMBER, LandType.STONE, LandType.WHOOL};
	
	private JButton confirmButton;
	
	private LandType pick;
	
	public OneFromAllDialogue(MainFrame frame, boolean modal) {
		super(frame, modal);
		init();
		
		this.pack();
	}
	
	private void init() {
		mainPanel = new JPanel();
		mainPanel.setLayout(new FlowLayout());
		this.add(mainPanel);
		
		first = new JComboBox<LandType>(TYPES);
		mainPanel.add(first);
		
		confirmButton = new JButton("Confirm");
		confirmButton.addActionListener(new DialogueClosingListener(this));
		mainPanel.add(confirmButton);
	}

	@Override
	public void setPick() {
		pick = (LandType)first.getSelectedItem();
	}
	
	public LandType getPick() {
		return pick;
	}
}
