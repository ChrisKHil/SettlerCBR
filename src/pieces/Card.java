package pieces;

import enums.CardEffect;
import jade.util.leap.Serializable;

public class Card implements Serializable{
	/**
	 * To determine the type of card.
	 */
	private CardEffect effect;
	
	public Card(CardEffect effect) {
		this.effect = effect;
	}
	
	public CardEffect getEffect() {
		return effect;
	}
}
