package Util;

import enums.BoardEvents;

public class BoardEvent {
	
	private BoardEvents eventType;
	/**
	 * Maybe null, not needed for every event
	 */
	private Object eventSource;
	
	public BoardEvent(BoardEvents eventType) {
		this.eventType = eventType;
	}
	
	public BoardEvent(BoardEvents eventType, Object eventSource) {
		this.eventType = eventType;
		this.eventSource = eventSource;
	}

	public Object getEventSource() {
		return eventSource;
	}
	
	public void setEventSource(Object eventSource) {
		this.eventSource = eventSource;
	}
	
	public BoardEvents getEventType() {
		return eventType;
	}
}
