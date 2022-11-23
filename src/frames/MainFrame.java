package frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import Util.AgentUtils;
import Util.BoardEvent;
import Util.ColorUtils;
import Util.LongestStreetUtils;
import Util.Trade;
import Util.TradingUtils;
import agents.AgentActionSettler;
import agents.AgentMessage;
import dialogue.DiscardCardsToThievesDialogue;
import dialogue.OneFromAllDialogue;
import dialogue.ThievePickAPlayerDialogue;
import dialogue.TradeDialogue;
import dialogue.TwoRessourceChoice;
import enums.BoardEvents;
import enums.CardEffect;
import enums.LandType;
import enums.TurnStage;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import jade.wrapper.gateway.JadeGateway;
import listener.BoardListener;
import pieces.Card;
import pieces.CityNode;
import pieces.CityPiece;
import pieces.StreetNode;
import player.Player;
import tiles.LandTile;

public class MainFrame extends JFrame implements BoardListener {

	private static final long serialVersionUID = 8087523880460785480L;
	
	private TurnStage stage;
	/**
	 * In case of random thieve change due to a activated card. We need to return to the correct stage.
	 */
	private TurnStage previousStage;
	
	private Board board;
	
	private JPanel leftPanel;
	
	private List<Color> colors = new ArrayList<Color>(Arrays.asList(Color.RED, Color.BLUE, Color.ORANGE));
	
	private List<Card> cardDrawPile = new ArrayList<Card>();

	private List<Card> cardDiscardPile = new ArrayList<Card>();
	
	private List<PlayerPanel> playerpanels;
	
	private List<Player> player;
	
	private List<AgentController> controllers;
	
	private ControlFrame bottomControl;
	
	private boolean buildStartTown;
	
	private boolean gameRunning;
	
	private boolean thieveDiscard;
	
	private boolean currentlyInTurn;
	
	private final int PLAYERCOUNT = 2;
	
	private final int KI_COUNT = 2;
	/**
	 * Is treated as a int, will be used to get the player from the list as they dont move.
	 * this will be increased to simulate turn order.
	 */
	private int activePlayer = 0;
	
	public MainFrame() {
		Dimension temp = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		this.getContentPane().setPreferredSize(new Dimension((int)(temp.width/1.2),(int)(temp.height/1.2)));
		this.stage = TurnStage.START_GAME;
		init();
		player = new ArrayList<Player>();
		
		playerpanels = new ArrayList<PlayerPanel>();
		gameRunning = false;
		thieveDiscard = false;
		currentlyInTurn = false;
		this.pack();
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setTitle("Settlers");
		this.setVisible(true);
	}
	
	private void init() {
		this.setLayout(new BorderLayout());
		
		leftPanel = new JPanel();
		leftPanel.setPreferredSize(
				new Dimension((int)(this.getContentPane().getPreferredSize().width*0.3), 
						(int)(this.getContentPane().getPreferredSize().height)));
		this.add(leftPanel, BorderLayout.WEST);
		System.out.println("bottom Heigth: " + (int)(this.getPreferredSize().height * 0.2));
		
		bottomControl = new ControlFrame(this);
		bottomControl.setPreferredSize(
				new Dimension((int)(this.getContentPane().getPreferredSize().width*0.8),
						(int)(this.getContentPane().getPreferredSize().height * 0.2)));
		bottomControl.getControlInforPanel().getHandCardPanel().getScrollPane().setPreferredSize(
				new Dimension((int)(this.getContentPane().getPreferredSize().width*0.5),(int)(this.getContentPane().getPreferredSize().height * 0.18)));
		this.add(bottomControl, BorderLayout.SOUTH);
		
		board = new Board(bottomControl, this);
		board.setPreferredSize(
				new Dimension((int)(this.getContentPane().getPreferredSize().width*0.8),
						(int)(this.getContentPane().getPreferredSize().height*0.8)));
		board.setBorder(BorderFactory.createLineBorder(Color.CYAN, 2));
		board.setupBoard();
		this.add(board, BorderLayout.CENTER);
	}
	/**
	 * Initializes the start of the game and stars the necessary number of agents.
	 */
	public void startGame() {		
		String[] KIColors = new String[KI_COUNT];
		for (int i = 0; i < PLAYERCOUNT; i++) {
			player.add(new Player(drawRandomColor()));
			//Define how many KI�s active (later adjustable)
			if (i < KI_COUNT) {
				player.get(i).setKI(true);
				KIColors[i] = ColorUtils.colorToString(player.get(i).getColor());
			}
		}
		LongestStreetUtils.setPlayers(player);
		Collections.shuffle(player);
		int i = 0;
		for(Player p : player) {
			//Debug stuff
			//p.debugRessources(i);
			PlayerPanel tempPanel = new PlayerPanel(p);
			leftPanel.add(tempPanel);
			playerpanels.add(tempPanel);
			board.addBoardListener(tempPanel);
			i++;
		}
		setStage(TurnStage.FIRST_TURN);
		createCardPile();
		bottomControl.getTradePanel().updateHarbourTrades(getActivePlayer());
		leftPanel.setBackground(getActivePlayer().getColor());
		bottomControl.updateTurnInfo();
		gameRunning = true;
		controllers = AgentUtils.creataAgents(this, KI_COUNT, KIColors); //will create three agents, might later be tweaked
		//This seems to be relevant as to not let the application crash as the startup creates problems.
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//setPlayerAgents();
		JadeGateway.init("agents.SettlerGatewayAgent", null);
		System.out.println("HERE WE START THE REAL AGENTES");

		Timer t = new Timer(3000, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("########################################### AGENT TURN STARTS #############################################");
				if ((getActivePlayer().isKI()) && gameRunning && !currentlyInTurn && !thieveDiscard) {
					currentlyInTurn = true;
					if (getActivePlayer().isKI()) {
						agentTurn(new AgentMessage(getPlayerAgentAsReciver(getActivePlayer())));
					} 
					
					updatePlayerPanels();
					board.repaint();
					bottomControl.repaint();
					
					currentlyInTurn = false;
					System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ AGENT TURN ENDS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
				}
			}
		});
		t.start();
	}
	/**
	 * to catch an exception and ease the messaging of non active player agents.
	 * @param p the playeragent to be messaged
	 * @return the name of the player agent as a String
	 */
	private String getPlayerAgentAsReciver (Player p) {
		String temp = "";
		try {
			temp = AgentUtils.AgentNameToColorString(AgentUtils.getAgentByPlayername(p, controllers));
		} catch (StaleProxyException e) {
			e.printStackTrace();
			System.out.println("Error while getting PlayerAgent name!");
		}
		return temp;
	}
	
	
	private void agentTurn(AgentMessage message) {
//		System.out.println("We are starting a Loop right now ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		
		try {
			JadeGateway.execute(message);
		} catch (ControllerException | InterruptedException e) {
			e.printStackTrace();
		}
		evaluateAgentMessage(message);
		System.out.println("       "+ " Turn end on: " + message.getAction());
			
//		System.out.println("I�m Out of my Loop and the game did not pass a turn properly ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	}
	/**
	 * Due to the nature of the Objects returned in the messages it is nessesary to determine the local counterpart to the choosen object as call by reference does not
	 * work as expected. Without the program would "build a street" but on a node that is not acutally on the board but .equals to one on the board.
	 * @param message
	 */
	private void evaluateAgentMessage(AgentMessage message) {
		System.out.println("<<<<<<<<<<<<< Evaluation for Action: " + message.getAction());
		if (message.getAction() != null) {
			switch(message.getAction()) {
			case PLACE_FIRST_TURN_TOWN:
				System.out.println("<<<<<<<<<<<<< Evaluation: Placing Town: " + (CityNode)message.getData() );
				getBoard().buildFirstTurnTown(getBoard().getLocalCityNodeToBuild((CityNode)message.getData()));
				break;
			case PLACE_FIRST_TURN_STREET:
				System.out.println("<<<<<<<<<<<<< Evaluation: Placing street");
				getBoard().buildFirstTurnStreet(getBoard().getLocalStreetNodeToBuild((StreetNode)message.getData()));
				break;
			case PLACE_TOWN:
				System.out.println("<<<<<<<<<<<<< Evaluation: Placing town");
				getBoard().buildTown(getBoard().getLocalCityNodeToBuild((CityNode)message.getData()));
				break;
			case PLACE_STREET:
				System.out.println("<<<<<<<<<<<<< Evaluation: Placing street");
				getBoard().buildStreet(getBoard().getLocalStreetNodeToBuild((StreetNode)message.getData()));
				break;
			case PLACE_CITY:
				System.out.println("<<<<<<<<<<<<< Evaluation: Placing City");
				getBoard().buildCity(getBoard().getLocalCityNodeToBuild((CityNode)message.getData()));
				break;
			case BUY_CARD:
				System.out.println("<<<<<<<<<<<<< Evaluation: Buying Card");
				playerDrawCard();
				break;
			case ROLL_DIE:
				System.out.println("<<<<<<<<<<<<< Evaluation:Rolling Die");
				getBottomControl().rollDice();
				break;
			case CHANGE_THIEVES:
				System.out.println("<<<<<<<<<<<<< Evaluation: Changing Thieves");
				getBoard().changeThieves(getBoard().getLocalLandTileToChangeThieves((Point)message.getData()));
				break;
			case DISCARD_RESSOURCES:
				System.out.println("<<<<<<<<<<<<< Evaluation: Discardin Ressources " + " TESTING RECIVER: " + message.getReciver() + " Ackt: "+ ColorUtils.colorToString(getActivePlayer().getColor()));
				System.out.println("NUMBER OF REDUCED_ " + ((List<LandType>)message.getData()).size());
				getPlayerByColor(message.getReciver()).reduceRessourcesByEnum((List<LandType>)message.getData());;
				break;
			case STEAL_RESSOURCES:
				System.out.println("<<<<<<<<<<<<< Evaluation: Steal Ressources " + " TESTING RECIVER: " + message.getReciver() + "Target: " + ((Player)message.getData()).getColor());
				stealRessources((Player)message.getData());
				break;
			case TRADE_WITH_BANK:
				System.out.println("<<<<<<<<<<<<< Would Trade with bank");
				Trade trade = (Trade) message.getData(); 
				TradingUtils.tradeWithBank(getActivePlayer(), trade.getOfferd(), trade.getRequested());
				System.out.println("Player Ressources: " + getActivePlayer().toString());
				System.out.println(player.toString());
				updatePlayerPanels();
				break;
			case TRADE_WITH_PLAYER:
				System.out.println("Would Trade with palyer");
				Trade tradeAgent = (Trade) message.getData();
				TradingUtils.agentTrading(getPlayerByColor(tradeAgent.getReciver()), getActivePlayer(), tradeAgent);
				break;
			case ADVANCE_TURN: 
				System.out.println("<<<<<<<<<<<<< Evaluation: Advance Turn Stage by one");
				advanceStage();
				break;
			case TRADE_WITH_HUMAN:
				System.out.println("<<<<<<<<<<<<<<Evalutaion: Trading with player");
				Trade tradeHuman = (Trade) message.getData();
				createTrade(colorStringToPlayer(tradeHuman.getReciver()), tradeHuman);
				break;
			case TRADE_HUMAN_DENIED:
				System.out.println("<<<<<<<<<<<<<<Evalutaion: Denied TRADE");
				Trade tradeHumanDenied = (Trade) message.getData();
				createTrade(colorStringToPlayer(tradeHumanDenied.getSender()), tradeHumanDenied);
				break;
			case TRADE_HUMAN_ACCEPTED:
				System.out.println("<<<<<<<<<<<<<<Evalutaion: ACCEPTED TRADE");
				Trade tradeHumanAccepet= (Trade) message.getData();
				TradingUtils.agentTrading(getPlayerByColor(tradeHumanAccepet.getReciver()), getActivePlayer(), tradeHumanAccepet);
				updatePlayerPanels();
				break;
			default:
				break;
			}
		}
	}
	
	private void updateAllPlayerValues() {
		for (Component c: leftPanel.getComponents()) {
			if (c instanceof PlayerPanel) {
				((PlayerPanel) c).updateValues();
			}
		}
	}
	
	private void createCardPile() {
		for (int i = 0; i < 25; i++) {
			if (i < 2) {
				//cardDrawPile.add(new Card(CardEffect.FREE_STREET));
				cardDrawPile.add(new Card(CardEffect.VICTORY_POINT));
			} else if (i < 4) {
				cardDrawPile.add(new Card(CardEffect.GET_ONE_FROM_ALL));
			} else if (i < 6) {
				cardDrawPile.add(new Card(CardEffect.TAKE_TWO_RESSOURCES));
			} else if (i < 10) {
				cardDrawPile.add(new Card(CardEffect.VICTORY_POINT));
			} else if (i < 25) {
				cardDrawPile.add(new Card(CardEffect.KNIGHT));
			}
		}
		Collections.shuffle(cardDrawPile);
	}
	
	public void playerDrawCard() {
		if (cardDrawPile.size() > 0 && getActivePlayer().canBuyCard()) {
			System.out.println("Drawing a card.");
			System.out.println("	>Cards in pile pre draw: " + cardDrawPile.size());
			Card tempCard = cardDrawPile.get(0);
			getActivePlayer().addCard(tempCard);
			System.out.println("	>Player handsize post draw: " + getActivePlayer().getHand().size());
			bottomControl.getControlInforPanel().updateHand(tempCard);
			cardDrawPile.remove(tempCard);
			System.out.println("	>Cards in pile post draw: " + cardDrawPile.size());
			updateAllPlayerValues();
			bottomControl.fitBuildToPlayer();
		}
	}
	
	private Color drawRandomColor() {
		Random rand = new Random();
		int temp = rand.nextInt(colors.size());
		Color tempC = colors.get(temp);
		colors.remove(temp);
		return tempC;
	}
	
	public Board getBoard() {
		return this.board;
	}
	
	public int rollDice() {
		int temp = Util.DieUtils.rollTowDie();
		//int temp = 7;
		if (temp != 7) {
			List<LandTile> tiles = board.getTilesByNumber(temp);
			for (LandTile t : tiles) {
				if (!t.getContainsThieves()) {
					List<CityNode>tempCitys = board.getCloseNodes(t);
					for (CityNode c : tempCitys) {
						if (c.getPiece() != null) {
							//Added resources and does so twice if the piece of the player is a CityPiece
							c.getPiece().getPlayer().addRessourceByEnum(t.getType(), c.getPiece() instanceof CityPiece);
						}
					}				
				}
			}
			board.notifieListeners(new BoardEvent(BoardEvents.RESSOURCES_DISTRIBUTED));
		} else {
			setStage(TurnStage.CHANGE_THIEVES);
		}
		return temp;
	}
	
	public Player getActivePlayer () {
		return player.get(activePlayer);
	}
	
	public void setNextPlayer() {
		if (activePlayer < player.size() -1) {
			activePlayer++;
		} else {
			activePlayer = 0;
		}
	}
	
	public void setNextPlayerFirstTurn() {
		System.out.println("Next---------------------------------------");
		if (activePlayer < player.size() -1 && !(getStage() == TurnStage.SECOND_TURN)) {
			activePlayer++;
		} else if (activePlayer > 0 && (getStage() == TurnStage.SECOND_TURN)) {
			System.out.println("ReducePalyer");
			activePlayer--;
		} else if (activePlayer == 0) {
			setStage(TurnStage.ROLL_DICE);
		} else {
			setStage(TurnStage.SECOND_TURN);
		}
		bottomControl.updateTurnInfo();
		leftPanel.setBackground(getActivePlayer().getColor());
	}
	
	public List<Player> getPlayer() {
		return player;
	}
	
	public Player getPlayerByColor(String color) {
		Player temp = null;
		for (Player p : player) {
			if (p.isKI() && Util.ColorUtils.colorToString(p.getColor()).equals(color)) {
				temp = p;
			}
		}
		return temp;
	}
	
	public TurnStage getStage() {
		//System.out.println("STAGE: " + this.stage.toString());
		return stage;
	}
	
	public void setStage(TurnStage stage) {
		System.out.print(">Transition from: " + this.getStage() + " to " + stage);
		this.previousStage = this.stage;
		this.stage = stage;
		System.out.println(" || Updated to: " + this.getStage() + " and " + this.previousStage);
	}
	
	public boolean getBuildStartTown(){
		return this.buildStartTown;
	}
	/**
	 * Used to switch between building a town and a street at the start of the game.
	 * @param buildStartTown
	 */
	public void setBuildStartTown(boolean buildStartTown) {
		this.buildStartTown = buildStartTown;
	}
	/**
	 * To determine if a player is the last in a turn cycle, used for the start mechanic.
	 * @return
	 */
	public boolean isLastPlayer() {
		return activePlayer == player.size() - 1;
	}
	
	public void freeStreedCard() {
		this.setStage(TurnStage.BUILD_STREET);
	}
	/**
	 * takes one resource of a given type from each non active player as only active player can use cards.
	 * for each resource taken the active player gets one of that kind
	 * @param type
	 */
	public void takeRessoureceFromPlayer(LandType type) {
		int tempSum = 0;
		for (Player p : player) {
			if (!p.equals(player.get(activePlayer))) {
				if (p.getRessourceByEnum(type) > 0) {
					tempSum++;
					p.reduceRessourceByEnum(type);
				}
			}
		}
		for (int i =0; i < tempSum; i++) {
			getActivePlayer().addRessourceByEnum(type, false);
		}
	}
	
	public void advanceStage() {
		switch(getStage()) {
		case ROLL_DICE:
			bottomControl.rollDiceToTrade();
			setStage(TurnStage.TRADE);
			//Change RollDice to Trade.
			bottomControl.getTradePanel().updateHarbourTrades(getActivePlayer());
			bottomControl.getTradePanel().updatePlayerTrades();
			break;
		case TRADE:
			bottomControl.changeTurnEndButton();
			Card j = null;
			if (getActivePlayer().getColor().getBlue() == 255 && getActivePlayer().getHand().size() > 0) {
				for (Card c: getActivePlayer().getHand()) {
					if (c.getEffect() != CardEffect.VICTORY_POINT) {
						j = c;
						break;
					}
				}
			}
			if (j != null) {
				activatedCard(j);
				break;
			}
			setStage(TurnStage.BUILD);
			break;
		case BUILD:
			if (getActivePlayer().getVictoryPoints() >= 10) {
				setStage(TurnStage.END_OF_GAME);
				for(PlayerPanel p:playerpanels) {
					p.win(getActivePlayer().getColor());
				}
				//Dialogue for the end? Replay option?
			} else {
				setStage(TurnStage.ROLL_DICE);
				//ChangeActivePlayer
				setNextPlayer();
				//Reslet Buttons
				leftPanel.setBackground(getActivePlayer().getColor());
				bottomControl.resetToStartOfTurn();
				bottomControl.getControlInforPanel().nextPlayerHand();
			}
			break;
		case FIRST_TURN:			
			break;
		default:
			break;
		}
	}
	
	public boolean getGameRunning() {
		return gameRunning;
	}
	
	/**
	 * Takes a list with LandTypes as input, adds one ressource for each Type at the first two places to the active player.
	 * @param types
	 */
	public void takeRessourcesFromPile (List<LandType> types) {
		getActivePlayer().addRessourceByEnum(types.get(0), false);
		getActivePlayer().addRessourceByEnum(types.get(1), false);
	}
	
	/**
	 * To be called to activate cards during a turn will handle the effects
	 * @param card
	 */
	public void activatedCard(Card card) {
		if (card.getEffect() == CardEffect.KNIGHT || card.getEffect() == CardEffect.VICTORY_POINT) {
			getActivePlayer().activateCard(card);
			if (card.getEffect() == CardEffect.KNIGHT) {
				//Enable the knight thingy
				setStage(TurnStage.CHANGE_THIEVES);
				distributeMostKnightsCard();
			} //other card will just be considered when checking the winning point count
		} else {
			getActivePlayer().discardCard(card);
			cardDiscardPile.add(card);
			if (card.getEffect() == CardEffect.FREE_STREET) {
				setStage(TurnStage.BUILD_STREET);
			} else if (card.getEffect() == CardEffect.TAKE_TWO_RESSOURCES) {
				Random rand = new Random();
				LandType[] types = TradingUtils.getTypes();
				List<LandType> typesToPick = new ArrayList<LandType>();
				typesToPick.add(types[rand.nextInt(5)]);
				typesToPick.add(types[rand.nextInt(5)]);
				
				
				/*
				TwoRessourceChoice tempTdialogue = new TwoRessourceChoice(this, true);
				System.out.println("			>PRE DIALOGUE------------------------------------------");
				tempTdialogue.setVisible(true);
				System.out.println("			>AFTER DIALOGUE------------------------------------------");
				takeRessourcesFromPile(tempTdialogue.getPicks());
				*/
				
				takeRessourcesFromPile(typesToPick);
			} else if (card.getEffect() == CardEffect.GET_ONE_FROM_ALL) {
				Random rand = new Random();
				LandType[] types = TradingUtils.getTypes();
				takeRessoureceFromPlayer(types[rand.nextInt(5)]);
				
				/*
				OneFromAllDialogue tempDialogue = new OneFromAllDialogue(this, true);
				tempDialogue.setVisible(true);
				takeRessoureceFromPlayer(tempDialogue.getPick());
				*/
			}
		}
		bottomControl.getControlInforPanel().nextPlayerHand();
		updatePlayerPanels();
		validate();
	}
	
	public void updatePlayerPanels() {
		for (PlayerPanel p : playerpanels) {
			p.updateValues();
		}
	}
	/**
	 * Creates a Traderequest from the active Player to thew reciver.
	 */
	public void createTrade(Player reciver, Trade trade) {
		if (!reciver.isKI()) {
			TradeDialogue dialogue = null;
			if  (!getActivePlayer().isKI()) {
				dialogue = new TradeDialogue(this, true, reciver);
				dialogue.setVisible(true);
			} else {
				dialogue = new TradeDialogue(this, true, reciver, trade);
				dialogue.setPick();
				System.out.println("After Dialogue");
			}
			if (!dialogue.getCancled()) {
				reciver.reciveTradeReques(getActivePlayer(), dialogue);
			}
			if (!dialogue.getAccepted() && getActivePlayer().isKI()) {
				System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<NOT ACCEPTED.");
				AgentMessage returnMessage = new AgentMessage(trade.getSender());
				trade.setCounterOffer(false);
				returnMessage.setData(trade);
				agentTurn(returnMessage);
			}
		} else {
			TradeDialogue dialogue = null;
			if (!getActivePlayer().isKI()) {
				dialogue = new TradeDialogue(this, true, reciver);
				dialogue.setVisible(true);
			}
			if (!dialogue.getCancled()) {
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>TradeTO AGENT.");
				trade = new Trade(ColorUtils.colorToString(getActivePlayer().getColor())
					,ColorUtils.colorToString(reciver.getColor()), dialogue.getRequestArray(), dialogue.getOfferArray());
				AgentMessage returnMessage = new AgentMessage(trade.getReciver());
				trade.setCounterOffer(false);
				returnMessage.setData(trade);
				agentTurn(returnMessage);
			}
		}
		updatePlayerPanels();
	}
	
	private void distributeMostKnightsCard () {
		Player tempGotKnights = null;
		Player tempMostKnights = null;
		for(Player p : player) {
			if (p.getHasMostKnights()) {
				tempGotKnights = p;
			}
			//if p has more than the current leading person and over two  or  if most knights is not set and p got over two
			if ((tempMostKnights != null && p.getKnightCount() > 2 && p.getKnightCount() > tempMostKnights.getKnightCount()) || tempMostKnights == null && p.getKnightCount() > 2) {
				tempMostKnights = p;
			}	
		}
		if (tempGotKnights != null && tempMostKnights != null && !tempGotKnights.equals(tempMostKnights)) {
			tempGotKnights.setHasMostKnights(!tempGotKnights.getHasMostKnights()); //Does not have it, will have it.
			tempMostKnights.setHasMostKnights(!tempMostKnights.getHasMostKnights()); // He has it the negation will take it.
		} else if (tempGotKnights == null && tempMostKnights != null) {
			tempMostKnights.setHasMostKnights(!tempMostKnights.getHasMostKnights());
		}
	}
	
	public void stealRessources(List<Player> player) {
		ThievePickAPlayerDialogue tempDialogue = new ThievePickAPlayerDialogue(this, true, player);
		tempDialogue.setVisible(true);
		Player choosenPlayer = tempDialogue.getChoosenPlayer();
		stealRessources(choosenPlayer);
	}
	
	public void stealRessources(Player player) {
		TradingUtils.takeRessourceFromPlayer(getActivePlayer(), player);
		updatePlayerPanels();
	}
	
	public void discardRessourcesToThieves() {
		List<String> reciversTemp = new ArrayList<String>();
		for (Player p : player) {
			//List is used here as the order does not matter, only the amount of ressources is relevant
			if (TradingUtils.createShuffleList(p).size() > 7) {
				if (!p.isKI()) {
					DiscardCardsToThievesDialogue tempDialogue = new DiscardCardsToThievesDialogue(p, true, this);
					tempDialogue.setVisible(true);
					p.reduceRessourcesByEnum(tempDialogue.getToDiscard());
				
				} else {
					reciversTemp.add(getPlayerAgentAsReciver(p));
				}
			}
		}
		System.out.println("SOO we got in.... but run late seems broken for this");
		for (String s : reciversTemp) {
			AgentMessage message = new AgentMessage(s);
			message.setAction(AgentActionSettler.DISCARD_RESSOURCES);

					thieveDiscard = true;
					System.out.println("Starting discrad: --------------------------------------------------------------------------------------------------------");
					agentTurn(message);
					System.out.println("Somehow done with discard.------------------------------------------------------------------------------------------------------");
					thieveDiscard = false;
					updatePlayerPanels();
		}
		updatePlayerPanels();
	}
	
	public ControlFrame getBottomControl() {
		return bottomControl;
	}
	
	@Override
	public void boardEventHappened(BoardEvent event) {
		switch (event.getEventType()) {
		case THIEVES_PLACED:
			List<Player> tempList = getBoard().getAdjecentPlayers((LandTile)event.getEventSource());
			
			discardRessourcesToThieves();
			

			System.out.println("Did we pass the discard?----------------------------------------------------------------------------------------------------------");
			if (tempList.size() > 0 ) {
				if (!getActivePlayer().isKI()) {
					stealRessources(tempList);
				} else {
					agentTurn(new AgentMessage(getPlayerAgentAsReciver(getActivePlayer()), AgentActionSettler.STEAL_RESSOURCES));
				}

			}
			
			setStage(previousStage);
			//Here we place the thieve effect.
			//FIRST OF REDUCE HANDCARDS FROM 7 DOWN BY A HALF ROUNDED DOWN
			//Pick a possible player by dialoge Player choosenPlayer = new CustomDialogue
			//ThivePickAPlayerDialogue diag = new ThivePickAPlayerDialogue();
			break;
		case PIECE_PLACED_SUCCESSFULLY:
			//This would cause agents to go backwards after placing a pieces as they dont change the Gamestate into one of the building once.
			if (!getActivePlayer().isKI()) {
				setStage(previousStage);
			}
			System.out.println("The Street got Placed. Reset Stage to: " + getStage());
			break;
		case RESSOURCES_DISTRIBUTED:
			updatePlayerPanels();
			leftPanel.repaint();
			break;
		default:
			break;
		}	
	}
	
	public Player colorStringToPlayer(String color) {
		Player player = null;
		
		for (Player p : this.player) {
			if (ColorUtils.colorToString(p.getColor()).equals(color)) {
				player = p;
			}
		}
		
		return player;
		
	}
}