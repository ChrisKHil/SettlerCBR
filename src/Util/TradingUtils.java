package Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.swing.DefaultComboBoxModel;

import enums.HarbourType;
import enums.LandType;
import player.Player;
/**
 * Offering methodes to enable the trading mechanic.
 * @author Alex
 *
 */
public class TradingUtils {
	
	private static final LandType[] TYPES = {LandType.CLAY, LandType.CORN, LandType.LUMBER, LandType.STONE, LandType.WHOOL};
	/**
	 * Order of clay, corn, lumber, stone, whool
	 */
	private static final int[] TOWN_PRICE = {1, 1, 1, 0, 1};	
	
	private static final int[] CITY_PRICE = {0, 2, 0, 3, 0};	
	
	private static final int[] STREET_PRICE = {1, 0, 1, 0, 0};	
	
	private static final int[] CARD_PRICE = {0, 0, 1, 1, 1};	
	
	public static void tradeWithBank(Player player, LandType offerdItem, LandType tradedItem , HarbourType tradOption) {
		//Get the value of the proposed ressource, it needs to be bigger than the threshold
		System.out.println(offerdItem + " : " + player.getRessourceByEnum(offerdItem) + " while " + getNeededValue(tradOption) + " isneeded" );
		System.out.println(player.toString());
		if (player.getRessourceByEnum(offerdItem) >= getNeededValue(tradOption)) {
			//reduces the ressources by taking in a landtype
			for (int i = 0; i < getNeededValue(tradOption); i++) {
				System.out.println("Reducing by one for :  " + offerdItem);
				player.reduceRessourceByEnum(offerdItem);
			}
			//Then adds one to the traded in ressource
			player.addRessourceByEnum(tradedItem, false);
		}
	}
	
	public static int getNeededValue(HarbourType type) {
		int value = 0;
		switch (type) {
		case THREE_TO_ONE:
			value = 3;
			break;
		case NONE:
			value = 4;
			break;
		default:
			value = 2;
			break;
		}		
		return value;
	}
	
	/**
	 * Determines the possible trades for a given threshold.
	 * @param type
	 * @return
	 */
	public static List<LandType> getPossibleTradeOffers(Player player, HarbourType type) {	
		List<LandType> tempList = new ArrayList<LandType>();
		if (player.getRessourceByEnum(LandType.CORN) >= TradingUtils.getNeededValue(type)) {
			tempList.add(LandType.CORN);
		}  
		if (player.getRessourceByEnum(LandType.CLAY) >= TradingUtils.getNeededValue(type)) {
			tempList.add(LandType.CLAY);
		} 
		if (player.getRessourceByEnum(LandType.LUMBER) >= TradingUtils.getNeededValue(type)) {
			tempList.add(LandType.LUMBER);
		} 
		if (player.getRessourceByEnum(LandType.STONE) >= TradingUtils.getNeededValue(type)) {
			tempList.add(LandType.STONE);
		} 
		if (player.getRessourceByEnum(LandType.WHOOL) >= TradingUtils.getNeededValue(type)) {
			tempList.add(LandType.WHOOL);
		}
		System.out.println("		>Here we got : " + tempList.size() + " possible Offerns");
		return tempList;
	}
	
	public static List<LandType> getPossibleTradePicks(HarbourType type){
		List<LandType> tempList = new ArrayList<LandType>();
		if (type == HarbourType.CLAY) {
			tempList.add(LandType.CLAY);
		} else if (type == HarbourType.CORN) {
			tempList.add(LandType.CORN);
		} else if (type == HarbourType.STONE) {
			tempList.add(LandType.STONE);
		} else if (type == HarbourType.WHOOL) {
			tempList.add(LandType.WHOOL);
		} else if (type == HarbourType.LUMBER) {
			tempList.add(LandType.LUMBER);
		} else {
			tempList.addAll(Arrays.asList(TradingUtils.getTypes()));
		}
		
		return tempList;
	}
	
	public static int sumOfRessources (int[] resourceArray) {
		int sum = 0; 
		for (int i = 0; i < 5; i++) {
			sum += resourceArray[i];
		}
		return sum;
	}
	
	public static LandType[] getTypes() {
		return TYPES;
	}
	
	/**
	 * Takes a ressource at Random from another player.
	 * @param activePlayer
	 * @param choosenPlayer
	 */
	public static void takeRessourceFromPlayer(Player activePlayer, Player choosenPlayer) {
		if (choosenPlayer != null) {
			Random rand = new Random();
			List<LandType> tempArrayList =  createShuffleList(choosenPlayer);
			LandType choosenPick = tempArrayList.get(rand.nextInt(tempArrayList.size()));
			activePlayer.addRessourceByEnum(choosenPick, false);
			choosenPlayer.reduceRessourceByEnum(choosenPick);
		}
	}
	/**
	 * Crates a List to randomly pick a ressource from a player.
	 * @param player
	 * @return
	 */
	public static List<LandType> createShuffleList (Player player) {
		List<LandType> tempShuffleList = new ArrayList<LandType>();
		//Create a list representing a grip full of cards. Add a landtype of a given card
		if (player != null) {
			for (LandType t : Arrays.asList(TradingUtils.getTypes())) {
				for (int i = 0; i < player.getRessourceByEnum(t); i++) {
					tempShuffleList.add(t);
				}
			}
			Collections.shuffle(tempShuffleList);
			System.out.println("	>We now added " + tempShuffleList.size() + " possible picks");
		}
		return tempShuffleList; 
	}

	public static DefaultComboBoxModel<Integer> createComboboxModel(int countTo) {
		DefaultComboBoxModel<Integer> model = new DefaultComboBoxModel<Integer>();
		for (int i = 0; i <= countTo; i++) {
			model.addElement(i);
		}
		model.setSelectedItem(0);
		return model;
	}
	
	public static int[] getTownPriceArray() {
		return TOWN_PRICE;
	}
	public static int[] getCityPriceArray() {
		return CITY_PRICE;
	}
	public static int[] getSteetPriceArray() {
		return STREET_PRICE;
	}
	public static int[] getCardPriceArray() {
		return CARD_PRICE;
	}
	/**
	 * Taking a resource array and putting all negative values to 0. Resources arrays are used in a way, so that
	 * only the positive values are of interest.
	 * @param ressourceArray
	 */
	public static void roundRessourceArray(int[] ressourceArray) {
		for (int i = 0; i < 5; i++) {
			if (ressourceArray[i] < 0) {
				ressourceArray[i] = 0;
			}
		}
	}
	
	public static int[] subtractPriceArrays(int[] ownedResources, int[] spentResources) {	
		int[] resultArray = new int[5];
		
		for (int i = 0; i < 5; i++) {
			resultArray[i] = ownedResources[i] - spentResources[i];
		}
		return resultArray;
	}
	
	public static boolean containsNegativeResources(int[] resources) {
		boolean hasSome = false;
		for (int i = 0; i < 5; i++) {
			if (resources[i] < 0) {
				hasSome = true;
			}
		}
		return hasSome;
	}
	
	public static boolean isNeededOffer(int[] offerArray, int[] neededResources) {
		boolean isNeeded = false;
		for(int i = 0; i < 5; i++) {
			if (offerArray[i] > 0 && neededResources[i] > 0) {
				isNeeded = true;
			}
		}
		return isNeeded;
	}
	
	public static boolean canBanktrade(int[] restresource, int nessesaryResource) {
		boolean canTrade = false;
		
		for (int i = 0; i < 5; i++) {
			if (restresource[i] >= nessesaryResource) {
				canTrade = true;
			}
		}
		return canTrade;
	}
	
	/**
	 * Creates an array containing the resource spend.
	 * @param restresource The resources that are left if the next step in the plan is executed.
	 * @return An array containing the necessary number of a resource to trade with the bank.
	 */
	public static int[] createBankTradingArray(int[] restresource, int[] harbourTypeArray) {
		int[] bankTradeArray = new int[5];
		int choosen = 6;
		for (int i = 0; i < 5; i++) {
			if(choosen == 6) {
				System.out.println("Rest: " +restresource[i] + " Harbour at i : " +  harbourTypeArray[i]);
			} else {
				System.out.println("Rest: " +restresource[i] + " Harbour at i : " +  harbourTypeArray[i] + " Harbour at choosen : " + harbourTypeArray[choosen] );
			}
			if (restresource[i] >= harbourTypeArray[i] && choosen == 6) {
				System.out.println("Found tradable resource :" + restresource[i] + " Setting to : " + harbourTypeArray[i]);
				bankTradeArray[i] = harbourTypeArray[i];
				choosen = i;
			} else if (restresource[i] >= harbourTypeArray[i] && harbourTypeArray[i] <= harbourTypeArray[choosen]) {
				System.out.println("Found tradable resource :" + restresource[i] + " Setting to : " + harbourTypeArray[i]);
				bankTradeArray[i] = harbourTypeArray[i];
				if (bankTradeArray[choosen] > 0 ) {
					bankTradeArray[choosen] = 0;
				}
				choosen = i;
			}
		}
		System.out.println("----BANKTRADING: ");
		TradingUtils.pritPrintResourceArray(bankTradeArray);
		return bankTradeArray;
	}
	/*
	 * fix note: prior to the fix the method created an int cooosen = 6 and then tried to access the harbourTradeArray at
	 * index 6, which caused an out of bounds exception every time 
	 * this method returns an array that contains zeros except for the index that contains the best trade rate
	 * and that the player has enaugh resources to trade for. I am not sure if this is intended but it seemed that way
	 * prior to the fix
	 */
	
	public static void pritPrintResourceArray(int[] array) {
		for (int i = 0; i < 5; i++) {
			System.out.print(array[i] + ":");
		}
	}
	
	public static LandType getLandTypeFromResourceArray(int[] resourceArray) {
		int typeId = 0;
		for (int i = 0; i < 5; i++) {
			if (resourceArray[i] > 0) {
				typeId = i;
			}
		}
		return TYPES[typeId];
	}
	
	public static void tradeWithBank(Player player, int[] offerArray, int[] tradingArray) {
		tradeWithBank(player, getLandTypeFromResourceArray(offerArray), getLandTypeFromResourceArray(tradingArray), player.harbourTypeByResourceArray(tradingArray));
	}
	
	public static void agentTrading(Player reciver, Player sender, Trade trade) {
		reciver.reduceResourcesByTradingArray(trade.getRequested());
		reciver.addResourcesByTradingArray(trade.getOfferd());
		sender.reduceResourcesByTradingArray(trade.getOfferd());
		sender.addResourcesByTradingArray(trade.getRequested());
	}
}
