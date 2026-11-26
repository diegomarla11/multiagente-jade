package behaviours;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.Scanner;
import utils.Utils;

public class ClientBehaviour extends CyclicBehaviour {
    private static final String[] QUESTIONS = {
        "Do you feel nervous, anxious or on edge?",
        "Do you feel that you are unable to stop or control worrying?",
        "Do you worry too much about different things?",
        "Do you have trouble relaxing?",
        "Do you have trouble concentrating on things, such as reading the newspaper or watching television?",
        "Do you become easily annoyed or irritable?",
        "Do you feel afraid as if something awful might happen?",
        "Do you feel that you are going crazy?",
        "Do you feel that you might pass out?",
        "Do you feel that you are trapped or caught?"
    };
    private final Scanner input = new Scanner(System.in);
    private AID service;

    public ClientBehaviour(Agent agent) {
        super(agent);
    }

    public void action() {
        if (service == null) {
            AID[] services = Utils.searchAgents(myAgent, "PsycometricService");
            if (services == null || services.length == 0) {
                block(200);
                return;
            }
            service = services[0];
            boolean sample = myAgent.getArguments() != null && myAgent.getArguments().length > 0
                && "demo".equals(myAgent.getArguments()[0]);
            String content = sample ? "3/37001" : survey();
            if (content == null) {
                myAgent.doDelete();
                return;
            }
            ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
            request.addReceiver(service);
            request.setConversationId(myAgent.getName());
            request.setContent(content);
            myAgent.send(request);
            return;
        }

        ACLMessage reply = myAgent.receive(MessageTemplate.MatchConversationId(myAgent.getName()));
        if (reply == null) {
            block();
            return;
        }
        System.out.println(reply.getContent());
        myAgent.doDelete();
    }

    private String survey() {
        System.out.println("Fictional exercise. Enter a sample five-digit postal code:");
        String code;
        do {
            if (!input.hasNextLine()) return null;
            code = input.nextLine().strip();
            if (!code.matches("[0-9]{5}")) System.out.println("Use five digits, for example 37001.");
        } while (!code.matches("[0-9]{5}"));

        int score = 0;
        for (int i = 0; i < QUESTIONS.length; i++) {
            System.out.println((i + 1) + ". " + QUESTIONS[i] + " (y/n)");
            while (true) {
                if (!input.hasNextLine()) return null;
                String answer = input.nextLine().strip();
                if ("y".equalsIgnoreCase(answer)) {
                    score++;
                    break;
                }
                if ("n".equalsIgnoreCase(answer)) break;
                System.out.println("Enter y or n.");
            }
        }
        return score + "/" + code;
    }
}
