package listener;

import Util.BoardEvent;

public interface BoardListener {
	
	public void boardEventHappened(BoardEvent event);
}
