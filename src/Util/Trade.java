package Util;

import jade.util.leap.Serializable;

public class Trade implements Serializable {

	/**
	 * {LandType.CLAY, LandType.CORN, LandType.LUMBER, LandType.STONE, LandType.WHOOL}
	 */
	private int[] requested;
	/**
	 *  {LandType.CLAY, LandType.CORN, LandType.LUMBER, LandType.STONE, LandType.WHOOL}
	 */
	private int[] offerd;
	
	private String sender;
	
	private String reciver;
	/**
	 * Set to true if the reciver updates the offerarray.
	 */
	boolean isCounterOffer;
	
	boolean accepted;
	
	/**
	 *  {LandType.CLAY, LandType.CORN, LandType.LUMBER, LandType.STONE, LandType.WHOOL}
	 * @param requested a combination of values representing the requested resources
	 * @param offered a combination of values representing the offered resources
	 */
	public Trade(String sender, String reciver, int[] requested, int[] offered) {
		this.sender = sender;
		this.reciver = reciver;
		this.requested = requested;
		this.offerd = offered;
		isCounterOffer = false;
		accepted = false;
	}
	
	public boolean isAccepted() {
		return accepted;
	}
	
	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}
	
	public String getReciver() {
		return reciver;
	}
	
	public int[] getRequested() {
		return requested;
	}
	
	public int[] getOfferd() {
		return offerd;
	}
	public String getSender() {
		return sender;
	}
	public void setOfferd(int[] offerd) {
		this.offerd = offerd;
	}
	
	public void setReciver(String reciver) {
		this.reciver = reciver;
	}
	
	public boolean isCounterOffer() {
		return isCounterOffer;
	}
	
	public void setCounterOffer(boolean isCounterOffer) {
		this.isCounterOffer = isCounterOffer;
	}
}
