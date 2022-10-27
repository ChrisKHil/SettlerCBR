package player;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import Util.ColorUtils;
import Util.TradingUtils;
import dialogue.TradeDialogue;
import enums.CardEffect;
import enums.HarbourType;
import enums.LandType;
import jade.util.leap.Serializable;
import pieces.Card;
import pieces.CityPiece;
import pieces.StreetPiece;
import pieces.TownPiece;

public class Player implements Serializable{
	
	private int whool;
	
	private int stone;
	
	private int lumber;
	
	private int corn;
	
	private int clay;
	
	private boolean isKI;
	
	//private AgentController agent;
	
	private Color color;
	
	private boolean hasMostKnights;
	
	private boolean hasLongestStreet;
	
	private List<TownPiece> ownedTownPieces;
	
	private List<CityPiece> ownedCityPieces;
	
	private List<StreetPiece> ownedStreetPieces;
	
	private List<TownPiece> placedTownPieces;
	
	private List<CityPiece> placedCityPieces;
	
	private List<StreetPiece> placedStreetPieces;
	
	private List<HarbourType> harbors;
	
	private List<Card> handcard;
	
	private List<Card> activeCards;
	
	public Player(Color color) {
		this.color = color;
		ownedTownPieces = new ArrayList<TownPiece>();
		ownedCityPieces = new ArrayList<CityPiece>();
		ownedStreetPieces = new ArrayList<StreetPiece>();
		harbors = new ArrayList<HarbourType>();
		harbors.add(HarbourType.NONE);
		
		placedTownPieces = new ArrayList<TownPiece>();
		placedCityPieces = new ArrayList<CityPiece>();
		placedStreetPieces = new ArrayList<StreetPiece>();
		
		handcard = new ArrayList<Card>();
		activeCards = new ArrayList<Card>();
		
		for (int i = 0; i < 5; i ++) {
			ownedTownPieces.add(new TownPiece(this));
		}
		for (int i = 0; i < 4; i ++) {
			ownedCityPieces.add(new CityPiece(this));
		}
		for (int i = 0; i < 15; i ++) {
			ownedStreetPieces.add(new StreetPiece(this));
		}
		//System.out.println("Piece Size: " + ownedTownPieces.size());
	}
	
	
	@Override
	public String toString() {
		String temp = color.toString() + "\nRessources:\n " +  " Clay: " + clay + " Corn: " + corn + " Lumber: " + lumber + " Stone: " + stone + " Whool: " + whool;		
		return temp;
	}
	
	public void addRessourceByEnum(LandType type, boolean isCity) {
		switch (type) {
			case WHOOL:
				whool += isCity ? 2 : 1;
			break;
			case LUMBER:
				lumber += isCity ? 2 : 1;
			break;
			case STONE:
				stone += isCity ? 2 : 1;
			break;
			case CORN:
				corn += isCity ? 2 : 1;
			break;
			case CLAY:
				clay += isCity ? 2 : 1;
			break;
			default:
			break;
		}
	}
	
	public Color getColor() {
		return color;
	}
	
	public boolean canBuildTown() {
		return (lumber > 0 && clay > 0 && corn > 0 && whool > 0);
	}
	
	public boolean canBuildStreet() {
		return (lumber > 0 && clay > 0);
	}
	
	public boolean canBuildCity() {
		return (stone > 2 && corn > 1);
	}
	
	public boolean canBuyCard() {
		return (stone > 0 && corn > 0 && whool > 0);
	}
	
	public int[] getNeededRessources(int[] priceArray) {
		int[] neededRessources = new int[5];
		int[] currentRessources = toRessourceArray();
		for (int i = 0; i < 5; i++) {
			neededRessources[i] = priceArray[i] - currentRessources[i];
		}
		TradingUtils.roundRessourceArray(neededRessources);
		System.out.print("Resources Needed:");
		TradingUtils.pritPrintResourceArray(neededRessources);
		System.out.println("");
		return neededRessources; 
	}
	/**
	 * Done to determine the possible amount of tradable resource cards given a certain priceArray for next actions.
	 * @param priceArray
	 * @return
	 */
	public int[] getRestRessources(int[] priceArray) {
		int[] restRessources = TradingUtils.subtractPriceArrays(toRessourceArray(), priceArray);
		TradingUtils.roundRessourceArray(restRessources);
		System.out.println("Rest Resources:");
		TradingUtils.pritPrintResourceArray(restRessources);
		System.out.println("");
		return restRessources;
	}
	
	public boolean isKI() {
		return isKI;
	}
	
	public void setKI(boolean isKI) {
		this.isKI = isKI;
	}
	
	private void payTown() {
		lumber--;
		clay--;
		corn--;
		whool--;
	}
	
	private void payCity() {
		stone -= 3;
		corn -= 2;
	}
	
	private void payStreet() {
		lumber--;
		clay--;
	}
	
	private void payCard() {
		corn--;
		whool--;
		stone--;
	}
	/**
	 * Will return null if not possible, should be handled when setting, dont remove town until city is in place.
	 * @param piece
	 * @return
	 */
	public CityPiece placeCityPiece(TownPiece piece) {
		CityPiece temp = null;
		if (!ownedCityPieces.isEmpty()) {
			placedTownPieces.remove(piece);
			ownedTownPieces.add(piece);
			
			temp = ownedCityPieces.get(0);
			placedCityPieces.add(temp);
			ownedCityPieces.remove(temp);
		}
		payCity();
		return temp;
	}
	
	public StreetPiece placeStreetPiece(boolean firstTurn) {
		StreetPiece temp = null;
		if (!ownedStreetPieces.isEmpty()) {
			temp = ownedStreetPieces.get(0);
			placedStreetPieces.add(temp);
			ownedStreetPieces.remove(temp);
		}
		if (!firstTurn) {
			payStreet();
		}
		return temp;
	}
	
	public TownPiece placeTownPiece(boolean firstTurn) {
		TownPiece temp = null;
		if (!ownedTownPieces.isEmpty()) {
			temp = ownedTownPieces.get(0);
			placedTownPieces.add(temp);
			ownedTownPieces.remove(temp);
		}
		if (!firstTurn) {
			payTown();
		}
		return temp;
	}
	
	public int getWhool() {
		return whool;
	}
	public int getClay() {
		return clay;
	}
	public int getCorn() {
		return corn;
	}
	public int getLumber() {
		return lumber;
	}
	public int getStone() {
		return stone;
	}
	
	public boolean getHasMostKnights() {
		return hasMostKnights;
	}
	
	public boolean getHastLongestStreet() {
		return hasLongestStreet;
	}
	
	public void setHasMostKnights(boolean hasMostKnights) {
		this.hasMostKnights = hasMostKnights;
	}
	
	public void setHastLongestStreet(boolean hasLonstestStreet) {
		this.hasLongestStreet = hasLonstestStreet;
	}
	
	public int getRessourceByEnum (LandType type) {
		int temp = 0;
		switch (type) {
		case WHOOL:
			temp = getWhool();
		break;
		case LUMBER:
			temp = getLumber();
		break;
		case STONE:
			temp = getStone();
		break;
		case CORN:
			temp = getCorn();
		break;
		case CLAY:
			temp = getClay();
		break;
		default:
		break;
		}
		return temp;
	}
	
	public void reduceRessourceByEnum (LandType type) {
		switch (type) {
		case WHOOL:
			whool--;
		break;
		case LUMBER:
			lumber--;
		break;
		case STONE:
			stone--;
		break;
		case CORN:
			corn--;
		break;
		case CLAY:
			clay--;
		break;
		default:
		break;
		}
	}
	
	public void addRessourceByEnum(LandType type, int number) {
		for (int i =0; i < number; i++) {
			addRessourceByEnum(type, false);
		}
	}
	
	public void reduceRessourcesByEnum(LandType type, int number) {
		for (int i = 0; i < number; i++) {
			reduceRessourceByEnum(type);
		}
	}

	public void reduceResourcesByTradingArray(int[] array) {
		clay -= array[0];
		corn -= array[1];
		lumber -= array[2];
		stone -= array[3];
		whool -= array[4];
	}
	
	public void addResourcesByTradingArray(int[] array) {
		clay += array[0];
		corn += array[1];
		lumber += array[2];
		stone += array[3];
		whool += array[4];
	}
	
	public void reduceRessourcesByEnum(List<LandType> types) {
		for (LandType t : types) {
			reduceRessourceByEnum(t);
		}
	}
	
	public void addCard(Card card) {
		System.out.println("	>Adding the card and paying for it.");
		handcard.add(card);
		payCard();
	}
	/**
	 * for knight and victory point cards.
	 * @param card
	 */
	public void activateCard(Card card) {
		if (handcard.contains(card)) {
			handcard.remove(card);
			activeCards.add(card);
		}
	}
	
	public void discardCard(Card card) {
		if (handcard.contains(card)) {
			handcard.remove(card);
		}
	}
	
	public List<Card> getActiveCards() {
		return activeCards;
	}
	
	public List<Card> getHand() {
		return handcard;
	}
	/**
	 * calculates the victory points for a given player.
	 * @return
	 */
	public int getVictoryPoints() {
		int points = 0;
		
		for (TownPiece t : placedTownPieces) {
			points++;
		}
		for (CityPiece p : placedCityPieces) {
			points += 2;
		}
		for (Card c: activeCards) {
			if (c.getEffect() == CardEffect.VICTORY_POINT) {
				points++;
			}
		}
		points += hasLongestStreet ? 2 : 0;
		points += hasMostKnights ? 2 : 0;
		return points;
	}
	
	public int getKnightCount() {
		int sum = 0;
		for (Card c : activeCards) {
			if (c.getEffect() == CardEffect.KNIGHT) {
				sum++;
			}
		}
		return sum;
	}
	/**
	 * Adds only new harbour types.
	 * @param type
	 */
	public void addHardbour(HarbourType type) {
		if (!harbors.contains(type)) {
			harbors.add(type);
		}
	}
	/**
	 * Ressources in order: clay, corn, lumber, stone, whool
	 * @return
	 */
	public int[] toRessourceArray() {
		int[] ressources = new int[5];
		ressources[0] = this.clay;
		ressources[1] = this.corn;
		ressources[2] = this.lumber;
		ressources[3] = this.stone;
		ressources[4] = this.whool;
		return ressources;
	}
	
	public List<HarbourType> getHarbours(){
		return harbors;
	}
	/**
	 * This may need a change for now agents will create a TradingDialogue when executing an agreed upon trade.
	 * @param sender Sender of the Trade.
	 * @param dialogue The dialogue which has the offer and the request.
	 */
	public void reciveTradeReques(Player sender, TradeDialogue dialogue) {
		dialogue.setRecivedStatus(true);
		dialogue.checkIfTradeViable();
		dialogue.setTitle(ColorUtils.colorToString(this.getColor()));
		dialogue.setVisible(true);
		int counter = 0;
		if (dialogue.getAccepted()) {
			for (LandType t : TradingUtils.getTypes()) {
				sender.reduceRessourcesByEnum(t, dialogue.getOfferArray()[counter]);
				sender.addRessourceByEnum(t, dialogue.getRequestArray()[counter]);
				this.reduceRessourcesByEnum(t, dialogue.getRequestArray()[counter]);
				this.addRessourceByEnum(t, dialogue.getOfferArray()[counter]);
				
				counter++;
			}
		}
		
	}
	/**
	 * Checks if the player has at least as many resources as the Array states.
	 * @param resources Resources to look up.
	 * @return if the Player has at leas that many resources
	 */
	public boolean hasResources(int[] resources) {
		boolean hasResources = true;
		int[] playerResources = toRessourceArray();
		//If player has less resources than the resourceArray at any point it will be set to false
		for (int i = 0; i < 5; i++) {
			if (playerResources[i] < resources[i]) {
				hasResources = false;
			}
		}
		return hasResources;
	}
	/**
	 * Returns array with the trade options for the bank for each resource
	 * @return
	 */
	public int[] getHarbortyesAsArray() {
		int[] harbourArray = null;
		if (harbors.contains(HarbourType.THREE_TO_ONE)) {
			harbourArray = new int[] {3, 3, 3, 3, 3};
		} else {
			harbourArray = new int[] {4, 4, 4, 4, 4};
		}
		if (harbors.contains(HarbourType.CLAY)) {
			harbourArray[0] = 2;
		}
		if (harbors.contains(HarbourType.CORN)) {
			harbourArray[1] = 2;
		}
		if (harbors.contains(HarbourType.LUMBER)) {
			harbourArray[2] = 2;
		}
		if (harbors.contains(HarbourType.STONE)) {
			harbourArray[3] = 2;
		}
		if (harbors.contains(HarbourType.WHOOL)) {
			harbourArray[4] = 2;
		}
		return harbourArray;
	}
	
	public HarbourType harbourTypeByResourceArray(int[] resourceArray) {
		HarbourType type = HarbourType.NONE;
		for (int i = 0; i < 5; i++) {
			if (resourceArray[i] == 1) {
				switch(i) {
				case 0:
					type = HarbourType.CLAY;
					break;
				case 1:
					type = HarbourType.CORN;
					break;
				case 2:
					type = HarbourType.LUMBER;
					break;
				case 3:
					type = HarbourType.STONE;
					break;
				case 4:
					type = HarbourType.WHOOL;
					break;
				}
			}
		}
		if (!harbors.contains(type)) {
			type = HarbourType.NONE;
		}
		if (harbors.contains(HarbourType.THREE_TO_ONE)) {
			type = HarbourType.THREE_TO_ONE;
		}
		return type;
	}
	
	/**
	 * Only testing purposes, will be disabled later.
	 */
	public void debugRessources() {
		whool = 5;
		corn = 5;
		stone = 5;
		clay = 5;
		lumber = 5;
	}
	
	public List<TownPiece> getPlacedTownPieces() {
		return placedTownPieces;
	}
	
	public List<CityPiece> getPlacedCityPieces() {
		return placedCityPieces;
	}
	
	public void debugRessources(int i ) {
		whool = 5;
		corn = 5;
		stone = 0;
		if (i == 0) {
			clay = 0;
			lumber = 5;
		} else {
			clay = 5;
			lumber = 0;
		}
	}	
}
