package frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import Util.BoardEvent;
import Util.ColorUtils;
import listener.BoardListener;
import player.Player;

public class PlayerPanel extends JPanel implements BoardListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Player player;
	
	private JPanel statPanel;
	
	private JPanel ressourcePanel;
	
	private JPanel winPointsPanel;
	
	private JPanel vPointsPanel;
	
	private JPanel victoryPanel;
	
	private JLabel playerLabel;
	
	private JLabel lumberLabel;
	
	private JLabel whoolLabel;
	
	private JLabel cornLabel;
	
	private JLabel stoneLabel;
	
	private JLabel clayLabel;
	
	private JLabel mostKnights;
	
	private JLabel longestStreet;
	
	private JLabel playerVP;
	
	private JLabel victory;
	
	private JTextArea lumberArea;
	
	private JTextArea whoolArea;
	
	private JTextArea cornArea;
	
	private JTextArea stoneArea;
	
	private JTextArea clayArea;
	
	public PlayerPanel(Player player) {
		this.player = player;
		this.setLayout(new BorderLayout());
		init();
	}
	
	private void init() {
		statPanel = new JPanel();
		statPanel.setLayout(new GridLayout(4,1));
		this.add(statPanel, BorderLayout.SOUTH);
		
		winPointsPanel = new JPanel();
		winPointsPanel.setLayout(new FlowLayout());
		statPanel.add(winPointsPanel);
		
		vPointsPanel = new JPanel();
		vPointsPanel.setLayout(new FlowLayout());
		statPanel.add(vPointsPanel);
		
		victoryPanel = new JPanel();
		victoryPanel.setLayout(new FlowLayout());
		statPanel.add(victoryPanel);
		
		playerLabel = new JLabel(ColorUtils.colorToString(player.getColor()));
		winPointsPanel.add(playerLabel);
		
		mostKnights = new JLabel("Knights (VP): " + player.getHasMostKnights());
		winPointsPanel.add(mostKnights);
		
		longestStreet = new JLabel("Street (VP): " + player.getHastLongestStreet());
		winPointsPanel.add(longestStreet);
		
		playerVP = new JLabel("Victory Points: ");
		vPointsPanel.add(playerVP);
		
		victory = new JLabel("");
		victoryPanel.add(victory);
		
		
		ressourcePanel = new JPanel();
		ressourcePanel.setLayout(new FlowLayout());
		statPanel.add(ressourcePanel);
		
		clayLabel = new JLabel("Clay: ");
		ressourcePanel.add(clayLabel);
		clayArea = new JTextArea();
		clayArea.setEditable(false);
		ressourcePanel.add(clayArea);
		
		cornLabel = new JLabel("Corn: ");
		ressourcePanel.add(cornLabel);
		cornArea = new JTextArea();
		cornArea.setEditable(false);
		ressourcePanel.add(cornArea);
		
		lumberLabel = new JLabel("Lumber: ");
		ressourcePanel.add(lumberLabel);
		lumberArea = new JTextArea();
		lumberArea.setEditable(false);
		ressourcePanel.add(lumberArea);
		
		stoneLabel = new JLabel("Stone: ");
		ressourcePanel.add(stoneLabel);
		stoneArea = new JTextArea();
		stoneArea.setEditable(false);
		ressourcePanel.add(stoneArea);
		
		whoolLabel = new JLabel("Whool: ");
		ressourcePanel.add(whoolLabel);
		whoolArea = new JTextArea();
		whoolArea.setEditable(false);
		ressourcePanel.add(whoolArea);	
		
		updateValues();
	}
	/**
	 * 
	 */
	public void updateValues() {
		lumberArea.setText(Integer.toString(player.getLumber()));
		stoneArea.setText(Integer.toString(player.getStone()));
		cornArea.setText(Integer.toString(player.getCorn()));
		whoolArea.setText(Integer.toString(player.getWhool()));
		clayArea.setText(Integer.toString(player.getClay()));
		mostKnights.setText("Knights (VP): " + player.getHasMostKnights());
		longestStreet.setText("Street (VP): " + player.getHastLongestStreet());
		playerVP.setText("Victory Points: " + Integer.toString(player.getVictoryPoints()));
	}

	@Override
	public void boardEventHappened(BoardEvent event) {
		updateValues();	
		repaint();
	}
	
	public void win(Color color) {
		if(player.getColor().equals(color)) {
			victory.setText("WINNER");
		} else {
			victory.setText("LOSER");
		}
	}
}
