package dialogue;

import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import Util.ColorUtils;
import Util.Trade;
import Util.TradingUtils;
import enums.LandType;
import frames.MainFrame;
import listener.DialogueClosingListener;
import listener.TradeAcceptRecive;
import listener.TradeCancleListener;
import listener.TradeDeniedRecive;
import player.Player;

public class TradeDialogue extends TradingCardDialogue {
	
	private JPanel mainPanel;
	
	private JLabel tradingLabel;
	
	private ResourcePickerComboboxes requests;
	
	private int[] requestArray;
	
	private ResourcePickerComboboxes offers;

	private int[] offerArray;
	
	private JLabel offerLabel;
	
	private JLabel requestLabel;
	
	private JPanel buttonPanel;
	
	private JButton closingButton;
	
	private boolean cancled = false;
	
	private boolean accepted = false;
	
	private JButton cancleButton;
	
	private Player reciver;
	
	private MainFrame frame;
	
	private DialogueClosingListener closingListenerOffer;
	
	private TradeCancleListener tradeCancleOffer;
	
	public TradeDialogue(MainFrame frame, boolean modal, Player player) {
		super(frame, modal);
		this.reciver = player;
		this.frame = frame;
		this.setTitle(ColorUtils.colorToString(frame.getActivePlayer().getColor()));
		
		init();
		setValues(frame.getActivePlayer());
		
		this.pack();
	}
	
	public TradeDialogue(MainFrame frame, boolean modal, Player player, Trade trade) {
		super(frame, modal);
		this.reciver = player;
		this.frame = frame;
		init();
		setWithTradeObject(trade);
		
		this.pack();
	}
	
	private void init() {

		mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(0,1));
		this.add(mainPanel);
		
		tradingLabel = new JLabel("");
	
		mainPanel.add(tradingLabel);
		
		requestLabel = new JLabel("Request");
		mainPanel.add(requestLabel);
		
		requests = new ResourcePickerComboboxes();
		mainPanel.add(requests);
		
		offerLabel = new JLabel("Offers");
		mainPanel.add(offerLabel);
		
		offers = new ResourcePickerComboboxes();
		mainPanel.add(offers);
		
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		mainPanel.add(buttonPanel);
		
		closingButton = new JButton("Offer");
		buttonPanel.add(closingButton);
		
		cancleButton = new JButton("Cancle");
		buttonPanel.add(cancleButton);	
		
		setRecivedStatus(false);
	}

	private void setValues (Player player) {
		offers.setBoxesByPlayer(player);
	}
	
	private void setWithTradeObject(Trade trade) {
		
		offers.setValuesByTradeObject(trade.getOfferd());
		requests.setValuesByTradeObject(trade.getRequested());
	}
	
	@Override
	public void setPick() {
		requestArray = requests.getRessourceListAsArray();
		offerArray = offers.getRessourceListAsArray();
	}

	public void setCancled(boolean cancled) {
		this.cancled = cancled;
	}
	
	public boolean getCancled() {
		return cancled;
	}
	
	public int[] getRequestArray() {
		return requestArray;
	}
	
	public int[] getOfferArray() {
		return offerArray;
	}
	
	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}
	
	public boolean getAccepted(){
		return accepted;
	}
	
	public void checkIfTradeViable() {
		int[] requestedArray = requests.getRessourceListAsArray();
		int counter = 0;
		boolean canBeAccepted = true;
		for (LandType t : TradingUtils.getTypes()) {
			System.out.println("		>We Requested: " + requestedArray[counter] + " and Playergot: " + reciver.getRessourceByEnum(t));
			if (reciver.getRessourceByEnum(t) < requestedArray[counter])  {
				canBeAccepted = false;
			}
			counter++;
		}
		closingButton.setEnabled(canBeAccepted);
	}
	
	public void setRecivedStatus(boolean recived) {
		String label = "Offer to " + ColorUtils.colorToString(reciver.getColor());
		if (recived) {
			label = "Recived trade from " +  ColorUtils.colorToString(frame.getActivePlayer().getColor());
			closingButton.removeActionListener(closingListenerOffer);
			closingButton.addActionListener(new TradeAcceptRecive(this));
			cancleButton.removeActionListener(tradeCancleOffer);
			cancleButton.addActionListener(new TradeDeniedRecive(this));
		} else {
			closingListenerOffer = new DialogueClosingListener(this);
			closingButton.addActionListener(closingListenerOffer);
			tradeCancleOffer = new TradeCancleListener(this);
			cancleButton.addActionListener(tradeCancleOffer);
		}
		tradingLabel.setText(label);
		offers.setEnabledForBoxes(!recived);
		requests.setEnabledForBoxes(!recived);
	}
}