package dialogue;

import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import enums.LandType;
import frames.MainFrame;
import listener.DialogueClosingListener;

public class TwoRessourceChoice extends TradingCardDialogue{

	private JPanel mainPanel;
	
	private JComboBox<LandType> first;
	
	private JComboBox<LandType> second;
	
	private final LandType[] TYPES = {LandType.CLAY, LandType.CORN, LandType.LUMBER, LandType.STONE, LandType.WHOOL};
	
	private JButton confirmButton;
	
	private List<LandType> pickedTypes;
	
	public TwoRessourceChoice(MainFrame frame, boolean modal) {
		super(frame, modal);
		pickedTypes = new ArrayList<LandType>();
		init();
		this.pack();
	}
	
	private void init() {
		mainPanel = new JPanel();
		mainPanel.setLayout(new FlowLayout());
		this.add(mainPanel);
		
		first = new JComboBox<LandType>(TYPES);
		mainPanel.add(first);
		
		second = new JComboBox<LandType>(TYPES);
		mainPanel.add(second);
		
		confirmButton = new JButton("Confirm");
		confirmButton.addActionListener(new DialogueClosingListener(this));
		mainPanel.add(confirmButton);
	}

	@Override
	public void setPick() {
		pickedTypes.add((LandType)first.getSelectedItem());
		pickedTypes.add((LandType)second.getSelectedItem());
	}
	
	public List<LandType> getPicks(){
		return pickedTypes;
	}
}
