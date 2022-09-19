package dialogue;

import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import Util.Trade;
import Util.TradingUtils;
import player.Player;

public class ResourcePickerComboboxes extends JPanel {
	
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
	
	private List<JComboBox<Integer>> boxes;
	
	public ResourcePickerComboboxes() {
		this.setLayout(new FlowLayout());
		boxes = new ArrayList<JComboBox<Integer>>();
		
		init();
	}
	
	private void init() {
		clayLabel = new JLabel("Clay: ");
		claCombo = new JComboBox<Integer>();
		claCombo.setModel(TradingUtils.createComboboxModel(20));
		boxes.add(claCombo);
		this.add(clayLabel);
		this.add(claCombo);
		
		cornLabel = new JLabel("Cron: ");
		conCombo = new JComboBox<Integer>();
		conCombo.setModel(TradingUtils.createComboboxModel(20));
		boxes.add(conCombo);
		this.add(cornLabel);
		this.add(conCombo);
		
		lumberLabel = new JLabel("Lumber: ");
		lumCombo = new JComboBox<Integer>();
		lumCombo.setModel(TradingUtils.createComboboxModel(20));
		boxes.add(lumCombo);
		this.add(lumberLabel);
		this.add(lumCombo);
		
		stoneLabel = new JLabel("Stone: ");
		stoCombo = new JComboBox<Integer>();
		stoCombo.setModel(TradingUtils.createComboboxModel(20));
		boxes.add(stoCombo);
		this.add(stoneLabel);
		this.add(stoCombo);
		
		whoolLabel = new JLabel("Whool: ");
		whoCombo = new JComboBox<Integer>();
		whoCombo.setModel(TradingUtils.createComboboxModel(20));
		boxes.add(whoCombo);
		this.add(whoolLabel);
		this.add(whoCombo);	
	}
	
	public int[] getRessourceListAsArray () {
		int[] temp = new int[5];
		int counter = 0;
		for (JComboBox<Integer> c : boxes) {
			temp[counter] = (Integer)c.getSelectedItem();
			counter++;
		}	
		return temp;
	}
	
	public void setBoxesByPlayer(Player player) {
		claCombo.setModel(TradingUtils.createComboboxModel(player.getClay()));
		conCombo.setModel(TradingUtils.createComboboxModel(player.getCorn()));
		lumCombo.setModel(TradingUtils.createComboboxModel(player.getLumber()));
		stoCombo.setModel(TradingUtils.createComboboxModel(player.getStone()));
		whoCombo.setModel(TradingUtils.createComboboxModel(player.getWhool()));
	}
	
	public void setValuesByTradeObject(int[] values) {
		System.out.println("TRADE PANEL : ");
		TradingUtils.pritPrintResourceArray(values);
		claCombo.setModel(TradingUtils.createComboboxModel(values[0]));
		claCombo.setSelectedItem(values[0]);
		conCombo.setModel(TradingUtils.createComboboxModel(values[1]));
		conCombo.setSelectedItem(values[1]);
		lumCombo.setModel(TradingUtils.createComboboxModel(values[2]));
		lumCombo.setSelectedItem(values[2]);
		stoCombo.setModel(TradingUtils.createComboboxModel(values[3]));
		stoCombo.setSelectedItem(values[3]);
		whoCombo.setModel(TradingUtils.createComboboxModel(values[4]));
		whoCombo.setSelectedItem(values[4]);
	}
	
	public void setEnabledForBoxes(boolean enabled) {
		for (JComboBox<Integer> c : boxes) {
			c.setEnabled(enabled);
		}
	}
}
