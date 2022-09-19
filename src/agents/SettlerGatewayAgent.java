package agents;

import java.io.IOException;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.wrapper.gateway.GatewayAgent;

public class SettlerGatewayAgent extends GatewayAgent {

	private AgentMessage message;
	
	@Override
	protected void setup() {
		super.setup();
		addBehaviour(new CyclicBehaviour() {
			@Override
			public void action() {
				ACLMessage aclmsg = receive();
					System.out.println("Message: " + (aclmsg != null) + " nonACL:" + message);
					if(aclmsg != null && message != null) {					
						try {
							if (aclmsg.getContentObject() != null) {
								message.setAction(((AgentMessage)aclmsg.getContentObject()).getAction());
								message.setData(((AgentMessage)aclmsg.getContentObject()).getData());	
							}
						} catch (UnreadableException e) {
							e.printStackTrace();
						}
						releaseCommand(message);
					} else {
						block();
					}
			}
		});
	}
	
	@Override
	protected void processCommand(Object arg0) {
		if (arg0 != null && arg0 instanceof AgentMessage) {
			this.message = (AgentMessage) arg0;
			//Should send messages to all relevant agents and still work for a single one
			ACLMessage aclmsg = new ACLMessage(ACLMessage.PROPAGATE);
			//List can not be empty, assuming at least one receiver every time thus save
			aclmsg.addReceiver(new AID(message.getReciver(), AID.ISLOCALNAME));
			try {
				aclmsg.setContentObject(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
			send(aclmsg);
		}
	}
}
