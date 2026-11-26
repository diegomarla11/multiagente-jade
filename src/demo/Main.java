package demo;

import java.util.concurrent.CountDownLatch;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;

public class Main {
    public static void main(String[] args) throws Exception {
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.LOCAL_HOST, "127.0.0.1");
        profile.setParameter(Profile.MAIN_HOST, "127.0.0.1");
        profile.setParameter("icps", LocalTransport.class.getName());
        profile.setParameter(Profile.NO_MTP, "true");
        profile.setParameter(Profile.DETECT_MAIN, "false");
        profile.setParameter(Profile.PLATFORM_ID, "portfolio-local");
        Runtime runtime = Runtime.instance();
        AgentContainer container = runtime.createMainContainer(profile);
        if (container == null) throw new IllegalStateException("JADE could not start its local container.");
        runtime.setCloseVM(true);
        CountDownLatch finished = new CountDownLatch(1);
        container.createNewAgent("PsycometricService", "agents.PsycometricService", null).start();
        String mode = args.length == 0 ? "demo" : args[0];
        container.createNewAgent("Client", "agents.Client", new Object[] {mode, finished}).start();
        finished.await();
        container.kill();
    }
}
