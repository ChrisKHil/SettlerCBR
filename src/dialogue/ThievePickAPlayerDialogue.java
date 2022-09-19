package dialogue;

import java.awt.GridLayout;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import frames.MainFrame;
import listener.DialogueClosingListener;
import player.Player;

public class ThievePickAPlayerDialogue extends TradingCardDialogue {

	private Player choosenPlayer;
	
	private JPanel mainPanel;
	
	private JComboBox<Player> playerBox;
	
	private JButton pickButton;
	
	public ThievePickAPlayerDialogue(MainFrame frame, boolean modal, List<Player> player) {
		super(frame, modal);
		init(player);
		
		this.pack();
	}

	private void init(List<Player> player) {
		mainPanel =  new JPanel();
		mainPanel.setLayout(new GridLayout(0,1));
		this.add(mainPanel);
		
		DefaultComboBoxModel<Player> model = new DefaultComboBoxModel<Player>();	
		for (Player p : player) {
			model.addElement(p);
		}
		
		playerBox = new JComboBox<Player>(model);
		playerBox.setRenderer(new ComboxRendererPlayer());
		mainPanel.add(playerBox);
		
		pickButton = new JButton("Confirm");
		pickButton.addActionListener(new DialogueClosingListener(this));
		mainPanel.add(pickButton);
	}
	
	public Player getChoosenPlayer() {
		return choosenPlayer;
	}
	
	@Override
	public void setPick() {
		this.choosenPlayer = (Player)playerBox.getSelectedItem();
	}
}
