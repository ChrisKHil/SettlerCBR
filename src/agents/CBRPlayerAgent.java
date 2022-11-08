package agents;

import java.awt.Color;
import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


import CaseBase.DiscardCardsThiefCB;
import CaseBase.FirstMoveCB;
import CaseBase.NextMoveCB;
import CaseBase.TradeCB;
import Util.AgentUtils;
import Util.ColorUtils;
import Util.MainFrameWrapper;
import Util.ResourceProbabilityPair;
import Util.Trade;
import Util.TradingUtils;
import enums.CardEffect;
import enums.LandType;
import enums.TurnStage;
import frames.MainFrame;
import jade.core.AID;
import jade.core.Agent;
import jade.core.Node;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import pieces.Card;
import pieces.CityNode;
import pieces.StreetNode;
import player.Player;
import tiles.LandTile;
import tiles.Tile;

public class CBRPlayerAgent extends Agent {
	
	private String name;
	
	private List<AgentActionSettler> nextActions;
	
	private ACLMessage gateWayMessage;
	
	private boolean playerTrading;
	
	private List<Player> possbileTradingPartner;
	
	private MainFrameWrapper frame;
	
	private boolean didDiscard;
	/**
	 * If there are multiply possible players, the agent needs to make a trade request to each possible player before increasing the offer
	 */
	private int tradeNumber;
	/**
	 * Keeps track of the max. amount of cards to be offered to the other players.
	 */
	private int maxOfferNumber;
	
	private List<Node> planedNodes;
	
	@Override
	protected void setup() {
		super.setup();
	
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("PlayerAgent");
		name =(String)getArguments()[0];
		//Here may the color be an appropriate way.
		sd.setName((String)getArguments()[0]);
		
		playerTrading = false;
		didDiscard = false;
		this.frame = new MainFrameWrapper((MainFrame)getArguments()[1]);
		nextActions = new ArrayList<AgentActionSettler>();
		
		
		dfd.addServices(sd);
		
		setEnabledO2ACommunication(true, 0);
		
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			System.out.println(e.getLocalizedMessage());
		}
		this.addBehaviour(new CyclicBehaviour() {
			//Here we handle the first Turn of an Agent
			//Distinction between start and everyone else, as start will be called by game itself.
			@Override
			public void action() {
				ACLMessage aclmsg = receive();
				System.out.println(">>>>>>>>>>>>>>>>>>> The Agent  "+ name +" got activated at STAGE: " + frame.getTurnStage() 
				+ " with Object value of " + (aclmsg != null));
				if (aclmsg != null && (aclmsg.getPerformative() == ACLMessage.PROPAGATE)) {
					try {
						if (aclmsg.getContentObject() != null && aclmsg.getContentObject() instanceof AgentMessage 
								&& ((AgentMessage)aclmsg.getContentObject()).getData() != null 
								&&  (((AgentMessage)aclmsg.getContentObject()).getData()) instanceof Trade) {
							try {
								tradeWithHuman(aclmsg);
								System.out.println("------------------------------------------------ Did Player OFFER");
							} catch (IOException e) {
								e.printStackTrace();
							}
						} else {
							handlePlayerTurnbehavior(aclmsg);
						}
					} catch (UnreadableException e) {
						System.out.println("Error return to ususal Turn behavior.");
						handlePlayerTurnbehavior(aclmsg);
						e.printStackTrace();
					}
					block();
				} else if (aclmsg != null && aclmsg.getPerformative() == ACLMessage.REQUEST) {
					handlePlayerTradeRequest(aclmsg);
				} else {
					System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>> The Agent "+ name +" Got a Blocked Cyclic Behaviro");
					block();
				}
			}
		});
	}
	
	private void handlePlayerTurnbehavior(ACLMessage aclmsg) {
		AgentMessage message = null;
		try {
			message = (AgentMessage)aclmsg.getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
		if (frame.getTurnStage() == TurnStage.FIRST_TURN || frame.getTurnStage() == TurnStage.SECOND_TURN) {
			firstTurns(message);
		} else {
			switch (frame.getTurnStage()) {
			case ROLL_DICE:
				rollDie(message);
				break;
			case CHANGE_THIEVES:
				changeThives(message);
				break;
			case TRADE:
				gateWayMessage = aclmsg;
				tradePanel(message);
				break;
			case BUILD:
				buildPhase(message);
				break;
			default:
				break;
			}
		}
		//Dont do this while negotiating with another player -> Game should stay paused till this is done
		if (!playerTrading) {
			ACLMessage reply = aclmsg.createReply();
			reply.setPerformative(ACLMessage.REQUEST);
			try {
				System.out.println("Message bevoe Settin Action; " + message.getAction()); 
				reply.setContentObject(message);
			} catch (IOException e) {
				System.out.println("------------------------------------------------------------------------Message: " + message.getData().getClass() + " ACTION: " + message.getAction());
				e.printStackTrace();
			}
			send(reply);
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>> The Agent "+ name +" finnished behavior properly  CYCLE: ");
		}
	}
	
	private void handlePlayerTradeRequest(ACLMessage message) {
		try {
			Trade trade = (Trade)message.getContentObject();
			boolean isSender = trade.getSender().equals(name);
			acceptTrade(trade);
			
			//Otherwise the sender will send a reply to the gateway agent. This way the game will execute the trade.
			if (trade.isAccepted()) {
				System.out.println("-Trade got accepted.");
				if (isSender) {
					System.out.println("-The Sender is getting back to the gateway.");
					ACLMessage reply = gateWayMessage.createReply();
					reply.setPerformative(ACLMessage.PROPAGATE);
					AgentMessage agentMessage = (AgentMessage)gateWayMessage.getContentObject();
					agentMessage.setAction(AgentActionSettler.TRADE_WITH_PLAYER);
					agentMessage.setData(trade);
					reply.setContentObject(agentMessage);
					
					send(reply);
					playerTrading = false;
				} else {
					System.out.println(name + "is resending Offer wiht acceptance");
					ACLMessage accreply = message.createReply();
					accreply.setPerformative(ACLMessage.REQUEST);
					accreply.setContentObject(trade);
					
					send(accreply);
				}
			} else {
				//deal with an unaccepted trade different for sender and reciver.
				if (isSender) {
					if ((TradingUtils.sumOfRessources(trade.getOfferd()) + 1) <= maxOfferNumber) {
						updateOffer(trade);
						System.out.println("-Agent resending the offer.");
						ACLMessage newTradeMessage = new ACLMessage(ACLMessage.REQUEST);
						System.out.println("The Reciver of this message: " + trade.getReciver());
						newTradeMessage.addReceiver(new AID(trade.getReciver(),AID.ISLOCALNAME));
						
						newTradeMessage.setContentObject(trade);
						
						send(newTradeMessage);
					}	else {
						playerTrading = false;
						AgentMessage messageBank = (AgentMessage)gateWayMessage.getContentObject();
						bankTrading(messageBank, trade.getRequested());
						System.out.println("AFTER BANK TRADING: " + messageBank.getAction());
						ACLMessage gateWayReply = gateWayMessage.createReply();
						gateWayReply.setContentObject(messageBank);
						send(gateWayReply);
					}
				} else {
					//At this point the trade is accepted, if the agent is not the initiator he will send back the accepted trade.
					if (!isSender) {
						System.out.println("-Is sending back a confimation.");
						ACLMessage reply = message.createReply();
						System.out.println("Sender: " +  message.getSender() + "Reply target: " + reply.getAllReceiver().toString());
						reply.setPerformative(ACLMessage.REQUEST);
						reply.setContentObject(trade);
						
						send(reply);
					}							
				}
			}
			//Evalute the recived tradeoffers
		} catch (UnreadableException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void acceptTrade (Trade trade) throws UnreadableException {
		boolean isSender = trade.getSender().equals(name);
		System.out.println("-Trade Sender: " +  trade.getSender() + " Agent Name: " + name + " is the sender: " + isSender);
		if (isSender) {
			determineNextAction(frame.getActivePlayer());	
		} else {
			System.out.println("Trading Reciver: " + trade.getReciver());
			determineNextAction(frame.getPlayerByColor(trade.getReciver()));
		}
		if (canDoTheTrade(trade.getRequested(), trade.getOfferd(), isSender)) {
			System.out.println("-Player can do the trade.");
			if (shouldDoTheTradeCBR(trade.getRequested(), trade.getOfferd(), isSender)) {
				System.out.println("-Player should do the trade.");
				if (!isSender) {
					trade.setAccepted(true);
				} else if (trade.isCounterOffer()) {
					trade.setAccepted(true);
				}
			}
		}
	}
	
	private void tradeWithHuman(ACLMessage message) throws UnreadableException, IOException {
		AgentMessage aMessage = (AgentMessage) message.getContentObject();
		Trade trade = (Trade)aMessage.getData();
		boolean isSender = trade.getSender().equals(name);
		acceptTrade(trade);
		if (trade.isAccepted()) {
			System.out.println("ACCEPTED");
			ACLMessage returnMessage = message.createReply();
			aMessage.setAction(AgentActionSettler.TRADE_HUMAN_ACCEPTED);
			aMessage.setData(trade);
			
			returnMessage.setContentObject(aMessage);					
			send(returnMessage);
		} else {
			if (isSender) {
				if ((TradingUtils.sumOfRessources(trade.getOfferd()) + 1) <= maxOfferNumber) {
					updateOffer(trade);
					System.out.println("-Agent resending the offer.");
					TradingUtils.pritPrintResourceArray(trade.getOfferd());
					TradingUtils.pritPrintResourceArray(trade.getRequested());
					System.out.println("sdaffd");
					ACLMessage returnMessage = message.createReply();
					aMessage.setAction(AgentActionSettler.TRADE_WITH_HUMAN);
					aMessage.setData(trade);
					
					returnMessage.setContentObject(aMessage);					
					send(returnMessage);
				}	else {
					playerTrading = false;
					AgentMessage messageBank = (AgentMessage)gateWayMessage.getContentObject();
					bankTrading(messageBank, trade.getRequested());
					System.out.println("AFTER BANK TRADING: " + messageBank.getAction());
					ACLMessage gateWayReply = gateWayMessage.createReply();
					gateWayReply.setContentObject(messageBank);
					send(gateWayReply);
				}
			} else {
				System.out.println("Is not SENDER");
				ACLMessage returnMessage = message.createReply();
				aMessage.setAction(AgentActionSettler.TRADE_HUMAN_DENIED);
				aMessage.setData(trade);
				
				returnMessage.setContentObject(aMessage);					
				send(returnMessage);
			}
		}
		
	}
	
	private void firstTurns(AgentMessage message) {
		if (frame.firstTurnTownBuild()) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>> The Agent "+ name +" is placing a street");
			//Here we build some street
			List<StreetNode> streets = frame.getFirstTurnBuildableStreets();
			message.setAction(AgentActionSettler.PLACE_FIRST_TURN_STREET);

			message.setData(streets.get(AgentUtils.randomChoice(streets.size())));

		} else {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>> The Agent "+ name +" is placing a town ");
			List<CityNode> possibleCities = frame.getBuildableCityNodesFirst();
			CityNode c = null;
			if (frame.getTurnStage() == TurnStage.SECOND_TURN) {
				ResourceProbabilityPair pair = frame.getResourceProbabilityCombi(frame.getPlayerByColor(name));
				System.out.println("Name: " + name.substring(0,1));
				String solution = "";
				//Neue Methode, die unseren Agenten die Probabilities besser macht.
				if(name.equals("Blue")) {
					solution = FirstMoveCB.agentQueryNEW(pair.getResources(), pair.getProbabilities(), name.substring(0,1));
				} else {
					solution = FirstMoveCB.agentQuery(pair.getResources(), pair.getProbabilities(), name.substring(0,1));
				}
				System.out.println("Solution: " + solution);
				if(name.equals("Blue")) {
					c = frame.findFittingNodeNEW(solution);
				} else {
					c = frame.findFittingNode(solution);
				}
			}
			if (c == null) {
				//Delibarate which city.
				//Spielt unser neuer Agent, so wird die neue CityNodePreference Methode aufgerufen.
				if(name.equals("Blue")) {
					possibleCities.sort((CityNode n, CityNode b) -> calculateCityNodePreferenceNEW(n) - calculateCityNodePreferenceNEW(b) );
				} else {
					possibleCities.sort((CityNode n, CityNode b) -> calculateCityNodePreference(n) - calculateCityNodePreference(b) );
				}
				c = possibleCities.get(0);
			}
			message.setAction(AgentActionSettler.PLACE_FIRST_TURN_TOWN);
			message.setData(c);
		}
	}
	
	/**
	 * To update the offer on reciver site in case of an inadequate offer.
	 */
	private void updateOffer (Trade trade) {
		System.out.println("-Updating tradeNumber");
		tradeNumber++;
		if (tradeNumber >= possbileTradingPartner.size()) {
			System.out.println("-Tradenumber too big, reset and increase offer.");
			tradeNumber = 0;
			increaseOffer(trade);
			trade.setReciver(ColorUtils.colorToString(possbileTradingPartner.get(tradeNumber).getColor()));
		} else {
			trade.setReciver(ColorUtils.colorToString(possbileTradingPartner.get(tradeNumber).getColor()));
		}
		System.out.println("--Offer updated, resend offer to: " + trade.getReciver());
	}
	
	private void initialiseBankTrade(AgentMessage message, int[] resourceToTradeFor) {
		System.out.println("----Agent is banktrading.");
		if (TradingUtils.canBanktrade(frame.getActivePlayer().getRestRessources(AgentUtils.actionToPriceArray(nextActions.get(0))), maxOfferNumber)) {
			System.out.println("Can Trade with the bank");
			message.setAction(AgentActionSettler.TRADE_WITH_BANK);
			message.setData(new Trade("non", "non", resourceToTradeFor,
					TradingUtils.createBankTradingArray(frame.getActivePlayer().getRestRessources(AgentUtils.actionToPriceArray(nextActions.get(0)))
							, frame.getActivePlayer().getHarbortyesAsArray())));
		} else {
			message.setAction(AgentActionSettler.ADVANCE_TURN);
		}
		System.out.println("----Message after init Bank: " + message.getAction() + " With Max Offer : " + maxOfferNumber); 
	}
	
	private void increaseOffer(Trade trade) {
		int[] currentOffer = trade.getOfferd();
		int offercount = TradingUtils.sumOfRessources(currentOffer);
		//We create a new offer with the same cardcount but different resources 
		offercount++;
		int[] newOffer = createOfferArray(offercount);
		trade.setOfferd(newOffer);
	}
	
	private void rollDie(AgentMessage message) {
		if (frame.didRollDie()) {
			System.out.println("Agend Did Roll and will advance");
			message.setAction(AgentActionSettler.ADVANCE_TURN);
		} else {
			message.setAction(AgentActionSettler.ROLL_DIE);
		}
	}
	
	
	/**
	 * The parameter get set initially and will be reset them before releasing the command.
	 * @param message
	 */
	private void tradePanel(AgentMessage message) {
		boolean doTrade = true;
		tradeNumber = 0;
		//First add a next action if there is none
		determineNextAction(frame.getActivePlayer());
		System.out.println("-Agent Next actions size: " + nextActions.size());
		if (nextActions.size() > 0) {
			//Determines if the next action can be done with the current ressources
			doTrade = AgentUtils.shouldTrade(nextActions.get(0), frame.getActivePlayer());
		}
		//here we assume that we at least can trade  Needed resources is only positive if there is still something needed.
		if ((AgentUtils.sumOfRessources(frame.getActivePlayer().getRestRessources(AgentUtils.actionToPriceArray(nextActions.get(0)))) < 1)) {
			System.out.println("--The agent lacks the ressouce count to trade.");
			doTrade = false;
		}
		if (doTrade) {
			System.out.println("--Agent should Trade.");
			//this array should only have a single 1 and every other number is 0 indicating the needed resouce
			int[] tradeArray = determineWhatToTradeFor(frame.getActivePlayer().getNeededRessources(AgentUtils.actionToPriceArray(nextActions.get(0))));
			maxOfferNumber = determineMaxOffer(); //should not offer more cards
			//look at which player has the resource needed
			possbileTradingPartner = AgentUtils.playerToTradeWith(frame.getActivePlayer(), frame.getAllPlayer(), tradeArray);
			//Only keep going if there is the possibility to event trade with a player. DISABLED FOR TESTING
			if (possbileTradingPartner.size() > 0) {
				System.out.println("---Agent has trading partner.");
				//TODO: order the available players descending according to optimal results (other player should gain as little as possible?)
				
				//determine the offer -> Max offer should be the upper bound for a tradeoffer as it should not be higher as the bank trade 		
				//At this point it will always be the first request of the turn
				int[] offerArray = createOfferArray(1);
				System.out.println("---Agent has a offerArray at max count of: " + maxOfferNumber);
				TradingUtils.pritPrintResourceArray(offerArray);
				if (TradingUtils.sumOfRessources(offerArray) <= maxOfferNumber) {
					System.out.println("---Agent can make a compelling offer to a player.");
					//Trade with a player/ Try to do so
					playerTrading = true;
					makeOffer(possbileTradingPartner, offerArray, tradeArray, message);
				} else {
					playerTrading = false;
					bankTrading(message, tradeArray);
				}
			} else {
				bankTrading(message, tradeArray);
			}
		}
		if (!doTrade) {
			System.out.println("-Agent should  not Trade.");
			message.setAction(AgentActionSettler.ADVANCE_TURN);
		}
	}
	
	private void bankTrading(AgentMessage message, int[] resourceToTradeFor) {
		System.out.println("---Agent tries banktrading.");
		System.out.println("---MAXOFFER: " + maxOfferNumber);
		//initiate Trading with a bank keep in mind that it may still be impossible stop trading in that case
		initialiseBankTrade(message, resourceToTradeFor);	
	}
	
	private void makeOffer(List<Player> possiblePartner, int[] offerArray, int[] requestArray, AgentMessage message) {
		Player tradingTarget = possiblePartner.get(0);
		Trade trade = new Trade(ColorUtils.colorToString(frame.getActivePlayer().getColor()),ColorUtils.colorToString(tradingTarget.getColor())
				,requestArray, offerArray);
		if (tradingTarget.isKI()) {
			ACLMessage tradeRequest = new ACLMessage(ACLMessage.REQUEST);
			try {
				tradeRequest.setContentObject(trade);
			} catch (IOException e) {
				e.printStackTrace();
			}	
			tradeRequest.addReceiver(new AID(trade.getReciver(), AID.ISLOCALNAME));
			send(tradeRequest);
		} else {
			//Reenable UI
			playerTrading = false;
			
			message.setAction(AgentActionSettler.TRADE_WITH_HUMAN);
			message.setData(trade);
		}
	}
	
	/**
	 * Returns an resource array to create a trade with, only containing one unit of one resource
	 * Due to the nature of the trade system in this programm.
	 * @return
	 */
	private int[] determineWhatToTradeFor(int[] restArray) {
		int[] tradeArray = new int[5];
		boolean foundResource = false;
		for (int i = 0; i < 5; i++) {
			if (!foundResource) {
				if (restArray[i] > 0) {
					tradeArray[i] = 1;
					foundResource = true;
				} else {
					tradeArray[i] = 0;
				}
			}
		}
		return tradeArray;
	}
	
	/**
	 * If the agent should trade. The part concerning the sender is preparation in case of implementation of counter offers.
	 * @param requestArray
	 * @param offerArray
	 * @param sender
	 * @return
	 */
	private boolean shouldDoTheTrade(int[] requestArray, int[] offerArray, boolean sender) {
		boolean shouldTrade = false;
		
		if(sender) {
			//Should consider to banktrade if equal amount of resources if needed
			if (TradingUtils.sumOfRessources(offerArray) <= determineMaxOffer()) {
				shouldTrade = true;
			}
		} else {
			if (TradingUtils.isNeededOffer(offerArray, frame.getPlayerByColor(name).getNeededRessources(AgentUtils.actionToPriceArray(nextActions.get(0))))) {
				shouldTrade = true;
			} else if (TradingUtils.sumOfRessources(offerArray) >= 2)  {
				shouldTrade = true;
			}
		}
		return shouldTrade;
	}
	
	private boolean shouldDoTheTradeCBR(int[] requestArray, int[] offerArray, boolean isSender) {
		boolean shouldTrade = false;
		System.out.println("CHECK CBR FOR TRADING");
		System.out.println("NAME_: " + name.substring(0,1) +  " Name: " + name);
		try {
 			shouldTrade = TradeCB.agentQuery(offerArray
					, frame.getPlayerByColor(name).getNeededRessources(AgentUtils.actionToPriceArray(nextActions.get(0)))
					, name.substring(0,1));
		} catch (Exception e) {
			System.out.println("Error Trading Cases using non CBR Ruling.");
			if (isSender) {
				shouldTrade = shouldDoTheTrade(requestArray, offerArray, isSender);
			} else {
				shouldTrade = shouldDoTheTrade(requestArray, offerArray, isSender);
			}			
		}	
		return shouldTrade;
	}
	
	/**
	 * Determines if a trade can be executed given the next action of the agent.
	 * @param requestArray The array containing the request.
	 * @param offerArray The array containing the offer.
	 * @param sender The sender of trade.
	 * @return If the trade is possible.
	 */
	private boolean canDoTheTrade(int[] requestArray, int[]offerArray, boolean sender) {
		boolean canTrade = false;
		int[] restArray = new int[5];
		if (sender) {
			restArray = TradingUtils.subtractPriceArrays(frame.getActivePlayer().getRestRessources(AgentUtils.actionToPriceArray(nextActions.get(0))), offerArray);
		} else {
			restArray = TradingUtils.subtractPriceArrays(frame.getActivePlayer().getRestRessources(AgentUtils.actionToPriceArray(nextActions.get(0))), requestArray);
		}
		if (!TradingUtils.containsNegativeResources(restArray)) {
			canTrade = true;
		}
		return canTrade;
	}	
	
	/**
	 * Here we filter what the Player can do if we ignore the resources and thus what to trade for.
	 */
	private void determineNextAction(Player player) {
		System.out.println("Player Null: " + (player == null) + "color values: " + player.getColor());
		playerSetPlan();
		System.out.println("-Agent " + ColorUtils.colorToString(player.getColor()) +  " is determining next action.");
		if (nextActions.size() == 0) {
			if (frame.getBuildableTowns().size() > 0) {
				System.out.println("--Agent is determined to built a town.");
				nextActions.add(AgentActionSettler.PLACE_TOWN);
			}
			if (AgentUtils.sumOfRessources(frame.getActivePlayer().getNeededRessources(TradingUtils.getCityPriceArray())) <= 2) {
				if (frame.getActivePlayer().getColor().getBlue() == 255) {
					if(frame.getBuilalbeCitys().size() > 0) {
						System.out.println("--Agent is determined to built a city.");
						nextActions.add(AgentActionSettler.PLACE_CITY);
					} else {
						System.out.println("--Agent is determined to built a street.");
						nextActions.add(AgentActionSettler.PLACE_STREET);
					}
				} else {
					System.out.println("--Agent is determined to built a city.");
					nextActions.add(AgentActionSettler.PLACE_CITY);
				}
			} else {
				System.out.println("--Agent is determined to built a street.");
				nextActions.add(AgentActionSettler.PLACE_STREET);
			}
		}
	}
	
	private void playerSetPlan() {
		int[] pieces = {frame.getPlayerByColor(name).getPlacedTownPieces().size(), frame.getPlayerByColor(name).getPlacedCityPieces().size()};
		String solution = NextMoveCB.agentQuery(name.substring(0,1), pieces, frame.getPlayerByColor(name).toRessourceArray());
		System.out.println(solution);
		
	}
	
	
	/**
	 * Creates an offer array with possible offers to another player.
	 * @param numberOfRequests The number of requests already made.
	 * @return A request array dependent on the already made number of requests.
	 */
	private int[] createOfferArray(int numberOfRequests) {
		int[] offers = new int[5];
		Random rand = new Random();
		int counter = 0;
		while (counter < numberOfRequests) {
			int temp = rand.nextInt(5);
			//if we got resources of the type after doing our next move
			System.out.println("trying offer: " + temp);
			if (frame.getActivePlayer().getRestRessources(AgentUtils.actionToPriceArray(nextActions.get(0)))[temp] > 0) {
				offers[temp]++;
				counter++;
			}
		}
		return offers;
	}
	
	private int determineMaxOffer() {
		int[] harbourTypeArray = frame.getActivePlayer().getHarbortyesAsArray();
		int[] restArray = frame.getActivePlayer().getRestRessources(AgentUtils.actionToPriceArray(nextActions.get(0)));
		int max = 4; 
		for (int i = 0; i < 5; i++) {
			if (restArray[i] >= harbourTypeArray[i]) {
				if (max > harbourTypeArray[i]) {
					max = harbourTypeArray[i];
				}
			}
		}
		return max;
	}
	
	private void buildPhase(AgentMessage message) {
		//As there is no need for Data in ADVANCE TURN it will just pass the turn
		//Same holds for buying a card as the only data is the desire to buy one
		delibareteBuildPhaseAction(message);
		
		switch (message.getAction()) {
		case PLACE_CITY:
			List<CityNode> possibleCities = frame.getBuilalbeCitys();
			message.setData(possibleCities.get(AgentUtils.randomChoice(possibleCities.size())));
			break;
		case PLACE_TOWN:
			List<CityNode> possibleTowns = frame.getBuildableTowns();
			message.setData(possibleTowns.get(AgentUtils.randomChoice(possibleTowns.size())));
			break;
		case PLACE_STREET:
			List<StreetNode> possibleStreets = frame.getBuildableStreetNodes();
			System.out.println("The SIZE OF THE STREETS: " + possibleStreets.size());
			message.setData(possibleStreets.get(AgentUtils.randomChoice(possibleStreets.size())));
			break;
		default:
			break;
		}
	}
	
	private void delibareteBuildPhaseAction(AgentMessage message) {
		//Usually, set it to advance and then check for better option, replace if found
		//Thus there will be a default action if something else breaks
		//TODO: Doing CBR call evalutating what to do. Currently randomly choosen option.
		List<AgentActionSettler> choices = possibleAgentActionsBuildPhase();
		message.setAction(choices.get(AgentUtils.randomChoice(choices.size())));
		
	}
	/**
	 * Method to Veryfie Agent behavior, will return possible actions at build phase.
	 * Multiples are to skew the chance favorable for expansion.
	 * @return All Action possible in Build Phase for the active Player
	 */
	private List<AgentActionSettler> possibleAgentActionsBuildPhase() {
		List<AgentActionSettler> tempActions = new ArrayList<AgentActionSettler>();
		tempActions.add(AgentActionSettler.ADVANCE_TURN);
		if (frame.getActivePlayer().getColor().getBlue() == 255) {
			if (frame.getActivePlayer().canBuildCity() && frame.getBuilalbeCitys().size() > 0) {
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> The Agent can build CITYS");
				tempActions.add(AgentActionSettler.PLACE_CITY);
				tempActions.add(AgentActionSettler.PLACE_CITY);
				tempActions.add(AgentActionSettler.PLACE_CITY);
				tempActions.add(AgentActionSettler.PLACE_CITY);
			}
			if (frame.getActivePlayer().canBuildStreet() && frame.getBuildableStreetNodes().size() > 0) {
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> The Agent can build STREETS");
				tempActions.add(AgentActionSettler.PLACE_STREET);
				tempActions.add(AgentActionSettler.PLACE_STREET);
			}
			if (frame.getActivePlayer().canBuildTown() && frame.getBuildableTowns().size() > 0) {
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> The Agent can build TOWNS");
				tempActions.add(AgentActionSettler.PLACE_TOWN);
				tempActions.add(AgentActionSettler.PLACE_TOWN);
				tempActions.add(AgentActionSettler.PLACE_TOWN);
				tempActions.add(AgentActionSettler.PLACE_TOWN);
				tempActions.add(AgentActionSettler.PLACE_TOWN);
			}
			if (frame.getActivePlayer().canBuyCard()) {
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> The Agent can BUY CARDS");
				tempActions.add(AgentActionSettler.BUY_CARD);
				tempActions.add(AgentActionSettler.BUY_CARD);
			}		
		} else {
			if (frame.getActivePlayer().canBuildCity() && frame.getBuilalbeCitys().size() > 0) {
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> The Agent can build CITYS");
				tempActions.add(AgentActionSettler.PLACE_CITY);
				tempActions.add(AgentActionSettler.PLACE_CITY);
				tempActions.add(AgentActionSettler.PLACE_CITY);
			}
			if (frame.getActivePlayer().canBuildStreet() && frame.getBuildableStreetNodes().size() > 0) {
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> The Agent can build STREETS");
				tempActions.add(AgentActionSettler.PLACE_STREET);
				tempActions.add(AgentActionSettler.PLACE_STREET);
			}
			if (frame.getActivePlayer().canBuildTown() && frame.getBuildableTowns().size() > 0) {
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> The Agent can build TOWNS");
				tempActions.add(AgentActionSettler.PLACE_TOWN);
				tempActions.add(AgentActionSettler.PLACE_TOWN);
				tempActions.add(AgentActionSettler.PLACE_TOWN);
				tempActions.add(AgentActionSettler.PLACE_TOWN);
				tempActions.add(AgentActionSettler.PLACE_TOWN);
				tempActions.add(AgentActionSettler.PLACE_TOWN);
			}
			if (frame.getActivePlayer().canBuyCard()) {
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> The Agent can BUY CARDS");
				tempActions.add(AgentActionSettler.BUY_CARD);
			}
		}
		return tempActions;
	}
	
	private void changeThives(AgentMessage message) {
		if (message.getAction() == null) {
			//Deliberate where to put them.
			message.setAction(AgentActionSettler.CHANGE_THIEVES);
			System.out.println("Changing Thives Agent Decision");
			//TODO:
			List<LandTile> tiles = frame.getAllEnemyTiles();
			//Unser Agent blockt nicht seine eigenen Felder
			
			if(name.equals("Blue")) {
				LandTile toBlock = tiles.get(AgentUtils.randomChoice(tiles.size()));
				message.setData(new Point(toBlock.getX(),toBlock.getY()));
			} else {
				message.setData(new Point(tiles.get(AgentUtils.randomChoice(tiles.size())).getX(),tiles.get(AgentUtils.randomChoice(tiles.size())).getY()));
			}
		} else if (message.getAction() == AgentActionSettler.DISCARD_RESSOURCES) {
			discardLandcards(message);
			didDiscard = true;
		} else if (message.getAction() == AgentActionSettler.STEAL_RESSOURCES) {
			pickPlayerToStealFrom(message);
		}
	}
	
	private void discardLandcards(AgentMessage message) {
		Player p  = frame.getPlayerByColor(name);
		//Determines number of landcards to discard
		int numberToDiscard = TradingUtils.createShuffleList(p).size() / 2;
		//DO some delibarateion about what to pic
		determineNextAction(frame.getPlayerByColor(name));
		String solution = "";
		if(name.equals("Blue")) {
			solution = DiscardCardsThiefCB.agentQuery(frame.getPlayerByColor(name).toRessourceArray()
					, frame.getPlayerByColor(name).getNeededRessourcesNEW(AgentUtils.actionToPriceArray(nextActions.get(0))), name.substring(0,1));
		} else {
			solution = DiscardCardsThiefCB.agentQuery(frame.getPlayerByColor(name).toRessourceArray()
					, frame.getPlayerByColor(name).getNeededRessources(AgentUtils.actionToPriceArray(nextActions.get(0))), name.substring(0,1));
		}
		
		
		List<LandType> toDiscard = new ArrayList<LandType>();
		if (!solution.equals("No case with a similarity of at least 0.7 could be found!")) {
			String[] solutionArray = solution.split(";");
			int[] toDiscardArray = new int[5];
			int[] ressourcesOwned = frame.getPlayerByColor(name).toRessourceArray();
			int[] discardDifference = new int[5];
			toDiscard = new ArrayList<LandType>();
			for (int i = 0 ; i < 5 ; i++){
				try {
					toDiscardArray[i] = Integer.parseInt(solutionArray[i]);
				} catch (NumberFormatException e) {
					System.out.println("Missed something: " + solutionArray[i]);
				}
			}
			int redistribute = 0;
			for (int i = 0 ; i < 5 ; i++){
				discardDifference[i] = ressourcesOwned[i] - toDiscardArray[i];
				if(discardDifference[i] < 0) {
					redistribute += (discardDifference[i] *-1);
					//Ressourcen, die der Spieler zu wenig hat um sie abzuwerfen, werden auch aus dem toDiscardArray entfernt. 
					toDiscardArray[i] += discardDifference[i];

				}
			}
			while(redistribute > 0) {
				int rand = AgentUtils.randomChoice(5);
				if (discardDifference[rand] > 0) {
					toDiscardArray[rand]++;
					discardDifference[rand]--;
					redistribute--;
				}
			}
			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < toDiscardArray[i]; j++) {
					//von j zu i geändert, Da j die Anzahl einer Ressource ist, nicht die Ressource selbst
					toDiscard.add(TradingUtils.getTypes()[i]);
				}
			}
			
			while(toDiscard.size() != numberToDiscard){
				if(toDiscard.size() < numberToDiscard) {
					toDiscard.removeAll(toDiscard);
					int rand = AgentUtils.randomChoice(5);
					if (discardDifference[rand] > 0) {
						toDiscardArray[rand]++;
						discardDifference[rand]--;
					}
					for (int i = 0; i < 5; i++) {
						for (int j = 0; j < toDiscardArray[i]; j++) {
							//von j zu i geändert, Da j die Anzahl einer Ressource ist, nicht die Ressource selbst
							toDiscard.add(TradingUtils.getTypes()[i]);
						}
					}
				} else {
					toDiscard.remove(0);
				}
			}
			
		} else {
			toDiscard = randomDiscardDebug(p, numberToDiscard);
		}
		message.setData(toDiscard);
	}
	
	private void pickPlayerToStealFrom (AgentMessage message) {
		Player p = randomPlayerChoiceDebug();
		message.setData(p);
	}
	
	private Player randomPlayerChoiceDebug () {
		Player p = null;
		
		int choice = AgentUtils.randomChoice(frame.getOtherPlayers(name).size());
		if (choice < frame.getOtherPlayers(name).size() && choice > 0 ) {
			choice--;
		}
		p = frame.getOtherPlayers(name).get(choice);
		return p;
	}
	
	private int calculateCityNodePreference(CityNode c){
		int pref = 0;
		for (Tile t : frame.getTilesByCity(c) ) {
			if (t instanceof LandTile) {
				pref += Math.abs(7 - ((LandTile)t).getNumber());
				if (((LandTile)t).getType() == LandType.STONE) {
					pref += 1;
				}
				if (((LandTile)t).getType() == LandType.CLAY || ((LandTile)t).getType() == LandType.LUMBER) {
					pref -= 1;
				}
			} else {
				//it is very bad to place a city wiht only one ore two resource fields and not having a harbour.
				pref += 7;
			}
		}
		
		switch (c.getHarbourType()) {
			case THREE_TO_ONE:
				//Offsetting the +7 for the harbourtile and an additional as better trades are nice.
				pref -= 1;
				break;
			case NONE:
				pref += 1;
				break;
			default:
				//Single resource trade is kind of better?
				pref -= 2;
				break;
		}
		return pref;
	}
	
	//Neue Methode für Güte der Nodes bestimmen. Wahrscheinlichkeit hat mehr Gewichtung und Desert und Harbor wird nahezu ausgeschlossen.
	private int calculateCityNodePreferenceNEW(CityNode c){
		int pref = 0;
		boolean gotClay = false;
		boolean gotLumber = false;
		for (Tile t : frame.getTilesByCity(c) ) {
			if (t instanceof LandTile) {
				pref += 3*(Math.abs(7 - ((LandTile)t).getNumber()));
				if (((LandTile)t).getType() == LandType.STONE) {
					pref += 1;
				}
				if (((LandTile)t).getType() == LandType.CLAY && !gotClay) {
					pref -= 10;
					gotClay = true;
				}
				if (((LandTile)t).getType() == LandType.LUMBER || !gotLumber) {
					pref -= 7;
					gotLumber = true;
				}
				if (((LandTile)t).getType() == LandType.DESERT) {
					pref += 100;
				}
			} else {
				//it is very bad to place a city wiht only one ore two resource fields and not having a harbour.
				pref += 100;
			}
		}
		
		/*switch (c.getHarbourType()) {
			case THREE_TO_ONE:
				//Offsetting the +7 for the harbourtile and an additional as better trades are nice.
				pref -= 1;
				break;
			case NONE:
				pref += 1;
				break;
			default:
				//Single resource trade is kind of better?
				pref -= 2;
				break;
		}*/
		return pref;
	}
	
	private List<LandType> randomDiscardDebug(Player p, int numberToDiscard){
		List<LandType> toDiscard = new ArrayList<LandType>();
		List<LandType> tempRessourceList = TradingUtils.createShuffleList(p);
		
		for (int i = 0; i < numberToDiscard; i++) {
			int choosenInt = AgentUtils.randomChoice(tempRessourceList.size());
			toDiscard.add(tempRessourceList.get(choosenInt));
			tempRessourceList.remove(choosenInt);
		}
		return toDiscard;
	}
	
	public void resetDiscarded() {
		didDiscard = false;
	}
	
	@Override
	protected void takeDown() {
		super.takeDown();
		
		try {
			DFService.deregister(this);
		} catch (FIPAException e) {
			System.out.println(e.getLocalizedMessage());
		}
	}
}

