package behaviours;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class PsycometricAgentBehaviour extends OneShotBehaviour {
    private final String score;
    private final AID manager;

    public PsycometricAgentBehaviour(Agent agent, String score, AID manager) {
        super(agent);
        this.score = score;
        this.manager = manager;
    }

    public void action() {
        ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
        reply.addReceiver(manager);
        reply.setContent("Affirmative answers: " + score + "/10 (academic simulation).");
        myAgent.send(reply);
        myAgent.doDelete();
    }
}
