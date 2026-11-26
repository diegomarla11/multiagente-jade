package behaviours;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import utils.LocationLookup;

public class GptBehaviour extends OneShotBehaviour {
    private final String postalCode;
    private final AID manager;

    public GptBehaviour(Agent agent, String postalCode, AID manager) {
        super(agent);
        this.postalCode = postalCode;
        this.manager = manager;
    }

    public void action() {
        ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
        reply.addReceiver(manager);
        reply.setContent(LocationLookup.forPostalCode(postalCode));
        myAgent.send(reply);
        myAgent.doDelete();
    }
}
