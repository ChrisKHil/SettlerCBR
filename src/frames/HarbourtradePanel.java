package frames;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Util.TradingUtils;
import enums.LandType;

public class HarbourtradePanel extends JPanel {
	
	private JTextField tradeinField;
	
	private JComboBox<LandType> tradeOptionBox;
	
	private JComboBox<LandType> tradeOfferBox;

	private JButton tradeInButton;
	
	private TradePanel tradePanel;
	
	public HarbourtradePanel(TradePanel tradePanel) {
		this.setLayout(new FlowLayout());
		this.tradePanel = tradePanel;
		
		tradeinField = new JTextField();
		this.add(tradeinField);
 		
		tradeOfferBox = new JComboBox<LandType>(TradingUtils.getTypes());
		this.add(tradeOfferBox);
		
		tradeOptionBox = new JComboBox<LandType>(TradingUtils.getTypes());
		this.add(tradeOptionBox);
		
		tradeInButton = new JButton("Trade");
		tradeInButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				TradingUtils.tradeWithBank(tradePanel.getFrame().getActivePlayer(),(LandType)tradeOfferBox.getSelectedItem(),
						(LandType)tradeOptionBox.getSelectedItem(), tradePanel.getSelectedTradingOption());
				tradePanel.getFrame().updatePlayerPanels();
			}
		});
		this.add(tradeInButton);
	}
	
	public LandType[] getSelectedLands() {
		LandType[] selected = new LandType[2];
		selected[0] = (LandType)tradeOfferBox.getSelectedItem();
		selected[1] = (LandType)tradeOptionBox.getSelectedItem();
		return selected;
	}
	
	public void updateTradeOffers() {
		DefaultComboBoxModel<LandType> types = new DefaultComboBoxModel<LandType>();
		for (LandType l : TradingUtils.getPossibleTradeOffers(tradePanel.getFrame().getActivePlayer(),tradePanel.getSelectedTradingOption())) {
			types.addElement(l);
		}
		tradeOfferBox.setModel(types);
		updateTradePicks();
	}
	
	public void updateTradePicks() {
		DefaultComboBoxModel<LandType> types = new DefaultComboBoxModel<LandType>();
		for (LandType l : TradingUtils.getPossibleTradePicks(tradePanel.getSelectedTradingOption())) {
			types.addElement(l);
		}
		tradeOptionBox.setModel(types);
	}
}
