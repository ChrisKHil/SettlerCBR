package dialogue;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import Util.ColorUtils;
import player.Player;

public class ComboxRendererPlayer extends DefaultListCellRenderer {
	
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		if (value instanceof Player) {
			value = ColorUtils.colorToString(((Player)value).getColor());
		}
		
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		return this;
	}
}
