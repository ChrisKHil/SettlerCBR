package frames;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import pieces.Card;
import player.Player;

@SuppressWarnings("serial")
public class HandcardPanel extends JPanel {
	
	private JScrollPane scrollPane;
	
	private JPanel scrollPanel;
	
	private JScrollPane scrollPaneActivtadCards;
	
	private JPanel scrollPanelActivatedCards;
	
	private JLabel handCardLabel;
	
	private JLabel activaCardLabel;
	
	private MainFrame frame;
	
	public HandcardPanel(MainFrame frame) {
		this.frame = frame;
		this.setLayout(new GridBagLayout());
		init();
	}
	
	private void init () {
		GridBagConstraints constrained = new GridBagConstraints();
		constrained.gridx = 0;
		constrained.gridy = 0;
		constrained.weightx = 1;
		constrained.fill = GridBagConstraints.HORIZONTAL;
		handCardLabel = new JLabel("Handcards");
		this.add(handCardLabel, constrained);
		
		//GridBagConstraints constrained = new GridBagConstraints();
		constrained.gridx = 1;
		constrained.gridy = 0;
		constrained.weightx = 1;
		constrained.fill = GridBagConstraints.HORIZONTAL;
		activaCardLabel = new JLabel("Active Cards");
		this.add(activaCardLabel, constrained);
		
		scrollPanel = new JPanel();
		scrollPanel.setLayout(new GridLayout(0,1));
		
		//GridBagConstraints constrained = new GridBagConstraints();
		constrained.gridx = 0;
		constrained.gridy = 1;
		constrained.weighty = 1;
		constrained.fill = GridBagConstraints.BOTH;
		scrollPane = new JScrollPane(scrollPanel);
		this.add(scrollPane, constrained);
		
		scrollPanelActivatedCards = new JPanel();
		scrollPanelActivatedCards.setLayout(new GridLayout(0,1));
		
		//GridBagConstraints constrained = new GridBagConstraints();
		constrained.gridx = 1;
		constrained.gridy = 1;
		constrained.weighty = 1;
		constrained.fill = GridBagConstraints.BOTH;
		scrollPaneActivtadCards = new  JScrollPane(scrollPanelActivatedCards);
		this.add(scrollPaneActivtadCards, constrained);
	}
	
	@SuppressWarnings("unused")
	private void debugCardCreation() {
		for (int i = 0; i< 15; i++) {
			JButton temp = new JButton("Dummy");
			scrollPanel.add(temp);
		}
	}
	
	public void createPlayerHand(Player player) {
		System.out.println(">Creating the new player hand: " + player.getHand().size() + " Size bevore remove: " + scrollPanel.getComponents().length);
		scrollPanel.removeAll();
		scrollPanelActivatedCards.removeAll();
		System.out.println(">Removed cards, test: " + scrollPanel.getComponents().length + " Handsize: " + player.getHand().size());
		for (Card c : player.getHand()) {
			System.out.println("		>we are in the creation");
			CardButton card = new CardButton(c, frame);
			scrollPanel.add(card);
		}
		
		for (Card c : player.getActiveCards()) {
			System.out.println("		>we are in the creation");
			CardButton card = new CardButton(c, frame);
			card.setEnabled(false);
			scrollPanelActivatedCards.add(card);
		}
		//To show the removed buttons
		scrollPanel.validate();
		scrollPanel.repaint();
		scrollPanelActivatedCards.validate();
		scrollPanelActivatedCards.repaint();
	}
	
	public void addCardToHand(Card card) {
		System.out.println("		>Adding Card Methode called, current Hand: " + scrollPanel.getComponents().length);
		CardButton cardButton = new CardButton(card, frame);
		scrollPanel.add(cardButton);
		System.out.println("		>Added Card to hand, current Hand: " + scrollPanel.getComponents().length);
	}
	
	public void addActiveCard (Card c) {
		CardButton card = new CardButton(c, frame);
		card.setEnabled(false);
		scrollPanelActivatedCards.add(card);
	}
	
	public JScrollPane getScrollPane() {
		return scrollPane;
	}
}