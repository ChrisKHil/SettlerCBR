package dialogue;

import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import Util.TradingUtils;
import enums.LandType;
import frames.MainFrame;
import listener.DialogueClosingListener;
import listener.DiscardRessourceListener;
import player.Player;

public class DiscardCardsToThievesDialogue extends TradingCardDialogue{	
	
	private Player player;
	
	private JPanel mainPanel;
	
	private int threshold;
	
	private List<JComboBox<Integer>> boxes;
	
	private JLabel cornLabel;
	
	private JComboBox<Integer> conCombo;
	
	private JLabel clayLabel;
	
	private JComboBox<Integer> claCombo;
	
	private JLabel whoolLabel;
	
	private JComboBox<Integer> whoCombo;
	
	private JLabel stoneLabel;
	
	private JComboBox<Integer> stoCombo;
	
	private JLabel lumberLabel;
	
	private JComboBox<Integer> lumCombo;
	
	private List<LandType> toDiscard;
	
	private JButton confirm;
	
	public DiscardCardsToThievesDialogue(Player player, boolean modal, MainFrame frame) {
		super(frame, modal);
		this.player = player;
		toDiscard = new ArrayList<LandType>();
		init();
		
		this.pack();
	}
	
	private void init() {
		mainPanel = new JPanel();
		mainPanel.setLayout(new FlowLayout());
		this.add(mainPanel);
		
		boxes = new ArrayList<JComboBox<Integer>>();
		
		int ressouceCount = TradingUtils.createShuffleList(player).size();
		if (ressouceCount > 7) {
			threshold = ressouceCount / 2;
		}
		System.out.println("Threshold: " + threshold);

		
		clayLabel = new JLabel("Clay: ");
		claCombo = new JComboBox<Integer>();
		claCombo.setModel(TradingUtils.createComboboxModel(player.getClay() < threshold ? player.getClay() : threshold));
		claCombo.addActionListener(new DiscardRessourceListener(this, claCombo));
		boxes.add(claCombo);
		mainPanel.add(clayLabel);
		mainPanel.add(claCombo);
		
		cornLabel = new JLabel("Cron: ");
		conCombo = new JComboBox<Integer>();
		conCombo.setModel(TradingUtils.createComboboxModel(player.getCorn() < threshold ? player.getCorn() : threshold));
		conCombo.addActionListener(new DiscardRessourceListener(this, conCombo));
		boxes.add(conCombo);
		mainPanel.add(cornLabel);
		mainPanel.add(conCombo);
		
		lumberLabel = new JLabel("Lumber: ");
		lumCombo = new JComboBox<Integer>();
		lumCombo.setModel(TradingUtils.createComboboxModel(player.getLumber() < threshold ? player.getLumber() : threshold));
		lumCombo.addActionListener(new DiscardRessourceListener(this, lumCombo));
		boxes.add(lumCombo);
		mainPanel.add(lumberLabel);
		mainPanel.add(lumCombo);
		
		stoneLabel = new JLabel("Stone: ");
		stoCombo = new JComboBox<Integer>();
		stoCombo.setModel(TradingUtils.createComboboxModel(player.getStone() < threshold ? player.getStone() : threshold));
		stoCombo.addActionListener(new DiscardRessourceListener(this, stoCombo));
		boxes.add(stoCombo);
		mainPanel.add(stoneLabel);
		mainPanel.add(stoCombo);
		
		whoolLabel = new JLabel("Whool: ");
		whoCombo = new JComboBox<Integer>();
		whoCombo.setModel(TradingUtils.createComboboxModel(player.getWhool() < threshold ? player.getWhool() : threshold));
		whoCombo.addActionListener(new DiscardRessourceListener(this, whoCombo));
		boxes.add(whoCombo);
		mainPanel.add(whoolLabel);
		mainPanel.add(whoCombo);	
		
		confirm = new JButton("Confirm");
		confirm.addActionListener(new DialogueClosingListener(this));
		mainPanel.add(confirm);
	}	
	
	public DefaultComboBoxModel<Integer> createComboboxModel(int countTo, int choosen) {
		System.out.println("			>We got a ModelChange with choosen: " + choosen + " and Count up to: " + countTo);
		if (countTo < choosen) {
			countTo = choosen;
		}
		DefaultComboBoxModel<Integer> model = new DefaultComboBoxModel<Integer>();
		for (int i = 0; i <= countTo; i++) {
			model.addElement(i);
		}
		model.setSelectedItem(choosen);
		return model;
	}
	
	public int getRestThreshold(int maxValue) {
		int alreadyChoosen = 0;
		alreadyChoosen += (int)conCombo.getSelectedItem();
		System.out.println("				>We got: " + alreadyChoosen);
		alreadyChoosen += (int)claCombo.getSelectedItem();
		System.out.println("				>We got: " + alreadyChoosen);
		alreadyChoosen += (int)whoCombo.getSelectedItem();
		System.out.println("				>We got: " + alreadyChoosen);
		alreadyChoosen += (int)stoCombo.getSelectedItem();
		System.out.println("				>We got: " + alreadyChoosen);
		alreadyChoosen += (int)lumCombo.getSelectedItem();
		System.out.println("				>We got: " + alreadyChoosen);
		int restThreshold = threshold - alreadyChoosen;
		System.out.println("				>We got REST OF : " + restThreshold);
		int tempThreshold = (maxValue <= restThreshold) ? maxValue : restThreshold;
		System.out.println("				>We got TEMP OF: " + tempThreshold);
		return (tempThreshold) >= 0 ? (tempThreshold) : 0;
	}
	
	public void fitRest(JComboBox<Integer> source) {
		int counter = 0;
		for (LandType t : TradingUtils.getTypes()) {
			if (!boxes.get(counter).equals(source)) {
				System.out.println("			>We got a Box");
				//Problem: getItemCounte gets automaticaly reduced, some way to get the acutall ressouce capacety form the palyer in a loop?
				(boxes.get(counter)).setModel(createComboboxModel(getRestThreshold(player.getRessourceByEnum(t)), (int)(boxes.get(counter)).getSelectedItem()));
			}
			counter++;
		}
	}

	@Override
	public void setPick() {
		int counter = 0;
		for (LandType t : TradingUtils.getTypes()) {
			for (int i = 0; i < (int)boxes.get(counter).getSelectedItem(); i++) {
				toDiscard.add(t);
			}
			counter++;
		}
	}
	
	public List<LandType> getToDiscard(){
		return toDiscard;
	}
}
