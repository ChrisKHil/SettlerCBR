package frames;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import Util.BoardEvent;
import enums.BoardEvents;
import enums.TurnStage;
import listener.BoardListener;
import listener.RollDiceListener;
import listener.TurnChangeListener;

public class ControlFrame extends JPanel implements BoardListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 691994212805901702L;

	private MainFrame frame;
	
	private JButton turnEndButton;
	
	private JButton dieRollButton;
	
	private JButton buildTown;
	
	private JButton buildCity;
	
	private JButton buildStreet;
	
	private JButton buyCard;
	
	private JButton debugBoardTestButton;
	
	private JButton debugChangePlayerButton;
	
	private JButton debugPlaceTargetCity;
	
	private JButton activateCardButton;
	
	private JButton startButton;
	
	private JPanel buildPanel;
	
	private JPanel diePanel;
	
	private JLabel dieRollLabel;
	
	private JPanel startPanel;
	
	private JPanel turnEndCardPanel;
	
	private JPanel actionButtonPanel;
	
	private TradePanel tradePanel;
	
	private ControllInfoBoxPanel controlInforPanel;
	
	private final String ROLL_TRADE_PANEL = "ROLLTRADE";
	
	private final String BUILD_PANEL = "BUILD";
	
	private final String START_PANEL = "START";
	
	private final String TRADE_PANEL = "TRADE";
	
	public ControlFrame(MainFrame frame) {
		this.frame = frame;
		this.setLayout(new BorderLayout());
		init();
	}
	
	private void init() {

		buildPanel = new JPanel();
		buildPanel.setLayout(new GridLayout(2,2));
		buildPanel.setVisible(true);
		
		diePanel = new JPanel();
		diePanel.setLayout(new BorderLayout());
		diePanel.setVisible(true);
		
		startPanel = new JPanel();
		startPanel.setVisible(true);
		
		tradePanel = new TradePanel(frame);
		
		turnEndCardPanel = new JPanel();
		turnEndCardPanel.setLayout(new GridLayout(2,0,10,20));
		this.add(turnEndCardPanel, BorderLayout.EAST);
		
		activateCardButton = new JButton("Activate Card");
		activateCardButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//frame.freeStreedCard();
			}
		});
		turnEndCardPanel.add(activateCardButton);
		activateCardButton.setEnabled(false);
		
		actionButtonPanel = new JPanel();
		actionButtonPanel.setLayout(new CardLayout());
		actionButtonPanel.add(startPanel, START_PANEL);
		actionButtonPanel.add(diePanel, ROLL_TRADE_PANEL);
		actionButtonPanel.add(buildPanel, BUILD_PANEL);
		actionButtonPanel.add(tradePanel, TRADE_PANEL);
		this.add(actionButtonPanel,BorderLayout.WEST);
		
		turnEndButton = new JButton("Next");
		turnEndCardPanel.add(turnEndButton);
		turnEndButton.addActionListener(new TurnChangeListener(this));
		turnEndButton.setEnabled(false);		
		
		dieRollButton = new JButton("Roll dice");
		dieRollButton.addActionListener(new RollDiceListener(this));
		diePanel.add(dieRollButton, BorderLayout.WEST);
		
		dieRollLabel = new JLabel();
		diePanel.add(dieRollLabel, BorderLayout.CENTER);
		
		buildCity = new JButton("Build City");
		buildCity.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (frame.getStage()== TurnStage.BUILD_CITY) {
					frame.setStage(TurnStage.BUILD);
					buildCity.setText("Build City");
				} else if (frame.getStage() == TurnStage.BUILD) {
					frame.setStage(TurnStage.BUILD_CITY);
					buildCity.setText("Cancle");
				}
			}
		});
		buildPanel.add(buildCity);
		
		buildTown = new JButton("Build Town");
		buildTown.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (frame.getStage() == TurnStage.BUILD_TOWN) {
					frame.setStage(TurnStage.BUILD);
					buildTown.setText("Build Town");
				} else if (frame.getStage() == TurnStage.BUILD) {
					frame.setStage(TurnStage.BUILD_TOWN);
					buildTown.setText("Cancle");
				}			
			}
		});
		buildPanel.add(buildTown);
		
		buildStreet = new JButton("Build Street");
		buildStreet.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (frame.getStage() == TurnStage.BUILD_STREET) {
					frame.setStage(TurnStage.BUILD);
					buildStreet.setText("Build Street");
				} else if (frame.getStage() == TurnStage.BUILD) {
					frame.setStage(TurnStage.BUILD_STREET);
					buildStreet.setText("Cancle");
				}			
			}
		});
		buildPanel.add(buildStreet);
		
		buyCard = new JButton("Buy Card");
		buyCard.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.playerDrawCard();
				
			}
		});
		buildPanel.add(buyCard);
		
		this.startButton = new JButton("Start Game");
		startPanel.add(startButton);
		startButton.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.startGame();
				tradePanel.initPlayerTrade(frame.getPlayer());
				//Here changes for the first turn.
				getCardLayout().show(actionButtonPanel, ROLL_TRADE_PANEL);
				changeActivateCardButton();
				fitBuildToPlayer();
			}
		});
		
		this.debugBoardTestButton = new JButton("Start BoardTests");
		startPanel.add(debugBoardTestButton);
		debugBoardTestButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.getBoard().setBoardDebugPhase(true);;
			}
		});
		
		this.debugChangePlayerButton = new JButton("Change Active Debug");
		startPanel.add(debugChangePlayerButton);
		debugChangePlayerButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.getBoard().changeActiveDebugPlayer();
			}
		});
		
		debugPlaceTargetCity = new JButton("Placing Target");
		startPanel.add(debugPlaceTargetCity);
		debugPlaceTargetCity.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.getBoard().setDebugPlacingTargetCity();
			}
		});
		
		controlInforPanel = new ControllInfoBoxPanel(frame);
		this.add(controlInforPanel, BorderLayout.CENTER);

	}
    /**
 	 * JustForTesting, will later be changed for the first turn
 	 */
	private void changeStartToDieRolle() {
		this.remove(startPanel);
		this.add(diePanel, BorderLayout.CENTER);
		
		this.validate();
	}
	
	public void changeTurnEndButtonEnable() {
		turnEndButton.setEnabled(!turnEndButton.isEnabled());
	}
	
	private void changeActivateCardButton() {
		activateCardButton.setEnabled(!activateCardButton.isEnabled());
	}
	
	public void changeTurnEndButton() {
		turnEndButton.setText("End Turn");
		getCardLayout().show(actionButtonPanel, BUILD_PANEL);
	}
	
	public void fitBuildToPlayer() {
		buildCity.setEnabled(frame.getActivePlayer().canBuildCity());
		buildTown.setEnabled(frame.getActivePlayer().canBuildTown());
		buildStreet.setEnabled(frame.getActivePlayer().canBuildStreet());
		buyCard.setEnabled(frame.getActivePlayer().canBuyCard());
	}
	
	public void rollDice() {
		dieRollLabel.setText(Integer.toString(frame.rollDice()));
		changeTurnEndButtonEnable();
		dieRollButton.setEnabled(false);
	}
	
	public boolean getRolledDie() {
		return !dieRollButton.isEnabled();
	}
	
	public void rollDiceToTrade() {
		getCardLayout().show(actionButtonPanel, TRADE_PANEL);
	}
	
	public MainFrame getMainFrame() {
		return this.frame;
	}
	
	public void tradeToBuild () {
		this.remove(startPanel);
		this.add(buildPanel, BorderLayout.EAST);
		frame.repaint();
	}
	
	public CardLayout getCardLayout() {
		return (CardLayout) actionButtonPanel.getLayout();
	}
	
	private void resetBuildButton() {
		buildCity.setText("Build City");
		buildStreet.setText("Build Street");
		buildTown.setText("Build Town");
	}
	
	public void resetToStartOfTurn () {
		turnEndButton.setText("Next");
		changeTurnEndButtonEnable();
		dieRollButton.setText("Roll dice");
		dieRollButton.setEnabled(true);
		dieRollLabel.setText("");
		getCardLayout().show(actionButtonPanel, ROLL_TRADE_PANEL);
		fitBuildToPlayer();
	}
	
	public ControllInfoBoxPanel getControlInforPanel() {
		return controlInforPanel;
	}
	
	public TradePanel getTradePanel() {
		return tradePanel;
	}
	
	@Override
	public void boardEventHappened(BoardEvent event) {
		fitBuildToPlayer();
		resetBuildButton();
		if (event.getEventType() == BoardEvents.TOWN_PLACED && (frame.getStage() == TurnStage.FIRST_TURN || frame.getStage() == TurnStage.SECOND_TURN )) {
			updateTurnInfo();
		}
	}
	
	public void updateTurnInfo() {
		String tempTextString = "";
		if (frame.getStage() == TurnStage.START_GAME || frame.getStage() == TurnStage.FIRST_TURN || frame.getStage() == TurnStage.SECOND_TURN ) {
			tempTextString = frame.getActivePlayer().getColor().toString() + (frame.getBuildStartTown() ? " Build Street" : " Build Town");
		} else {
			tempTextString =  frame.getActivePlayer().getColor().toString() + " Turn Stage: " + frame.getStage();
		}
		controlInforPanel.setInfoText(tempTextString);
	}
}
