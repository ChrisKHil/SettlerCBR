package frames;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import pieces.Card;

public class CardButton extends JButton{

	private Card card;
	
	private MainFrame frame;
	
	public CardButton(Card card, MainFrame frame) {
		this.frame = frame;
		this.card = card;
		this.setText(card.getEffect().toString());
		
		this.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.activatedCard(card);		
			}
		});
	}
	
	public Card getCard() {
		return card;
	}
}
