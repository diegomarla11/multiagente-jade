package agents;

import behaviours.ClientBehaviour;
import jade.core.Agent;

public class Client extends Agent{
	
	protected void setup() {
		
		System.out.println("Client-agent "+getAID().getName()+" is ready.");
		addBehaviour(new ClientBehaviour(this));

	}
	
	protected void takeDown() {
		System.out.println("Client-agent "+getAID().getName()+" terminating.");
		if (getArguments() != null && getArguments().length == 2 && getArguments()[1] instanceof java.util.concurrent.CountDownLatch) {
			((java.util.concurrent.CountDownLatch) getArguments()[1]).countDown();
		}
	}

}
