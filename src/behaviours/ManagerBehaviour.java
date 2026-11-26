package behaviours;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.StaleProxyException;

public class ManagerBehaviour extends Behaviour {
    private final AID service;
    private final String request;
    private boolean started;
    private boolean finished;
    private String score;
    private String location;

    public ManagerBehaviour(Agent agent, AID service, String request) {
        super(agent);
        this.service = service;
        this.request = request;
    }

    public void action() {
        String scorer = "Score-" + myAgent.getLocalName();
        String locator = "Location-" + myAgent.getLocalName();
        if (!started) {
            started = true;
            String[] parts = request.split("/", 2);
            try {
                myAgent.getContainerController().createNewAgent(scorer, "agents.PsycometricAgent",
                    new Object[] {parts[0], myAgent.getAID()}).start();
                myAgent.getContainerController().createNewAgent(locator, "agents.GptAgent",
                    new Object[] {parts[1], myAgent.getAID()}).start();
            } catch (StaleProxyException e) {
                finish(ACLMessage.FAILURE, "Could not start the agents for this case.");
            }
            return;
        }

        ACLMessage message = myAgent.receive();
        if (message == null) {
            block();
            return;
        }
        String sender = message.getSender().getLocalName();
        if (sender.equals(scorer)) score = message.getContent();
        if (sender.equals(locator)) location = message.getContent();
        if (score != null && location != null) {
            finish(ACLMessage.INFORM, score + "\n" + location);
        }
    }

    private void finish(int performative, String content) {
        ACLMessage reply = new ACLMessage(performative);
        reply.addReceiver(service);
        reply.setContent(content);
        myAgent.send(reply);
        finished = true;
        myAgent.doDelete();
    }

    public boolean done() {
        return finished;
    }
}
