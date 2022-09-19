package agents;

import agents.AgentActionSettler;
import jade.util.leap.Serializable;

/**
 * This class shall be passed empty to the agent and be evaluated after the return.
 */
public class AgentMessage implements Serializable{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7619462087448972595L;
	/**
	 * Name of the reciving Agent
	 */
	private String reciver;
	
	/**
	 * The action the agent want to execute
	 */
	private AgentActionSettler action;
	/**
	 * This will contain values where needed like Arrays for Tradeoffers
	 */
	private Object data;
	
	public AgentMessage(String reciver) {
		this.reciver = reciver;
	}
	
	public AgentMessage(String reciver, AgentActionSettler action) {
		this.reciver = reciver;
		this.action = action;
	}
	
	public void setAction(AgentActionSettler action) {
		this.action = action;
	}
	
	public AgentActionSettler getAction() {
		return action;
	}
	
	public Object getData() {
		return data;
	}
	
	public void setData(Object data) {
		this.data = data;
	}
	
	public String getReciver() {
		return reciver;
	}
	
	public void setReciver (String reciver) {
		this.reciver = reciver;
	}
}
