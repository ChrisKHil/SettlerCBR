package frames;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import pieces.Card;

public class ControllInfoBoxPanel extends JPanel {

	private MainFrame frame;
	
	private JLabel turnInfoLabel;
	
//	private JPanel centerOptionPanel;
	
	private HandcardPanel handCardPanel;
	
	public ControllInfoBoxPanel(MainFrame frame) {
		this.frame = frame;
		this.setLayout(new BorderLayout());
		init();
	}
	
	private void init() {
		turnInfoLabel = new JLabel();
		this.add(turnInfoLabel,BorderLayout.NORTH);
		
//		centerOptionPanel = new JPanel();
//		centerOptionPanel.setLayout(new CardLayout());
//		this.add(centerOptionPanel, BorderLayout.CENTER);
		
		handCardPanel = new HandcardPanel(frame);

		this.add(handCardPanel, BorderLayout.CENTER);
	}
	
	public void updateHand(Card card) {
		System.out.println("	>Adding card to hand of " + frame.getActivePlayer().getColor().toString());
		handCardPanel.addCardToHand(card);
	}
	
	public void nextPlayerHand () {
		handCardPanel.createPlayerHand(frame.getActivePlayer());
	}
	
	public void setInfoText(String text) {
		turnInfoLabel.setText(text);
	}
	
	public HandcardPanel getHandCardPanel() {
		return handCardPanel;
	}
}
