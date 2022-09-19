package frames;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import enums.HarbourType;
import player.Player;

public class TradePanel extends JPanel{

	private JPanel harbourtrades;
	
	private JPanel playerTrades;
	
	private JComboBox<HarbourType> tradeOptions;
	
	private HarbourtradePanel harbourTradePanel;
	
	private List<PlayerTradeButton> playerTradeButtons;
	
	private MainFrame frame;
	
	public TradePanel(MainFrame frame) {
		this.frame = frame;
		this.setLayout(new GridLayout(1,0));
		playerTradeButtons = new ArrayList<PlayerTradeButton>();
		initHarbourTrades();
	}
	
	public void initPlayerTrade(List<Player> player) {
		playerTrades = new JPanel();
		playerTrades.setLayout(new GridLayout(0,1));
		for (Player p : player) {
			PlayerTradeButton temp = new PlayerTradeButton(p);
			temp.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					frame.createTrade(p, null);
				}
			});
			playerTrades.add(temp);
			playerTradeButtons.add(temp);
		}
		this.add(playerTrades);
	}
	
	private void initHarbourTrades () {
		harbourtrades = new JPanel();
		this.add(harbourtrades);
		
		tradeOptions = new JComboBox<HarbourType>();
		tradeOptions.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				harbourTradePanel.updateTradeOffers();
				updateHarbourTrades(frame.getActivePlayer());
			}
		});
		harbourtrades.add(tradeOptions);
		harbourTradePanel = new HarbourtradePanel(this);
		harbourtrades.add(harbourTradePanel);
	}
	
	public void updateHarbourTrades(Player player) {
		DefaultComboBoxModel<HarbourType> types = new DefaultComboBoxModel<HarbourType>();
		System.out.println("	>The Player " + player.getColor().toString() + " got " + player.getHarbours().size() + " harbourtypes");
		for (HarbourType t : player.getHarbours()) {
			types.addElement(t);
		}
		tradeOptions.setModel(types);
		harbourTradePanel.updateTradeOffers();
	}
	
	public void updatePlayerTrades () {
		for (PlayerTradeButton p : playerTradeButtons) {
			if (p.getPlayer().equals(frame.getActivePlayer())) {
				p.setEnabled(false);
				System.out.println("This shgould only hit once");
			} else {
				p.setEnabled(true);
			}
		}
	}
	
	public HarbourType getSelectedTradingOption() {
		return (HarbourType)tradeOptions.getSelectedItem();
	}
	
	public MainFrame getFrame() {
		return frame;
	}
}
