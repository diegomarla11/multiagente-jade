package behaviours;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.StaleProxyException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PsycometricServiceBehaviour extends CyclicBehaviour {
    private final Map<String, ACLMessage> pending = new HashMap<>();

    public PsycometricServiceBehaviour(Agent agent) {
        super(agent);
    }

    public void action() {
        ACLMessage message = myAgent.receive();
        if (message == null) {
            block();
            return;
        }

        if (message.getPerformative() == ACLMessage.REQUEST) {
            ACLMessage reply = message.createReply();
            if (message.getContent() == null || !message.getContent().matches("(10|[0-9])/[0-9]{5}")) {
                reply.setPerformative(ACLMessage.REFUSE);
                reply.setContent("Expected a score from 0 to 10 and a five-digit postal code.");
                myAgent.send(reply);
                return;
            }
            String manager = "Manager-" + UUID.randomUUID();
            try {
                myAgent.getContainerController().createNewAgent(manager, "agents.Manager",
                    new Object[] {myAgent.getAID(), message.getContent()}).start();
                pending.put(manager, reply);
            } catch (StaleProxyException e) {
                reply.setPerformative(ACLMessage.FAILURE);
                reply.setContent("Could not start the manager.");
                myAgent.send(reply);
            }
        } else {
            ACLMessage reply = pending.remove(message.getSender().getLocalName());
            if (reply != null) {
                reply.setPerformative(message.getPerformative());
                reply.setContent(message.getContent());
                myAgent.send(reply);
            }
        }
    }
}
